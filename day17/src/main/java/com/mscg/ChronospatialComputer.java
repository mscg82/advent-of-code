package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public record ChronospatialComputer(List<Instruction> instructions, LongList registers, int instructionPointer, LongList results)
{

	public static ChronospatialComputer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = new ArrayList<>();
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
						final int[] binaryInstructions = Arrays.stream(parts[1].trim().split(",")) //
								.mapToInt(Integer::parseInt) //
								.toArray();
						final Operation[] allOperations = Operation.values();
						for (int i = 0; i < binaryInstructions.length; i++) {
							final int binaryInstruction = binaryInstructions[i];
							if (i % 2 == 0) {
								instructions.add(allOperations[binaryInstruction]);
							} else {
								instructions.add(new LiteralOperand(binaryInstruction));
							}
						}
					}
					default -> throw new IllegalArgumentException("invalid line: " + line);
				}
			}
			return new ChronospatialComputer(List.copyOf(instructions), LongList.of(registers), 0, LongList.of());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public String executionOutputs()
	{
		var current = this;
		while (true) {
			final var next = current.executeInstruction();
			if (next.isEmpty()) {
				break;
			}
			current = next.get();
		}
		return current.results.longStream() //
				.mapToObj(String::valueOf) //
				.collect(Collectors.joining(","));
	}

	Optional<ChronospatialComputer> executeInstruction()
	{
		if (instructionPointer >= instructions.size() - 1) {
			return Optional.empty();
		}

		if (!(instructions.get(instructionPointer) instanceof final Operation operation)) {
			throw new IllegalArgumentException(
					"Invalid instruction pointer value: " + instructionPointer + ". Expected operation.");
		}
		if (!(instructions.get(instructionPointer + 1) instanceof final LiteralOperand literalOperand)) {
			throw new IllegalArgumentException(
					"Invalid instruction pointer value: " + instructionPointer + ". Expected literal operand.");
		}

		final Operand operand = operation.operand(literalOperand);

		final LongList newRegisters = new LongArrayList(registers);
		final LongList newResults = new LongArrayList(results);
		final int newInstructionPointer = switch (operation) {
			case ADV -> {
				final long numerator = newRegisters.getLong(Register.A.ordinal());
				final long denominator = pow2(operand.getValue(newRegisters));
				newRegisters.set(Register.A.ordinal(), numerator / denominator);
				yield instructionPointer + 2;
			}

			case BXL -> {
				final long b = newRegisters.getLong(Register.B.ordinal());
				final long value = operand.getValue(newRegisters);
				final long xor = b ^ value;
				newRegisters.set(Register.B.ordinal(), xor);
				yield instructionPointer + 2;
			}

			case BST -> {
				final long value = operand.getValue(newRegisters);
				newRegisters.set(Register.B.ordinal(), value % 8);
				yield instructionPointer + 2;
			}

			case JNZ -> {
				final long value = operand.getValue(newRegisters);
				final long a = newRegisters.getLong(Register.A.ordinal());
				yield a == 0 ? instructionPointer + 2 : (int) value;
			}

			case BXC -> {
				final long b = newRegisters.getLong(Register.B.ordinal());
				final long c = newRegisters.getLong(Register.C.ordinal());
				newRegisters.set(Register.B.ordinal(), b ^ c);
				yield instructionPointer + 2;
			}

			case OUT -> {
				newResults.add(operand.getValue(newRegisters) % 8);
				yield instructionPointer + 2;
			}

			case BDV -> {
				final long numerator = newRegisters.getLong(Register.A.ordinal());
				final long denominator = pow2(operand.getValue(newRegisters));
				newRegisters.set(Register.B.ordinal(), numerator / denominator);
				yield instructionPointer + 2;
			}

			case CDV -> {
				final long numerator = newRegisters.getLong(Register.A.ordinal());
				final long denominator = pow2(operand.getValue(newRegisters));
				newRegisters.set(Register.C.ordinal(), numerator / denominator);
				yield instructionPointer + 2;
			}
		};

		return Optional.of(new ChronospatialComputer(instructions, LongLists.unmodifiable(newRegisters), newInstructionPointer,
				LongLists.unmodifiable(newResults)));
	}

	private long pow2(final long exponent)
	{
		long result = 1;
		for (long i = 1; i <= exponent; i++) {
			result = result * 2;
		}
		return result;
	}

	public sealed interface Instruction permits Operand, Operation {}

	public sealed interface Operand extends Instruction permits LiteralOperand, ComboOperand
	{
		default long getValue(final LongList registers)
		{
			return switch (this) {
				case LiteralOperand(final int v) -> v;
				case LiteralComboOperand(final int v) -> v;
				case RegisterOperand(final Register r) -> registers.getLong(r.ordinal());
			};
		}
	}

	public sealed interface ComboOperand extends Operand permits LiteralComboOperand, RegisterOperand {}

	public record LiteralOperand(int value) implements Operand
	{
		public ComboOperand toCombo()
		{
			return switch (value) {
				case 0, 1, 2, 3 -> new LiteralComboOperand(value);
				case 4 -> new RegisterOperand(Register.A);
				case 5 -> new RegisterOperand(Register.B);
				case 6 -> new RegisterOperand(Register.C);
				case 7 -> throw new IllegalArgumentException("Operand value 7 is reserved");
				default -> throw new IllegalArgumentException("Unknown operand value " + value);
			};
		}
	}

	public record LiteralComboOperand(int value) implements ComboOperand {}

	public record RegisterOperand(Register register) implements ComboOperand {}

	public enum Operation implements Instruction
	{
		ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV;

		public Operand operand(final LiteralOperand literal)
		{
			return switch (this) {
				case ADV, BST, BXC, OUT, BDV, CDV -> literal.toCombo();
				case BXL, JNZ -> literal;
			};
		}
	}

	public enum Register
	{
		A, B, C
	}

}
