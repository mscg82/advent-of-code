package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay3
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var organizer = RucksackOrganizer.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(organizer.sumAllDuplicatePriorities()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var organizer = RucksackOrganizer.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(organizer.sumSecurityBadges()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay3.class.getResourceAsStream("/input.txt"));
	}

}
