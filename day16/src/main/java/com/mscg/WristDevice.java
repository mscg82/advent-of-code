package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.RequiredArgsConstructor;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record WristDevice(List<Sample> samples, List<Instruction> instructions)
{

	@SuppressWarnings("java:S1301")
	public static WristDevice parseInput(final BufferedReader in) throws IOException
	{
		final List<String> allLines;
		try {
			allLines = in.lines().toList();
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		enum ParseState
		{
			SAMPLES, INSTRUCTIONS
		}

		final var samples = new ArrayList<Sample>();
		final var instructions = new ArrayList<Instruction>();
		final var iterator = allLines.iterator();
		var state = ParseState.SAMPLES;
		while (iterator.hasNext()) {
			switch (state) {
				case SAMPLES -> {
					final String line = iterator.next();
					if (line.isBlank()) {
						iterator.next(); // skip empty line
						state = ParseState.INSTRUCTIONS;
						continue;
					}

					final var before = RegistersSet.from(line);
					final var instruction = Instruction.from(iterator.next());
					final var after = RegistersSet.from(iterator.next());
					iterator.next(); // skip empty line
					samples.add(new Sample(before, instruction, after));
				}

				case INSTRUCTIONS -> {
					final var instruction = Instruction.from(iterator.next());
					instructions.add(instruction);
				}
			}
		}
		return new WristDevice(List.copyOf(samples), List.copyOf(instructions));
	}

	public long countSamplesWithMoreThan3Matches()
	{
		return samples.stream() //
				.filter(sample -> sample.findMatchingOpcodes().size() >= 3) //
				.count();
	}

	public long decodeAndExecute()
	{
		final var decodedOpcodes = decodeOpcodes();
		final RegistersSet result = Seq.seq(instructions.stream()) //
				.foldLeft(RegistersSet.init(), (regSet, instruction) -> {
					final var opcode = decodedOpcodes.get(instruction.opcode());
					return opcode.execute(instruction, regSet);
				});
		return result.v0();
	}

	private Map<Integer, Opcode> decodeOpcodes()
	{
		final var decodedOpcodes = new HashMap<Integer, Opcode>();
		final var unmatchedOpcodes = EnumSet.allOf(Opcode.class);

		record MatchedOpcode(int value, Opcode opcode) {}

		while (!unmatchedOpcodes.isEmpty()) {
			final Stream<Set<MatchedOpcode>> unknownMatchingOpcodes = samples.stream() //
					.map(sample -> sample.findMatchingOpcodes(unmatchedOpcodes).stream() //
							.map(opcode -> new MatchedOpcode(sample.instruction().opcode(), opcode)) //
							.collect(Collectors.toUnmodifiableSet())) //
					.filter(not(Set::isEmpty));

			final Map<Integer, Set<Set<MatchedOpcode>>> countToSetOfMatchingOpcodes = unknownMatchingOpcodes //
					.collect(Collectors.groupingBy(Set::size, Collectors.toUnmodifiableSet()));

			final Set<Set<MatchedOpcode>> matchingToOnlyOne = countToSetOfMatchingOpcodes.get(1);
			matchingToOnlyOne.stream() //
					.flatMap(Set::stream) //
					.forEach(matchedOpcode -> {
						decodedOpcodes.put(matchedOpcode.value, matchedOpcode.opcode);
						unmatchedOpcodes.remove(matchedOpcode.opcode);
					});
		}

		return Map.copyOf(decodedOpcodes);
	}

	@RecordBuilder
	public record Instruction(int opcode, int a, int b, int c)
	{
		public static Instruction from(final String line)
		{
			final String[] parts = line.split(" ");
			if (parts.length != 4) {
				throw new IllegalArgumentException("Input line doesn't contain 4 components");
			}

			return WristDeviceInstructionBuilder.builder() //
					.opcode(Integer.parseInt(parts[0].trim())) //
					.a(Integer.parseInt(parts[1].trim())) //
					.b(Integer.parseInt(parts[2].trim())) //
					.c(Integer.parseInt(parts[3].trim())) //
					.build();
		}
	}

	@RecordBuilder
	public record RegistersSet(long v0, long v1, long v2, long v3) implements WristDeviceRegistersSetBuilder.With
	{
		public static RegistersSet init()
		{
			return new RegistersSet(0, 0, 0, 0);
		}

		public static RegistersSet from(final String line)
		{
			final int startIndex = line.indexOf('[');
			final int endIndex = line.indexOf(']', startIndex);
			if (startIndex < 0 || endIndex < 0) {
				throw new IllegalArgumentException("Input line doesn't contain a register set portion");
			}
			final String[] values = line.substring(startIndex + 1, endIndex).split(",");
			if (values.length != 4) {
				throw new IllegalArgumentException("Input line doesn't contain 4 values");
			}
			return WristDeviceRegistersSetBuilder.builder() //
					.v0(Long.parseLong(values[0].trim())) //
					.v1(Long.parseLong(values[1].trim())) //
					.v2(Long.parseLong(values[2].trim())) //
					.v3(Long.parseLong(values[3].trim())) //
					.build();
		}

		public long get(final Register register)
		{
			return switch (register) {
				case R0 -> v0;
				case R1 -> v1;
				case R2 -> v2;
				case R3 -> v3;
			};
		}

		public RegistersSet set(final Register register, final long newValue)
		{
			return switch (register) {
				case R0 -> this.withV0(newValue);
				case R1 -> this.withV1(newValue);
				case R2 -> this.withV2(newValue);
				case R3 -> this.withV3(newValue);
			};
		}
	}

	public record Sample(RegistersSet before, Instruction instruction, RegistersSet after)
	{
		public List<Opcode> findMatchingOpcodes()
		{
			return findMatchingOpcodes(EnumSet.allOf(Opcode.class));
		}

		public List<Opcode> findMatchingOpcodes(final Set<Opcode> opcodesToConsider)
		{
			return opcodesToConsider.stream() //
					.filter(opcode -> {
						final RegistersSet executed = opcode.execute(instruction, before);
						return executed.equals(after);
					}) //
					.toList();
		}
	}

	@RequiredArgsConstructor
	public enum Register
	{
		R0, R1, R2, R3;

		public static Register from(final int v)
		{
			return switch (v) {
				case 0 -> R0;
				case 1 -> R1;
				case 2 -> R2;
				case 3 -> R3;
				default -> throw new IllegalArgumentException("Unsupported register index " + v);
			};
		}
	}

	@SuppressWarnings("java:S115")
	public enum Opcode
	{
		addr, addi, //
		mulr, muli, //
		banr, bani, //
		borr, bori, //
		setr, seti, //
		gtir, gtri, gtrr, //
		eqir, eqri, eqrr;

		public RegistersSet execute(final Instruction instruction, final RegistersSet input)
		{
			final Register target = Register.from(instruction.c());
			return switch (this) {
				case addr -> input.set(target, //
						input.get(Register.from(instruction.a())) + input.get(Register.from(instruction.b())));
				case addi -> input.set(target, input.get(Register.from(instruction.a())) + instruction.b());

				case mulr -> input.set(target, //
						input.get(Register.from(instruction.a())) * input.get(Register.from(instruction.b())));
				case muli -> input.set(target, input.get(Register.from(instruction.a())) * instruction.b());

				case banr -> input.set(target, //
						input.get(Register.from(instruction.a())) & input.get(Register.from(instruction.b())));
				case bani -> input.set(target, input.get(Register.from(instruction.a())) & instruction.b());

				case borr -> input.set(target, //
						input.get(Register.from(instruction.a())) | input.get(Register.from(instruction.b())));
				case bori -> input.set(target, input.get(Register.from(instruction.a())) | instruction.b());

				case setr -> input.set(target, input.get(Register.from(instruction.a())));
				case seti -> input.set(target, instruction.a());

				case gtir -> {
					final long a = instruction.a();
					final long b = input.get(Register.from(instruction.b()));
					yield input.set(target, a > b ? 1 : 0);
				}
				case gtri -> {
					final long a = input.get(Register.from(instruction.a()));
					final long b = instruction.b();
					yield input.set(target, a > b ? 1 : 0);
				}
				case gtrr -> {
					final long a = input.get(Register.from(instruction.a()));
					final long b = input.get(Register.from(instruction.b()));
					yield input.set(target, a > b ? 1 : 0);
				}

				case eqir -> {
					final long a = instruction.a();
					final long b = input.get(Register.from(instruction.b()));
					yield input.set(target, a == b ? 1 : 0);
				}
				case eqri -> {
					final long a = input.get(Register.from(instruction.a()));
					final long b = instruction.b();
					yield input.set(target, a == b ? 1 : 0);
				}
				case eqrr -> {
					final long a = input.get(Register.from(instruction.a()));
					final long b = input.get(Register.from(instruction.b()));
					yield input.set(target, a == b ? 1 : 0);
				}

			};
		}
	}

}
