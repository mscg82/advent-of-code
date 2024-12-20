package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RaceConditionTest
{

	@Test
	void testCountCheatsThatSaveAtLeastSomeTime() throws Exception
	{
		final var race = RaceCondition.parseInput(InputUtils.readInput("""
				###############
				#...#...#.....#
				#.#.#.#.#.###.#
				#S#...#.#.#...#
				#######.#.#.###
				#######.#.#...#
				#######.#.###.#
				###..E#...#...#
				###.#######.###
				#...###...#...#
				#.#####.#.###.#
				#.#...#.#.#...#
				#.#.#.#.#.#.###
				#...#...#...###
				###############"""));

		assertEquals(1, race.countCheatsThatSaveAtLeast(64));
		assertEquals(2, race.countCheatsThatSaveAtLeast(40));
		assertEquals(3, race.countCheatsThatSaveAtLeast(38));
		assertEquals(4, race.countCheatsThatSaveAtLeast(36));
		assertEquals(5, race.countCheatsThatSaveAtLeast(20));
		assertEquals(8, race.countCheatsThatSaveAtLeast(12));
		assertEquals(10, race.countCheatsThatSaveAtLeast(10));
		assertEquals(14, race.countCheatsThatSaveAtLeast(8));
		assertEquals(16, race.countCheatsThatSaveAtLeast(6));
		assertEquals(30, race.countCheatsThatSaveAtLeast(4));
		assertEquals(44, race.countCheatsThatSaveAtLeast(2));
	}

	@Test
	void testLongCountCheatsThatSaveAtLeastSomeTime() throws Exception
	{
		final var race = RaceCondition.parseInput(InputUtils.readInput("""
				###############
				#...#...#.....#
				#.#.#.#.#.###.#
				#S#...#.#.#...#
				#######.#.#.###
				#######.#.#...#
				#######.#.###.#
				###..E#...#...#
				###.#######.###
				#...###...#...#
				#.#####.#.###.#
				#.#...#.#.#...#
				#.#.#.#.#.#.###
				#...#...#...###
				###############"""));

		assertEquals(3, race.countLongCheatsThatSaveAtLeast(76));
		assertEquals(7, race.countLongCheatsThatSaveAtLeast(74));
		assertEquals(29, race.countLongCheatsThatSaveAtLeast(72));
		assertEquals(41, race.countLongCheatsThatSaveAtLeast(70));
	}

}
