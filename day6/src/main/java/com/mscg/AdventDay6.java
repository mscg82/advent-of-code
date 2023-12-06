package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay6
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var boatGame = BoatGame.parseInput(in);
			System.out.println(STR."Part 1 - Answer \{boatGame.computeWinningCombinations()}");
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var boatGame = BoatGame.parseInput(in);
			System.out.println(STR."Part 2 - Answer \{boatGame.computeWinningCombinationsInOneGame()}");
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay6.class.getResourceAsStream("/input.txt"));
	}

}
