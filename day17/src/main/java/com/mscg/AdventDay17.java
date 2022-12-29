package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

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
			final var flow = PyroclasticFlow.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(flow.getHeightAfterRocks(2022L)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var flow = PyroclasticFlow.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(flow.getHeightAfterRocks(1000000000000L)));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay17.class.getResourceAsStream("/input.txt"));
	}
}
