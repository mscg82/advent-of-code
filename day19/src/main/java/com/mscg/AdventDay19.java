package com.mscg;

import com.mscg.WristDeviceV2.Register;
import com.mscg.WristDeviceV2.RegistersSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AdventDay19
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var device = WristDeviceV2.parseInput(in);
			final var registers = device.executeProgram();
			System.out.println("Part 1 - Answer %d".formatted(registers.get(Register.R0)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var device = WristDeviceV2.parseInput(in);
			final var initialRegisters = RegistersSet.init().set(Register.R0, 1);
			final var registers = device.executeProgram(initialRegisters, true);
			System.out.println("Part 2 - Answer %d".formatted(registers.get(Register.R0)));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(new InputStreamReader(Objects.requireNonNull(AdventDay19.class.getResourceAsStream("/input.txt")),
				StandardCharsets.UTF_8));
	}
}
