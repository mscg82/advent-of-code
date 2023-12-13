package com.msg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay12
{

	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var springField = SpringField.parseInput(in);
			System.out.println(STR."Part 1 - Answer \{springField.sumAllArrangements()}");
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var springField = SpringField.parseInput(in);
			System.out.println(STR."Part 2 - Answer \{springField.sumAllUnfoldedArrangements()}");
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay12.class.getResourceAsStream("/input.txt"));
	}

}
