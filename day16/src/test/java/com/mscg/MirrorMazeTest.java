package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MirrorMazeTest
{

	@Test
	void testEnergizedCells() throws Exception
	{
		final var maze = MirrorMaze.parseInput(new BufferedReader(new StringReader("""
				.|...\\....
				|.-.\\.....
				.....|-...
				........|.
				..........
				.........\\
				..../.\\\\..
				.-.-/..|..
				.|....-|.\\
				..//.|....""")));
		assertEquals(46, maze.computeEnergizedCells());
	}

	@Test
	void testMaxEnergizedCells() throws Exception
	{
		final var maze = MirrorMaze.parseInput(new BufferedReader(new StringReader("""
				.|...\\....
				|.-.\\.....
				.....|-...
				........|.
				..........
				.........\\
				..../.\\\\..
				.-.-/..|..
				.|....-|.\\
				..//.|....""")));
		assertEquals(51, maze.computeMaxEnergizedCells());
	}

}
