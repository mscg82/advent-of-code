package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay11
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var game = MonkeysGame.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(game.computeMonkeyBusinessLevel(20, false)));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var game = MonkeysGame.parseInput(in);
			System.out.println("Part 2 - Answer %d".formatted(game.computeMonkeyBusinessLevel(10_000, true)));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay11.class.getResourceAsStream("/input.txt"));
	}

}
