package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings({ "RedundantStringFormatCall", "java:S106" })
public class AdventDay14
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var rockDish = RockDish.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(rockDish.tiltNorth().weight()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var rockDish = RockDish.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(rockDish.tiltContinuously().weight()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay14.class.getResourceAsStream("/input.txt"));
	}
}
