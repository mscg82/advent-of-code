package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay17
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var map = TerrainMap.parseInput(in);
			final TerrainMap evolvedMap = map.evolve();
			System.out.println("Part 1 - Answer %d".formatted(evolvedMap.waterPositions().stream() //
					.filter(pos -> pos.y() >= evolvedMap.minY()) //
					.count()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var map = TerrainMap.parseInput(in);
			final TerrainMap evolvedMap = map.evolve();
			System.out.println("Part 2 - Answer %d".formatted(evolvedMap.stillWaterPositions().size()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay17.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}

}
