package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay18
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cubes = Cubes.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(cubes.computeSurfaceArea()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cubes = Cubes.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(cubes.computeExteriorSurfaceArea()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay18.class.getResourceAsStream("/input.txt"));
	}
}
