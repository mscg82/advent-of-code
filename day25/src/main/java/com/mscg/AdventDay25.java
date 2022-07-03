package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AdventDay25
{
	public static void main(final String[] args) throws Exception
	{
		part1();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var constellation = SpaceTimeConstellation.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(constellation.countConstellations()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(new InputStreamReader(Objects.requireNonNull(AdventDay25.class.getResourceAsStream("/input.txt")),
				StandardCharsets.UTF_8));
	}
}
