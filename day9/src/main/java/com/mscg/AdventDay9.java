package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class AdventDay9
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var computer = IntcodeV4.parseInput(in);
			final IntcodeV4 executed = computer.execute(List.of(1L).iterator(), false);
			System.out.println("Part 1 - Answer %s".formatted(Arrays.toString(executed.outputs())));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var computer = IntcodeV4.parseInput(in);
			final IntcodeV4 executed = computer.execute(List.of(2L).iterator(), false);
			System.out.println("Part 2 - Answer %s".formatted(Arrays.toString(executed.outputs())));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay9.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}

}
