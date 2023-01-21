package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlizzardBasinTest
{

	@Test
	void testShortestPath() throws Exception
	{
		final var basin = BlizzardBasin.parseInput(new BufferedReader(new StringReader("""
				#.######
				#>>.<^<#
				#.<..<<#
				#>v.><>#
				#<^v^^>#
				######.#""")));
		assertEquals(18, basin.computeShortestPath());
	}

	@Test
	void testShortestPath3Ways() throws Exception
	{
		final var basin = BlizzardBasin.parseInput(new BufferedReader(new StringReader("""
				#.######
				#>>.<^<#
				#.<..<<#
				#>v.><>#
				#<^v^^>#
				######.#""")));
		assertEquals(54, basin.computeShortestPath3Way());
	}

}
