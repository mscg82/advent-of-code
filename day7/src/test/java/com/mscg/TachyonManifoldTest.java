package com.mscg;

import com.mscg.utils.Position8Bits;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TachyonManifoldTest
{

	@Test
	void testParse() throws Exception
	{
		final var manifold = TachyonManifold.parseInput(buildTestInput());
		assertEquals(15, manifold.rows());
		assertEquals(16, manifold.cols());
		assertEquals(new Position8Bits(7, 0), manifold.start());
		assertEquals(22, manifold.splitters().size());
	}

	@Test
	void testCountSplits() throws Exception
	{
		final var manifold = TachyonManifold.parseInput(buildTestInput());
		assertEquals(21, manifold.countSplits());
	}

	@Test
	void testCountTimelines() throws Exception
	{
		final var manifold = TachyonManifold.parseInput(buildTestInput());
		assertEquals(40, manifold.countTimelines());
	}

	private static BufferedReader buildTestInput()
	{
		return new BufferedReader(new StringReader("""
				.......S.......
				...............
				.......^.......
				...............
				......^.^......
				...............
				.....^.^.^.....
				...............
				....^.^...^....
				...............
				...^.^...^.^...
				...............
				..^...^.....^..
				...............
				.^.^.^.^.^...^.
				...............
				"""));
	}

}
