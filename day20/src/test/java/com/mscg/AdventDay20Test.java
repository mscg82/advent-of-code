package com.mscg;

import com.mscg.RegularMap.Position;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventDay20Test
{

	@Test
	void testParse1() throws Exception
	{
		final var map = RegularMap.parseInput(fromString("^ENWWW(NEEE|SSE(EE|N))$"));
		assertEquals(16, map.adjacencyMap().size());
		assertEquals(Set.of(new Position(1, 0)), map.adjacencyMap().get(new Position(0, 0)));
		assertEquals(Set.of(new Position(-1, -1), new Position(-2, -2), new Position(-2, 0)),
				map.adjacencyMap().get(new Position(-2, -1)));
		assertEquals(10, map.findLongestPath());
	}

	@Test
	void testParse2() throws Exception
	{
		final var map = RegularMap.parseInput(fromString("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$"));
		assertEquals(25, map.adjacencyMap().size());
		assertEquals(Set.of(new Position(1, 0)), map.adjacencyMap().get(new Position(0, 0)));
		assertEquals(Set.of(new Position(-1, -1), new Position(-2, -2), new Position(-2, 0)),
				map.adjacencyMap().get(new Position(-2, -1)));
		assertEquals(Set.of(new Position(1, 1), new Position(-1, 1), new Position(0, 2)),
				map.adjacencyMap().get(new Position(0, 1)));
		assertEquals(18, map.findLongestPath());
	}

	private static BufferedReader fromString(final String value)
	{
		return new BufferedReader(new StringReader(value));
	}

}
