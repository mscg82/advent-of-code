package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

public record WristDeviceV3(List<Instruction> instructions, Register ip)
{

	public static WristDeviceV3 parseInput(final BufferedReader in) throws IOException
	{
		final var pattern = Pattern.compile("#ip (\\d+)");
		final String ipLine = in.readLine();
		final var matcher = pattern.matcher(ipLine);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Can't read instruction pointer binding from line " + ipLine);
		}
		final int ipRegNum = Integer.parseInt(matcher.group(1));
		final var ip = Register.from(ipRegNum);

		final List<Instruction> instructions = in.lines() //
				.map(Instruction::from) //
				.toList();

		return new WristDeviceV3(instructions, ip);
	}

	public long findFirstHaltingValue()
	{
		final var haltingValues = executeProgram(RegistersSet.init(), true);
		return haltingValues.get(0);
	}

	public long findLastHaltingValue()
	{
		final var haltingValues = executeProgram(RegistersSet.init(), true);
		return haltingValues.get(haltingValues.size() - 1);
	}

	public List<Long> executeProgram(final RegistersSet initialRegisterSet, final boolean executeOptimized)
	{
		var registerSet = initialRegisterSet;

		if (executeOptimized) {
			return executeOptimized(registerSet);
		} else {
			final var haltingValues = new LinkedHashSet<Long>();
			while (true) {
				final int ipVal = (int) registerSet.get(ip);
				if (ipVal < 0 || ipVal >= instructions.size()) {
					break;
				}
				if (ipVal == 28) {
					final long r5 = registerSet.get(Register.R5);
					if (!haltingValues.add(r5)) {
						break;
					}
				}
				final var instruction = instructions.get(ipVal);
				final RegistersSet afterExecution = instruction.execute(registerSet);
				registerSet = afterExecution.set(ip, afterExecution.get(ip) + 1);
			}

			return List.copyOf(haltingValues);
		}

	}

	@SuppressWarnings({ "UnusedAssignment", "java:S1481", "java:S1854" })
	private List<Long> executeOptimized(final RegistersSet registerSet)
	{
		final var haltingValues = new LinkedHashSet<Long>();

		final long r0 = registerSet.get(Register.R0);
		final long r1 = registerSet.get(Register.R1);
		long r2 = registerSet.get(Register.R2);
		long r3 = registerSet.get(Register.R3);
		long r4 = registerSet.get(Register.R4);
		long r5 = registerSet.get(Register.R5);
		while (true) {
			r4 = r5 | 65536;
			r5 = 13431073;
			while (true) {
				r3 = r4 & 255;
				r5 = r5 + r3;
				r5 = r5 & 16777215;
				r5 = r5 * 65899;
				r5 = r5 & 16777215;
				if (256 > r4) {
					break;
				}
				r3 = r4 / 256;
				r2 = r3 + 1;
				r4 = r3;
			}
			if (!haltingValues.add(r5) || r5 == r0) {
				break;
			}
		}
		return List.copyOf(haltingValues);
	}

	@RecordBuilder
	public record Instruction(Opcode opcode, int a, int b, int c)
	{
		public static Instruction from(final String line)
		{
			final String[] parts = line.split(" ");
			if (parts.length != 4) {
				throw new IllegalArgumentException("Input line doesn't contain 4 components");
			}

			return WristDeviceV3InstructionBuilder.builder() //
					.opcode(Opcode.valueOf(parts[0].trim())) //
					.a(Integer.parseInt(parts[1].trim())) //
					.b(Integer.parseInt(parts[2].trim())) //
					.c(Integer.parseInt(parts[3].trim())) //
					.build();
		}

		public RegistersSet execute(final RegistersSet input)
		{
			return opcode.execute(this, input);
		}

		@Override
		public String toString()
		{
			return opcode.name() + " " + a + " " + b + " " + c;
		}
	}

	@RecordBuilder
	public record RegistersSet(long v0, long v1, long v2, long v3, long v4, long v5)
			implements WristDeviceV3RegistersSetBuilder.With
	{
		public static RegistersSet init()
		{
			return new RegistersSet(0, 0, 0, 0, 0, 0);
		}

		public long get(final Register register)
		{
			return switch (register) {
				case R0 -> v0;
				case R1 -> v1;
				case R2 -> v2;
				case R3 -> v3;
				case R4 -> v4;
				case R5 -> v5;
			};
		}

		public RegistersSet set(final Register register, final long newValue)
		{
			return switch (register) {
				case R0 -> this.withV0(newValue);
				case R1 -> this.withV1(newValue);
				case R2 -> this.withV2(newValue);
				case R3 -> this.withV3(newValue);
				case R4 -> this.withV4(newValue);
				case R5 -> this.withV5(newValue);
			};
		}

		@Override
		public String toString()
		{
			return "[" + v0 + ", " + v1 + ", " + v2 + ", " + v3 + ", " + v4 + ", " + v5 + "]";
		}
	}

	@RequiredArgsConstructor
	public enum Register
	{
		R0, R1, R2, R3, R4, R5;

		public static Register from(final int v)
		{
			return switch (v) {
				case 0 -> R0;
				case 1 -> R1;
				case 2 -> R2;
				case 3 -> R3;
				case 4 -> R4;
				case 5 -> R5;
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
