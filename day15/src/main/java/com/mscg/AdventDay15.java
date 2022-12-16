package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay15
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var network = SensorNetwork.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(network.countPositionWithoutBeaconAtLine(2_000_000)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var network = SensorNetwork.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(network.findBeaconFrequency(0, 4_000_000)));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay15.class.getResourceAsStream("/input.txt"));
	}
}
