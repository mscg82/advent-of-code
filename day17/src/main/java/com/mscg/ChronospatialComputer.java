package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public record ChronospatialComputer(List<Instruction> instructions, IntList binaryInstructions, LongList registers,
									int instructionPointer, LongList results)
{

	public static ChronospatialComputer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = new ArrayList<>();
			int[] binaryInstructions = null;
			final long[] registers = new long[3];
			for (final String line : StreamUtils.iterate(in.lines().filter(not(String::isBlank)))) {
				final var parts = line.split(": ");
				if (parts.length != 2) {
					throw new IllegalArgumentException("invalid line: " + line);
				}
				switch (parts[0]) {
					case "Register A" -> registers[Register.A.ordinal()] = Long.parseLong(parts[1].trim());
					case "Register B" -> registers[Register.B.ordinal()] = Long.parseLong(parts[1].trim());
					case "Register C" -> registers[Register.C.ordinal()] = Long.parseLong(parts[1].trim());
					case "Program" -> {
						binaryInstructions = Arrays.stream(parts[1].trim().split(",")) //
								.mapToInt(Integer::parseInt) //
								.toArray();
						final Operation[] allOperations = Operation.values();
						for (int i = 0; i < binaryInstructions.length; i += 2) {
							final Operation operation = allOperations[binaryInstructions[i]];
							instructions.add(operation);
							instructions.add(operation.operand(binaryInstructions[i + 1]));
						}
					}
					default -> throw new IllegalArgumentException("invalid line: " + line);
				}
			}
			return new ChronospatialComputer(List.copyOf(instructions), //
					IntList.of(Objects.requireNonNull(binaryInstructions)), //
					LongList.of(registers), 0, LongList.of());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public String executionOutputs()
	{
		final ChronospatialComputer runComputer = run();
		return runComputer.results().longStream() //
				.mapToObj(String::valueOf) //
				.collect(Collectors.joining(","));
	}

	public long findInputToMatchProgram()
	{
		final IntList expectedOutput = binaryInstructions;
		final var queue = new ArrayDeque<>((List.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L)));
		for (int i = 1, l = expectedOutput.size(); i <= l && !queue.isEmpty(); i++) {
			final long target = base8DigitsToNumber(expectedOutput, i);
			final var newValues = new LongArrayList();
			while (!queue.isEmpty()) {
				final long currentA = queue.poll();
				final var newRegisters = new LongArrayList(registers);
				Register.A.set(newRegisters, currentA);
				final var newComputer = new ChronospatialComputer(instructions, binaryInstructions,
						LongLists.unmodifiable(newRegisters), instructionPointer, results);
				final ChronospatialComputer runComputer = newComputer.run();
				final IntStream newResultsAsInts = runComputer.results().longStream() //
						.mapToInt(v -> (int) v);
				final IntList newResults = IntImmutableList.toList(newResultsAsInts);
				final long resultsAsNumber = base8DigitsToNumber(newResults, newResults.size());
				if (resultsAsNumber == target) {
					if (i < l) {
						for (int j = 0; j < 8; j++) {
							newValues.add(currentA * 8 + j);
						}
					} else {
						newValues.add(currentA);
					}
				}
			}
			queue.addAll(newValues);
		}
		return queue.stream() //
				.mapToLong(Long::longValue) //
				.min() //
				.orElseThrow(() -> new IllegalStateException("Unable to find suitable input"));
	}

	@Override
	public String toString()
	{
		final var sw = new StringWriter();
		final var out = new PrintWriter(sw);
		out.println("A: %d [%s]".formatted(Register.A.get(registers), Long.toString(Register.A.get(registers), 8)));
		out.println("B: %d [%s]".formatted(Register.B.get(registers), Long.toString(Register.B.get(registers), 8)));
		out.println("C: %d [%s]".formatted(Register.C.get(registers), Long.toString(Register.C.get(registers), 8)));
		out.println();
		for (int i = 0; i < instructions.size(); i += 2) {
			final var operation = (Operation) instructions.get(i);
			final var operand = (Operand) instructions.get(i + 1);
			final var operandStr = switch (operand) {
				case LiteralOperand(final int value) -> String.valueOf(value);
				case RegisterOperand(final Register register) -> "[" + register.name() + "]";
			};
			out.println("%s %s %s".formatted((i == instructionPointer ? ">" : " "), operation.name(), operandStr));
		}
		out.print("Program: " + binaryInstructions);

		return sw.toString();
	}

	private ChronospatialComputer run()
	{
		ChronospatialComputer current = this;
		while (true) {
			final Optional<ChronospatialComputer> next = current.executeInstruction();
			if (next.isEmpty()) {
				break;
			}
			current = next.get();
		}
		return current;
	}

	private Optional<ChronospatialComputer> executeInstruction()
	{
		if (instructionPointer >= instructions.size() - 1) {
			return Optional.empty();
		}

		if (!(instructions.get(instructionPointer) instanceof final Operation operation)) {
			throw new IllegalArgumentException(
					"Invalid instruction pointer value: " + instructionPointer + ". Expected operation.");
		}
		if (!(instructions.get(instructionPointer + 1) instanceof final Operand operand)) {
			throw new IllegalArgumentException("Invalid instruction pointer value: " + instructionPointer + ". Expected operand.");
		}

		final LongList newRegisters = new LongArrayList(registers);
		final LongList newResults = new LongArrayList(results);
		final int newInstructionPointer = switch (operation) {
			case ADV -> {
				final long numerator = Register.A.get(newRegisters);
				final long denominator = pow2(operand.getValue(newRegisters));
				Register.A.set(newRegisters, numerator / denominator);
				yield instructionPointer + 2;
			}

			case BXL -> {
				final long b = Register.B.get(newRegisters);
				final long value = operand.getValue(newRegisters);
				final long xor = b ^ value;
				Register.B.set(newRegisters, xor);
				yield instructionPointer + 2;
			}

			case BST -> {
				final long value = operand.getValue(newRegisters);
				Register.B.set(newRegisters, value % 8);
				yield instructionPointer + 2;
			}

			case JNZ -> {
				final long value = operand.getValue(newRegisters);
				final long a = Register.A.get(newRegisters);
				yield a == 0 ? instructionPointer + 2 : (int) value;
			}

			case BXC -> {
				final long b = Register.B.get(newRegisters);
				final long c = Register.C.get(newRegisters);
				Register.B.set(newRegisters, b ^ c);
				yield instructionPointer + 2;
			}

			case OUT -> {
				newResults.add(operand.getValue(newRegisters) % 8);
				yield instructionPointer + 2;
			}

			case BDV -> {
				final long numerator = Register.A.get(newRegisters);
				final long denominator = pow2(operand.getValue(newRegisters));
				Register.B.set(newRegisters, numerator / denominator);
				yield instructionPointer + 2;
			}

			case CDV -> {
				final long numerator = Register.A.get(newRegisters);
				final long denominator = pow2(operand.getValue(newRegisters));
				Register.C.set(newRegisters, numerator / denominator);
				yield instructionPointer + 2;
			}
		};

		return Optional.of(new ChronospatialComputer(instructions, binaryInstructions, LongLists.unmodifiable(newRegisters),
				newInstructionPointer, LongLists.unmodifiable(newResults)));
	}

	private static long pow2(final long exponent)
	{
		long result = 1;
		for (long i = 1; i <= exponent; i++) {
			result = result * 2;
		}
		return result;
	}

	private static long base8DigitsToNumber(final IntList digits, final int maxDigits)
	{
		long value = 0;
		final int l = digits.size();
		for (int i = l - maxDigits; i < l; i++) {
			final int digit = digits.getInt(i);
			value = value * 8 + digit;
		}
		return value;
	}

	public sealed interface Instruction permits Operand, Operation {}

	public sealed interface Operand extends Instruction permits LiteralOperand, RegisterOperand
	{
		static Operand asCombo(final int value)
		{
			return switch (value) {
				case 0, 1, 2, 3 -> new LiteralOperand(value);
				case 4 -> new RegisterOperand(Register.A);
				case 5 -> new RegisterOperand(Register.B);
				case 6 -> new RegisterOperand(Register.C);
				case 7 -> throw new IllegalArgumentException("Operand value 7 is reserved");
				default -> throw new IllegalArgumentException("Unknown operand value " + value);
			};
		}

		default long getValue(final LongList registers)
		{
			return switch (this) {
				case LiteralOperand(final int v) -> v;
				case RegisterOperand(final Register r) -> r.get(registers);
			};
		}
	}

	public record LiteralOperand(int value) implements Operand {}

	public record RegisterOperand(Register register) implements Operand {}

	public enum Operation implements Instruction
	{
		ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV;

		public Operand operand(final int value)
		{
			return switch (this) {
				case ADV, BST, OUT, BDV, CDV -> Operand.asCombo(value);
				case BXL, JNZ, BXC -> new LiteralOperand(value);
			};
		}
	}

	public enum Register
	{
		A, B, C;

		public void set(final LongList registers, final long value)
		{
			registers.set(ordinal(), value);
		}

		public long get(final LongList registers)
		{
			return registers.getLong(ordinal());
		}
	}

}
