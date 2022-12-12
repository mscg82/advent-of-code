package com.msg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay12
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var heightMap = HeightMap.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(heightMap.computeLengthOfShortestPathToExit()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var heightMap = HeightMap.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(heightMap.computeShortestLengthFromAllLowestElevations()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay12.class.getResourceAsStream("/input.txt"));
	}

}
