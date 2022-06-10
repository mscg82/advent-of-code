package com.mscg;

import com.mscg.TerrainMap.Position;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventDay17Test
{
	@Test
	void testParse() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));
		assertEquals("""
				...........#
				#..#.......#
				#..#..#.....
				#..#..#.....
				#.....#.....
				#.....#.....
				#######.....
				............
				............
				...#.....#..
				...#.....#..
				...#.....#..
				...#######..""", map.asMapString());
	}

	@Test
	void testEvolve1() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));

		final var evolvedMap = map.withDripPositions(Set.of(new Position(500, 0))).letWaterDrip();
		assertEquals("""
				.....w.....#
				#..#.......#
				#..#..#.....
				#..#..#.....
				#.....#.....
				#.....#.....
				#######.....
				............
				............
				...#.....#..
				...#.....#..
				...#.....#..
				...#######..""", evolvedMap.asMapString());
	}

	@Test
	void testEvolve2() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));

		final var evolvedMap = map.withDripPositions(Set.of(new Position(500, 6))).letWaterDrip();
		assertEquals("""
				...........#
				#..#.......#
				#..#..#.....
				#..#..#.....
				#.....#.....
				#wwwww#.....
				#######.....
				............
				............
				...#.....#..
				...#.....#..
				...#.....#..
				...#######..""", evolvedMap.asMapString());
		assertEquals(5, evolvedMap.waterPositions().size());
		assertEquals(Set.of(new Position(500, 5)), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve3() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));

		final var waterPositions = Set.of( //
				new Position(496, 6), //
				new Position(497, 6), //
				new Position(498, 6), //
				new Position(499, 6), //
				new Position(500, 6), //
				new Position(496, 5), //
				new Position(497, 5), //
				new Position(498, 5), //
				new Position(499, 5), //
				new Position(500, 5) //
		);
		final var fixedMap = map.withWaterPositions(waterPositions).withDripPositions(Set.of(new Position(500, 4)));

		final var evolvedMap = fixedMap.letWaterDrip();
		assertEquals("""
				...........#
				#..#.......#
				#..#..#.....
				#..#ww#.....
				#wwwww#.....
				#wwwww#.....
				#######.....
				............
				............
				...#.....#..
				...#.....#..
				...#.....#..
				...#######..""", evolvedMap.asMapString());
		assertEquals(12, evolvedMap.waterPositions().size());
		assertEquals(Set.of(new Position(500, 3)), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve4() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));

		final var waterPositions = Set.of( //
				new Position(496, 6), //
				new Position(497, 6), //
				new Position(498, 6), //
				new Position(499, 6), //
				new Position(500, 6), //
				new Position(496, 5), //
				new Position(497, 5), //
				new Position(498, 5), //
				new Position(499, 5), //
				new Position(500, 5), //
				new Position(499, 4), //
				new Position(500, 4), //
				new Position(499, 3), //
				new Position(500, 3) //
		);
		final var fixedMap = map.withWaterPositions(waterPositions).withDripPositions(Set.of(new Position(500, 2)));

		final var evolvedMap = fixedMap.letWaterDrip();
		assertEquals("""
				...........#
				#..#wwww...#
				#..#ww#.....
				#..#ww#.....
				#wwwww#.....
				#wwwww#.....
				#######.....
				............
				............
				...#.....#..
				...#.....#..
				...#.....#..
				...#######..""", evolvedMap.asMapString());
		assertEquals(18, evolvedMap.waterPositions().size());
		assertEquals(Set.of(new Position(502, 2)), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve5() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				x=495, y=2..7
				y=7, x=495..501
				x=501, y=3..7
				x=498, y=2..4
				x=506, y=1..2
				x=498, y=10..13
				x=504, y=10..13
				y=13, x=498..504"""));

		final var evolvedMap = map.evolve();
		assertEquals("""
				.....w.....#
				#..#wwww...#
				#..#ww#w....
				#..#ww#w....
				#wwwww#w....
				#wwwww#w....
				#######w....
				.......w....
				..wwwwwwwww.
				..w#wwwww#w.
				..w#wwwww#w.
				..w#wwwww#w.
				..w#######w.""", evolvedMap.asMapString());
		assertEquals(57, evolvedMap.waterPositions().size());
		assertEquals(Set.of(new Position(497, 13), new Position(505, 13)), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve6() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				y=5, x=498..502
				x=498, y=3..5
				x=502, y=3..5
				y=8, x=495..505
				x=495, y=2..8
				x=505, y=3..8"""));

		assertEquals("""
				#..........
				#..#...#..#
				#..#...#..#
				#..#####..#
				#.........#
				#.........#
				###########""", map.asMapString());

		final var evolvedMap = map.evolve();
		assertEquals("""
				.....w......
				#wwwwwwwwwww
				#ww#www#ww#w
				#ww#www#ww#w
				#ww#####ww#w
				#wwwwwwwww#w
				#wwwwwwwww#w
				###########w""", evolvedMap.asMapString());
		assertEquals(54, evolvedMap.waterPositions().size());
		assertEquals(Set.of(new Position(506, 8)), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve7() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				y=5, x=498..502
				x=498, y=3..5
				x=502, y=4..5"""));

		final var waterPositions = Set.of( //
				new Position(499, 3), //
				new Position(500, 3), //
				new Position(501, 3), //
				new Position(502, 3), //
				new Position(503, 3), //
				new Position(499, 4), //
				new Position(500, 4), //
				new Position(501, 4), //
				new Position(503, 4), //
				new Position(503, 5) //
		);
		final var fixedMap = map.withWaterPositions(waterPositions).withDripPositions(Set.of(new Position(499, 2)));

		assertEquals("""
				#wwwww
				#www#w
				#####w""", fixedMap.asMapString());

		final var evolvedMap = fixedMap.letWaterDrip();
		assertEquals("""
				#wwwww
				#www#w
				#####w""", evolvedMap.asMapString());
		assertEquals(10, evolvedMap.waterPositions().size());
		assertEquals(Set.of(), evolvedMap.dripPositions());
	}

	@Test
	void testEvolve8() throws Exception
	{
		final var map = TerrainMap.parseInput(fromString("""
				y=5, x=498..502
				x=498, y=3..5
				x=502, y=3..5
				y=10, x=501..505
				x=501, y=7..10
				x=505, y=8..10
				y=18, x=496..499
				x=496, y=11..18
				x=499, y=10..18
				y=25, x=494..507
				x=494, y=22..25
				x=507, y=23..25"""));

		assertEquals("""
				....#...#.....
				....#...#.....
				....#####.....
				..............
				.......#......
				.......#...#..
				.......#...#..
				.....#.#####..
				..#..#........
				..#..#........
				..#..#........
				..#..#........
				..#..#........
				..#..#........
				..#..#........
				..####........
				..............
				..............
				..............
				#.............
				#............#
				#............#
				##############""", map.asMapString());

		final var evolvedMap = map.evolve();
		assertEquals("""
				......w........
				...wwwwwww.....
				...w#www#w.....
				...w#www#w.....
				...w#####w.....
				...w.....w.....
				...w...#wwwww..
				...w...#www#w..
				...w...#www#w..
				.wwww#.#####w..
				.w#ww#......w..
				.w#ww#......w..
				.w#ww#......w..
				.w#ww#......w..
				.w#ww#......w..
				.w#ww#......w..
				.w#ww#......w..
				.w####......w..
				.w..........w..
				.w..........w..
				.w..........w..
				#wwwwwwwwwwwwww
				#wwwwwwwwwwww#w
				#wwwwwwwwwwww#w
				##############w""", evolvedMap.asMapString());
		//assertEquals(54, evolvedMap.waterPositions().size());
		//assertEquals(Set.of(new Position(506, 8)), evolvedMap.dripPositions());
	}

	private static BufferedReader fromString(final String value)
	{
		return new BufferedReader(new StringReader(value));
	}
}
