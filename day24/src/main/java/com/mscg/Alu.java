package com.mscg;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.LongStream;

import static java.util.function.Predicate.not;

public record Alu(List<Instruction> instructions)
{

	public static Alu parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = in.lines() //
					.filter(line -> !line.startsWith("#")) //
					.map(Instruction::parse) //
					.toList();

			return new Alu(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findMaxModelNumber()
	{
		final List<Relation> knownRelations = computeRelationsBetweenDigits();

		final StringBuilder maxModelNumber = new StringBuilder("00000000000000");
		for (final Relation relation : knownRelations) {
			LongStream.range(0, 9) //
					.map(digit1 -> 9 - digit1) //
					.filter(digit1 -> setDigitIntoModelNumber(digit1, relation, maxModelNumber)) //
					.findFirst() //
					.orElseThrow(() -> new IllegalStateException("Can't find max model number"));
		}

		return Long.parseLong(maxModelNumber.toString());
	}

	public long findMinModelNumber()
	{
		final List<Relation> knownRelations = computeRelationsBetweenDigits();

		final StringBuilder minModelNumber = new StringBuilder("00000000000000");
		for (final Relation relation : knownRelations) {
			LongStream.range(0, 9) //
					.map(digit1 -> 1 + digit1) //
					.filter(digit1 -> setDigitIntoModelNumber(digit1, relation, minModelNumber)) //
					.findFirst() //
					.orElseThrow(() -> new IllegalStateException("Can't find max model number"));
		}

		return Long.parseLong(minModelNumber.toString());
	}

	private boolean setDigitIntoModelNumber(final long digit1, final Relation relation, final StringBuilder maxModelNumber)
	{
		final long digit2 = digit1 + relation.diff();
		if (digit2 >= 1 && digit2 <= 9) {
			maxModelNumber.setCharAt(relation.input1(), (char) (digit1 + '0'));
			maxModelNumber.setCharAt(relation.input2(), (char) (digit2 + '0'));
			return true;
		}
		return false;
	}

	private List<Relation> computeRelationsBetweenDigits()
	{
		final List<List<Instruction>> splitted = instructions.stream() //
				.collect(Collector.of(
						() -> ListSplitterAccumulator.<Instruction>splitOn(instruction -> instruction.opcode() == OpCode.INP), //
						ListSplitterAccumulator::add, //
						ListSplitterAccumulator::merge, //
						ListSplitterAccumulator::getParts));
		final var divZ1 = new Instruction(OpCode.DIV, Register.Z, new Value(1));

		record PushedInput(int index, long value) {}

		final Deque<PushedInput> queue = new ArrayDeque<>();

		final List<Relation> knownRelations = new ArrayList<>();

		for (int i = 0; i < splitted.size(); i++) {
			final List<Instruction> block = splitted.get(i);
			final boolean isPushBlock = block.stream().anyMatch(divZ1::equals);
			if (isPushBlock) {
				queue.add(new PushedInput(i, executePushBlock(block)));
			} else {
				final PushedInput pushedInput = queue.removeLast();
				final long offset = executePopBlock(block);
				knownRelations.add(new Relation(pushedInput.index(), i, pushedInput.value() + offset));
			}
		}

		return List.copyOf(knownRelations);
	}

	private long executePushBlock(final List<Instruction> instructions)
	{
		final var mulY0 = new Instruction(OpCode.MUL, Register.Y, new Value(0));
		final Instruction ins = instructions.stream() //
				.dropWhile(not(mulY0::equals)) // first mul y 0
				.skip(1) //
				.dropWhile(not(mulY0::equals)) // second mul y 0
				.skip(2) //
				.findFirst() //
				.orElseThrow();
		return Instruction.value(new long[0], ins.p2());
	}

	private long executePopBlock(final List<Instruction> instructions)
	{
		final var divZ26 = new Instruction(OpCode.DIV, Register.Z, new Value(26));
		final Instruction ins = instructions.stream() //
				.dropWhile(not(divZ26::equals)) //
				.skip(1) //
				.findFirst() //
				.orElseThrow();
		return Instruction.value(new long[0], ins.p2());
	}

	public sealed interface Parameter permits Register, Value {}

	public record Value(long value) implements Parameter
	{
		public static Optional<Value> from(final String value)
		{
			try {
				return Optional.of(new Value(Long.parseLong(value)));
			} catch (final NumberFormatException e) {
				return Optional.empty();
			}
		}
	}

	public record Instruction(@NonNull OpCode opcode, @NonNull Register p1, Parameter p2)
	{

		public static Instruction parse(final String line)
		{
			final var parts = line.split(" ");
			final OpCode opcode = OpCode.from(parts[0])
					.orElseThrow(() -> new IllegalArgumentException("Unsupported opcode " + parts[0]));
			final Register p1 = Register.from(parts[1])
					.orElseThrow(() -> new IllegalArgumentException("Unsupported register " + parts[1]));
			final Parameter p2 = switch (opcode) {
				case INP -> null;
				case ADD, MUL, DIV, MOD, EQL -> Register.from(parts[2]) //
						.map(Parameter.class::cast) //
						.or(() -> Value.from(parts[2])) //
						.orElseThrow(() -> new IllegalArgumentException("Unsupported value " + parts[2]));
			};

			return new Instruction(opcode, p1, p2);
		}

		private static long value(final long[] registers, final Parameter p)
		{
			return switch (p) {
				case Value v -> v.value();
				case Register r -> registers[r.ordinal()];
			};
		}

	}

	@RequiredArgsConstructor
	public static class ListSplitterAccumulator<T>
	{
		@Getter
		private final List<List<T>> parts = new ArrayList<>();

		private List<T> currentPart;

		private final @NonNull Predicate<T> splitCondition;

		public static <T> ListSplitterAccumulator<T> splitOn(@NonNull final Predicate<T> splitCondition)
		{
			return new ListSplitterAccumulator<>(splitCondition);
		}

		public void add(final T element)
		{
			if (splitCondition.test(element)) {
				currentPart = new ArrayList<>();
				parts.add(currentPart);
			}
			currentPart.add(element);
		}

		public ListSplitterAccumulator<T> merge(final ListSplitterAccumulator<T> other)
		{
			parts.addAll(other.parts);
			other.currentPart.forEach(this::add);
			return this;
		}
	}

	private record Relation(int input1, int input2, long diff) {}

	public enum OpCode
	{
		INP, ADD, MUL, DIV, MOD, EQL;

		public static Optional<OpCode> from(final String value)
		{
			return Optional.ofNullable(switch (value) {
				case "inp" -> INP;
				case "add" -> ADD;
				case "mul" -> MUL;
				case "div" -> DIV;
				case "mod" -> MOD;
				case "eql" -> EQL;
				default -> null;
			});
		}

	}

	public enum Register implements Parameter
	{
		X, Y, W, Z;

		public static Optional<Register> from(final String value)
		{
			return Optional.ofNullable(switch (value) {
				case "x" -> X;
				case "y" -> Y;
				case "w" -> W;
				case "z" -> Z;
				default -> null;
			});
		}
	}

}
