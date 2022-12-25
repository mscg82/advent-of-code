package com.mscg;

import com.mscg.utils.InputUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class AdventDay25
{
	public static void main(final String[] args) throws Exception
	{
		part1();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var snafu = Snafu.parseInput(in);
			System.out.println("Part 1 - Answer %s".formatted(snafu.sumValues()));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay25.class.getResourceAsStream("/input.txt"));
	}
}
