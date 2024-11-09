package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RockDishTest
{

	@Test
	void testTiltNorth() throws Exception
	{
		final var rockDish = RockDish.parseInput(new BufferedReader(new StringReader("""
				O....#....
				O.OO#....#
				.....##...
				OO.#O....O
				.O.....O#.
				O.#..O.#.#
				..O..#O..O
				.......O..
				#....###..
				#OO..#....""")));

		assertEquals("""
				OOOO.#.O..
				OO..#....#
				OO..O##..O
				O..#.OO...
				........#.
				..#....#.#
				..O..#.O.O
				..O.......
				#....###..
				#....#....""", rockDish.tiltNorth().toString());
	}

	@Test
	void testWeight() throws Exception
	{
		final var rockDish = RockDish.parseInput(new BufferedReader(new StringReader("""
				O....#....
				O.OO#....#
				.....##...
				OO.#O....O
				.O.....O#.
				O.#..O.#.#
				..O..#O..O
				.......O..
				#....###..
				#OO..#....""")));

		assertEquals(136L, rockDish.tiltNorth().weight());
	}

	@Test
	void testTiltCycle() throws Exception
	{
		final var rockDish = RockDish.parseInput(new BufferedReader(new StringReader("""
				O....#....
				O.OO#....#
				.....##...
				OO.#O....O
				.O.....O#.
				O.#..O.#.#
				..O..#O..O
				.......O..
				#....###..
				#OO..#....""")));

		final var cycle1 = rockDish.tiltOneCycle();
		assertEquals("""
				.....#....
				....#...O#
				...OO##...
				.OO#......
				.....OOO#.
				.O#...O#.#
				....O#....
				......OOOO
				#...O###..
				#..OO#....""", cycle1.toString());

		final var cycle2 = cycle1.tiltOneCycle();
		assertEquals("""
				.....#....
				....#...O#
				.....##...
				..O#......
				.....OOO#.
				.O#...O#.#
				....O#...O
				.......OOO
				#..OO###..
				#.OOO#...O""", cycle2.toString());

		final var cycle3 = cycle2.tiltOneCycle();
		assertEquals("""
				.....#....
				....#...O#
				.....##...
				..O#......
				.....OOO#.
				.O#...O#.#
				....O#...O
				.......OOO
				#...O###.O
				#.OOO#...O""", cycle3.toString());
	}

	@Test
	void testWeightAfterTilts() throws Exception
	{
		final var rockDish = RockDish.parseInput(new BufferedReader(new StringReader("""
				O....#....
				O.OO#....#
				.....##...
				OO.#O....O
				.O.....O#.
				O.#..O.#.#
				..O..#O..O
				.......O..
				#....###..
				#OO..#....""")));

		assertEquals(64, rockDish.tiltContinuously().weight());
	}

}
