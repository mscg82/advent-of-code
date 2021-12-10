package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
			final var imageDecoder = SifImageDecoder.parseInput(in, 6, 25);
			System.out.println("Part 1 - Answer %d".formatted(imageDecoder.scoreLayerWithFewerZeros()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var imageDecoder = SifImageDecoder.parseInput(in, 6, 25);
			System.out.println("Part 2 - Answer %n%s".formatted(imageDecoder.decodeImage()));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay8.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}

}
