package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrucibleMapTest
{

	@Test
	void testMinHeatLoss() throws Exception
	{
		final var map = CrucibleMap.parseInput(new BufferedReader(new StringReader("""
				2413432311323
				3215453535623
				3255245654254
				3446585845452
				4546657867536
				1438598798454
				4457876987766
				3637877979653
				4654967986887
				4564679986453
				1224686865563
				2546548887735
				4322674655533""")));
		assertEquals(102, map.findMinHeatLoss(System.out::println));
	}

	@Test
	void testMinHeatLossBigCrucible1() throws Exception
	{
		final var map = CrucibleMap.parseInput(new BufferedReader(new StringReader("""
				2413432311323
				3215453535623
				3255245654254
				3446585845452
				4546657867536
				1438598798454
				4457876987766
				3637877979653
				4654967986887
				4564679986453
				1224686865563
				2546548887735
				4322674655533""")));
		assertEquals(94, map.findMinHeatLossBigCrucible(System.out::println));
	}

	@Test
	void testMinHeatLossBigCrucible2() throws Exception
	{
		final var map = CrucibleMap.parseInput(new BufferedReader(new StringReader("""
				111111111111
				999999999991
				999999999991
				999999999991
				999999999991""")));
		assertEquals(71, map.findMinHeatLossBigCrucible(System.out::println));
	}

}
