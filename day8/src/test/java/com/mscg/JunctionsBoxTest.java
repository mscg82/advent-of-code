package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JunctionsBoxTest
{

	@Test
	void testParse() throws Exception
	{
		final var junctionsBox = JunctionsBox.parseInput(generateTestInput());
		assertEquals(20, junctionsBox.junctions().size());
	}

	@Test
	void testFindSizeOfBiggestThreeCircuits() throws Exception
	{
		final var junctionsBox = JunctionsBox.parseInput(generateTestInput());
		assertEquals(40, junctionsBox.findSizeOfBiggestThreeCircuits(10));
	}

	@Test
	void testFindLastConnection() throws Exception
	{
		final var junctionsBox = JunctionsBox.parseInput(generateTestInput());
		assertEquals(25272, junctionsBox.findLastConnection());
	}

	private static BufferedReader generateTestInput()
	{
		return new BufferedReader(new StringReader("""
				162,817,812
				57,618,57
				906,360,560
				592,479,940
				352,342,300
				466,668,158
				542,29,236
				431,825,988
				739,650,466
				52,470,668
				216,146,977
				819,987,18
				117,168,530
				805,96,715
				346,949,466
				970,615,88
				941,993,340
				862,61,35
				984,92,344
				425,690,689
				"""));
	}

}
