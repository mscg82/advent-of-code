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
			System.out.println(STR."Part 1 - Answer \{0}");
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			System.out.println(STR."Part 2 - Answer \{0}");
		}
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay11.class.getResourceAsStream("/input.txt"));
	}

}
