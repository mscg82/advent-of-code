package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonkeyMapTest
{

	@Test
	void testParse() throws Exception
	{
		final var input = """
				        ...#
				        .#..
				        #...
				        ....
				...#.......#
				........#...
				..#....#....
				..........#.
				        ...#....
				        .....#..
				        .#......
				        ......#.
				    
				10R5L5R10L4R5L5
				""";
		final var mm = MonkeyMap.parseInput(new BufferedReader(new StringReader(input)));
		assertEquals(new MonkeyMap.Status(new MonkeyMap.Position(9, 1), MonkeyMap.Facing.RIGHT), mm.initialStatus());
		assertEquals(MonkeyMap.Tile.EMPTY, mm.getTile(new MonkeyMap.Position(1, 1)));
		assertEquals(MonkeyMap.Tile.OPEN, mm.getTile(new MonkeyMap.Position(9, 1)));
		assertEquals(MonkeyMap.Tile.WALL, mm.getTile(new MonkeyMap.Position(12, 1)));
		assertEquals(MonkeyMap.Tile.EMPTY, mm.getTile(new MonkeyMap.Position(13, 1)));
		assertEquals(List.of( //
						new MonkeyMap.Movement(10), //
						MonkeyMap.Rotation.RIGHT, //
						new MonkeyMap.Movement(5), //
						MonkeyMap.Rotation.LEFT, //
						new MonkeyMap.Movement(5), //
						MonkeyMap.Rotation.RIGHT, //
						new MonkeyMap.Movement(10), //
						MonkeyMap.Rotation.LEFT, //
						new MonkeyMap.Movement(4), //
						MonkeyMap.Rotation.RIGHT, //
						new MonkeyMap.Movement(5), //
						MonkeyMap.Rotation.LEFT, //
						new MonkeyMap.Movement(5)), //
				mm.instructions());
	}

	@Test
	void testPassword() throws Exception
	{
		final var input = """
				        ...#
				        .#..
				        #...
				        ....
				...#.......#
				........#...
				..#....#....
				..........#.
				        ...#....
				        .....#..
				        .#......
				        ......#.
				    
				10R5L5R10L4R5L5
				""";
		final var mm = MonkeyMap.parseInput(new BufferedReader(new StringReader(input)));
		assertEquals(6032, mm.findPassword());
	}

}
