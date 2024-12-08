package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings({ "RedundantStringFormatCall", "java:S106" })
public class AdventDay8
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var antennaMap = AntennaMap.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(antennaMap.countAntiNodeLocations()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var antennaMap = AntennaMap.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(antennaMap.countAntiNodeLocationsWithHarmonics()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay8.class.getResourceAsStream("/input.txt"));
	}

}
