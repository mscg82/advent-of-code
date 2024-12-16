package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReinderMazeTest
{

	@Test
	void testFindBestPathCost1() throws Exception
	{
		final var maze = ReindeerMaze.parseInput(InputUtils.readInput("""
				###############
				#.......#....E#
				#.#.###.#.###.#
				#.....#.#...#.#
				#.###.#####.#.#
				#.#.#.......#.#
				#.#.#####.###.#
				#...........#.#
				###.#.#####.#.#
				#...#.....#.#.#
				#.#.#.###.#.#.#
				#.....#...#.#.#
				#.###.#.#.#.#.#
				#S..#.....#...#
				###############"""));

		assertEquals(7036, maze.findBestPathCost());
	}

	@Test
	void testFindBestPathCost2() throws Exception
	{
		final var maze = ReindeerMaze.parseInput(InputUtils.readInput("""
				#################
				#...#...#...#..E#
				#.#.#.#.#.#.#.#.#
				#.#.#.#...#...#.#
				#.#.#.#.###.#.#.#
				#...#.#.#.....#.#
				#.#.#.#.#.#####.#
				#.#...#.#.#.....#
				#.#.#####.#.###.#
				#.#.#.......#...#
				#.#.###.#####.###
				#.#.#...#.....#.#
				#.#.#.#####.###.#
				#.#.#.........#.#
				#.#.#.#########.#
				#S#.............#
				#################"""));

		assertEquals(11048, maze.findBestPathCost());
	}

	@Test
	void testFindTilesWithBestPath1() throws Exception
	{
		final var maze = ReindeerMaze.parseInput(InputUtils.readInput("""
				###############
				#.......#....E#
				#.#.###.#.###.#
				#.....#.#...#.#
				#.###.#####.#.#
				#.#.#.......#.#
				#.#.#####.###.#
				#...........#.#
				###.#.#####.#.#
				#...#.....#.#.#
				#.#.#.###.#.#.#
				#.....#...#.#.#
				#.###.#.#.#.#.#
				#S..#.....#...#
				###############"""));

		assertEquals(45, maze.findTilesWithBestPath());
	}

	@Test
	void testFindTilesWithBestPath2() throws Exception
	{
		final var maze = ReindeerMaze.parseInput(InputUtils.readInput("""
				#################
				#...#...#...#..E#
				#.#.#.#.#.#.#.#.#
				#.#.#.#...#...#.#
				#.#.#.#.###.#.#.#
				#...#.#.#.....#.#
				#.#.#.#.#.#####.#
				#.#...#.#.#.....#
				#.#.#####.#.###.#
				#.#.#.......#...#
				#.#.###.#####.###
				#.#.#...#.....#.#
				#.#.#.#####.###.#
				#.#.#.........#.#
				#.#.#.#########.#
				#S#.............#
				#################"""));

		assertEquals(64, maze.findTilesWithBestPath());
	}

}
