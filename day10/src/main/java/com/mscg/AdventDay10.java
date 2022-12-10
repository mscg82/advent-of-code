package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay10
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var chip = RadioChip.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(chip.computeSignalStrength()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var chip = RadioChip.parseInput(in);
			System.out.println("Part 2 - Answer %n%s".formatted(chip.renderImage()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay10.class.getResourceAsStream("/input.txt"));
	}

}
