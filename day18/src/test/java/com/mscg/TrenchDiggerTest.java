package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrenchDiggerTest
{

	@Test
	void testTrenchDigging() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 6 (#70c710)
				D 5 (#0dc571)
				L 2 (#5713f0)
				D 2 (#d2c081)
				R 2 (#59c680)
				D 2 (#411b91)
				L 5 (#8ceee2)
				U 2 (#caa173)
				L 1 (#1b58a2)
				U 2 (#caa171)
				R 2 (#7807d2)
				U 3 (#a77fa3)
				L 2 (#015232)
				U 2 (#7a21e3)""")));

		assertEquals("""
				#######
				#.....#
				###...#
				..#...#
				..#...#
				###.###
				#...#..
				##..###
				.#....#
				.######""", digger.digTrench().toVisualizationString());
	}

	@Test
	void testSimpleComputeArea() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 4 (#70c710)
				U 2 (#0dc571)
				R 2 (#5713f0)
				D 4 (#d2c081)
				L 6 (#d2c082)
				U 2 (#d2c083)""")));

		assertEquals("""
				....###
				....#.#
				#####.#
				#.....#
				#######""", digger.digTrench().toVisualizationString());

		assertEquals(27, digger.computeInnerArea());
	}

	@Test
	void testSimpleComputeArea2() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 4 (#70c710)
				U 2 (#0dc571)
				L 2 (#5713f0)
				U 2 (#d2c081)
				R 4 (#d2c082)
				D 6 (#d2c083)
				L 6 (#d2c084)
				U 2 (#d2c085)""")));

		assertEquals("""
				..#####
				..#...#
				..###.#
				....#.#
				#####.#
				#.....#
				#######""", digger.digTrench().toVisualizationString());

		assertEquals(39, digger.computeInnerArea());
	}

	@Test
	void testSimpleComputeArea3() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 6 (#70c710)
				U 4 (#0dc571)
				L 2 (#5713f0)
				D 2 (#d2c081)
				L 2 (#d2c082)
				U 2 (#d2c083)
				L 2 (#d2c084)
				D 4 (#d2c085)""")));

		assertEquals("""
				###.###
				#.#.#.#
				#.###.#
				#.....#
				#######""", digger.digTrench().toVisualizationString());

		assertEquals(33, digger.computeInnerArea());
	}

	@Test
	void testComputeArea() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 6 (#70c710)
				D 5 (#0dc571)
				L 2 (#5713f0)
				D 2 (#d2c081)
				R 2 (#59c680)
				D 2 (#411b91)
				L 5 (#8ceee2)
				U 2 (#caa173)
				L 1 (#1b58a2)
				U 2 (#caa171)
				R 2 (#7807d2)
				U 3 (#a77fa3)
				L 2 (#015232)
				U 2 (#7a21e3)""")));

		assertEquals(62, digger.computeInnerArea());
	}

	@Test
	void testComputeAreaFixed() throws Exception
	{
		final var digger = TrenchDigger.parseInput(new BufferedReader(new StringReader("""
				R 6 (#70c710)
				D 5 (#0dc571)
				L 2 (#5713f0)
				D 2 (#d2c081)
				R 2 (#59c680)
				D 2 (#411b91)
				L 5 (#8ceee2)
				U 2 (#caa173)
				L 1 (#1b58a2)
				U 2 (#caa171)
				R 2 (#7807d2)
				U 3 (#a77fa3)
				L 2 (#015232)
				U 2 (#7a21e3)""")));

		assertEquals(952408144115L, digger.computeInnerAreaFixed());
	}

}
