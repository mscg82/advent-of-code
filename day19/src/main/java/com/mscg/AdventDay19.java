package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

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
			final var geodeOpener = GeodeOpener.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(geodeOpener.sumAllQualityLevels()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var geodeOpener = GeodeOpener.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(geodeOpener.computeFirstThree()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay19.class.getResourceAsStream("/input.txt"));
	}
}
