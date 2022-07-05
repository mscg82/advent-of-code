package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AdventDay24
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var fight = DiseaseFight.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(fight.computeWinningArmySize()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var fight = DiseaseFight.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(fight.computeWinningArmySizeWithBoost()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(new InputStreamReader(Objects.requireNonNull(AdventDay24.class.getResourceAsStream("/input.txt")),
				StandardCharsets.UTF_8));
	}
}
