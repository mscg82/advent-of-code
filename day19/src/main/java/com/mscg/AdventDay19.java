package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

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
			final var beam = TractorBeam.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(beam.countAffectedPosition()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var beam = TractorBeam.parseInput(in);
			final var before = Instant.now();
			final TractorBeam.Position position = beam.findPosition();
			final var elapsed = Duration.between(before, Instant.now());
			System.out.println("Part 2 - Answer %d, elapsed %s".formatted(position.x() * 10_000 + position.y(), elapsed));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay19.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}
}
