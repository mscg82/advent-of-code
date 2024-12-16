package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings({ "RedundantStringFormatCall", "java:S106" })
public class AdventDay1
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var comp = LocationIDComparator.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(comp.compareLists()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var comp = LocationIDComparator.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(comp.computeSimilarity()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay1.class.getResourceAsStream("/input.txt"));
	}

}
