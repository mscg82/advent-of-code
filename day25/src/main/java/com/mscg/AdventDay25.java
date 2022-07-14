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
			System.out.println("Part 1 - Answer %d".formatted(0));
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay25.class.getResourceAsStream("/input.txt"));
	}
}
