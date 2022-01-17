package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay22
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var shuffler = SpaceDeckShuffler.parseInput(in, 10007);
			System.out.println("Part 1 - Answer %d".formatted(shuffler.trackCard(2019)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var shuffler = SpaceDeckShuffler.parseInput(in, 119_315_717_514_047L);
			System.out.println("Part 2 - Answer %d".formatted(shuffler.trackCardsInPositionInBigDeck(2020, 101_741_582_076_661L)));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay22.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}
}
