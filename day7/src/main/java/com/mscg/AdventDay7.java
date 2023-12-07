package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay7
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cards = CamelCards.parseInput(in);
			System.out.println(STR."Part 1 - Answer \{cards.computeTotalWinning()}");
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cards = CamelCards.parseInput(in);
			System.out.println(STR."Part 2 - Answer \{cards.computeTotalWinningWithJolly()}");
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay7.class.getResourceAsStream("/input.txt"));
	}

}
