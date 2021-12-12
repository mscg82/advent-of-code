package com.msg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
			final var map = CaveMap.parseInput(in);
			final List<List<String>> allPaths = map.findAllPaths();
			System.out.println("Part 1 - Answer %d".formatted(allPaths.size()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var map = CaveMap.parseInput(in);
			final List<List<String>> allPaths = map.findAllPaths2();
			System.out.println("Part 2 - Answer %d".formatted(allPaths.size()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}

}
