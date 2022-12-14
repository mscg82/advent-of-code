package com.mscg;

import com.mscg.CaveMap.Position;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaveMapTest
{
	@Test
	void parse() throws Exception
	{
		final var caveMap = CaveMap.parseInput(new BufferedReader(new StringReader("""
				498,4 -> 498,6 -> 496,6
				503,4 -> 502,4 -> 502,9 -> 494,9
				""")));
		assertEquals(new LinkedHashSet<>(List.of( //
						new Position(498, 4), new Position(498, 5), new Position(498, 6), new Position(497, 6), new Position(496, 6), //
						new Position(503, 4), new Position(502, 4), new Position(502, 5), new Position(502, 6), new Position(502, 7), //
						new Position(502, 8), new Position(502, 9), new Position(501, 9), new Position(500, 9), new Position(499, 9), //
						new Position(498, 9), new Position(497, 9), new Position(496, 9), new Position(495, 9), new Position(494, 9))), //
				caveMap.rockPositions());
	}

	@Test
	void pour() throws Exception
	{
		final var caveMap = CaveMap.parseInput(new BufferedReader(new StringReader("""
				498,4 -> 498,6 -> 496,6
				503,4 -> 502,4 -> 502,9 -> 494,9
				""")));
		assertEquals(24, caveMap.pourSand());
	}

	@Test
	void pourOnBed() throws Exception
	{
		final var caveMap = CaveMap.parseInput(new BufferedReader(new StringReader("""
				498,4 -> 498,6 -> 496,6
				503,4 -> 502,4 -> 502,9 -> 494,9
				""")));
		assertEquals(93, caveMap.pourSandOnBed());
	}
}
