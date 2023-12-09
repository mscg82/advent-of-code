package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings({ "RedundantStringFormatCall", "java:S106" })
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
			final var oasisAnalyzer = OasisAnalyzer.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(oasisAnalyzer.sumExtrapolatedNextValues()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var oasisAnalyzer = OasisAnalyzer.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(oasisAnalyzer.sumExtrapolatedPreviousValues()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay9.class.getResourceAsStream("/input.txt"));
	}

}
