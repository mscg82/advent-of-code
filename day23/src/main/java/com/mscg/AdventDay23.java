package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

public class AdventDay23
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var house = AmphipodsHouse.parseInput1(in);
			final var before = Instant.now();
			final long minCost = house.findMinCost();
			final var elapsed = Duration.between(before, Instant.now());
			System.out.println("Part 1 - Answer %d, elapsed: %s".formatted(minCost, elapsed));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var house = AmphipodsHouse.parseInput2(in);
			final var before = Instant.now();
			final long minCost = house.findMinCost();
			final var elapsed = Duration.between(before, Instant.now());
			System.out.println("Part 2 - Answer %d, elapsed: %s".formatted(minCost, elapsed));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay23.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}
}
