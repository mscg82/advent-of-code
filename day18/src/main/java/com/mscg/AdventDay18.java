package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings({ "RedundantStringFormatCall", "java:S106" })
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
			final var ramRun = RamRun.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(ramRun.findMinPathToExit(1024, 70, 70)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var ramRun = RamRun.parseInput(in);
			final var blockingPosition = ramRun.findFirstBlockingByte(70, 70);
			System.out.println("Part 2 - Answer (%d,%d)".formatted(blockingPosition.x(), blockingPosition.y()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay18.class.getResourceAsStream("/input.txt"));
	}
}
