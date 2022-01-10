package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay20
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var maze = DonutMaze.parseInput(in);
			final List<DonutMaze.Position> shortestPath = maze.findShortestPath();
			System.out.println("Part 1 - Answer %d".formatted(shortestPath.size() - 1));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var maze = DonutMaze.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(maze.findRecursiveShortestPath()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay20.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}
}
