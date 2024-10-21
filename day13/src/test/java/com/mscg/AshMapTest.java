package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AshMapTest
{

	@Test
	void testParse() throws Exception
	{
		final var ashMap = AshMap.parseInput(new BufferedReader(new StringReader("""
				#.##..##.
				..#.##.#.
				##......#
				##......#
				..#.##.#.
				..##..##.
				#.#.##.#.
				
				#...##..#
				#....#..#
				..##..###
				#####.##.
				#####.##.
				..##..###
				#....#..#
				""")));
		assertEquals(2, ashMap.maps().size());
		assertEquals(7, ashMap.maps().get(0).rows());
		assertEquals(9, ashMap.maps().get(0).cols());
		assertEquals(7, ashMap.maps().get(1).rows());
		assertEquals(9, ashMap.maps().get(1).cols());
	}

	@Test
	void testMirrorPoint() throws Exception
	{
		final var ashMap = AshMap.parseInput(new BufferedReader(new StringReader("""
				#.##..##.
				..#.##.#.
				##......#
				##......#
				..#.##.#.
				..##..##.
				#.#.##.#.
				
				#...##..#
				#....#..#
				..##..###
				#####.##.
				#####.##.
				..##..###
				#....#..#
				""")));

		final var map1 = ashMap.maps().get(0);
		assertEquals(Optional.of(new AshMap.MirrorPoint(AshMap.Direction.VERTICAL, 5)), map1.findMirrorPoint());

		final var map2 = ashMap.maps().get(1);
		assertEquals(Optional.of(new AshMap.MirrorPoint(AshMap.Direction.HORIZONTAL, 4)), map2.findMirrorPoint());
	}

	@Test
	void testSmudgedMirrorPoint() throws Exception
	{
		final var ashMap = AshMap.parseInput(new BufferedReader(new StringReader("""
				#.##..##.
				..#.##.#.
				##......#
				##......#
				..#.##.#.
				..##..##.
				#.#.##.#.
				
				#...##..#
				#....#..#
				..##..###
				#####.##.
				#####.##.
				..##..###
				#....#..#
				""")));

		final var map1 = ashMap.maps().get(0);
		assertEquals(Optional.of(new AshMap.MirrorPoint(AshMap.Direction.HORIZONTAL, 3)), map1.findSmudgedMirrorPoint());

		final var map2 = ashMap.maps().get(1);
		assertEquals(Optional.of(new AshMap.MirrorPoint(AshMap.Direction.HORIZONTAL, 1)), map2.findSmudgedMirrorPoint());
	}

	@Test
	void testSummarize() throws Exception
	{
		final var ashMap = AshMap.parseInput(new BufferedReader(new StringReader("""
				#.##..##.
				..#.##.#.
				##......#
				##......#
				..#.##.#.
				..##..##.
				#.#.##.#.
				
				#...##..#
				#....#..#
				..##..###
				#####.##.
				#####.##.
				..##..###
				#....#..#
				""")));

		assertEquals(405L, ashMap.summarize());
		assertEquals(400L, ashMap.summarizeSmudged());
	}

}
