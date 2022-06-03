package com.mscg;

import com.mscg.CombatMap.MapNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventDay15Test
{

	@Test
	void testParse1() throws Exception
	{
		final var input = """
				#######
				#.G.E.#
				#E.G.E#
				#.G.E.#
				#######""";
		final var combatMap = CombatMap.parseInput(fromString(input));
		assertEquals(input, combatMap.toString());
		Assertions.assertEquals(List.of( //
						CombatMapPositionBuilder.Position(4, 1), //
						CombatMapPositionBuilder.Position(1, 2), //
						CombatMapPositionBuilder.Position(5, 2), //
						CombatMapPositionBuilder.Position(4, 3)), //
				getPositions(combatMap.elfs()));
		Assertions.assertEquals(List.of( //
						CombatMapPositionBuilder.Position(2, 1), //
						CombatMapPositionBuilder.Position(3, 2), //
						CombatMapPositionBuilder.Position(2, 3)), //
				getPositions(combatMap.goblins()));
		final var walkablePositions = combatMap.mapNodes().stream() //
				.collect(Collectors.toMap(MapNode::position, MapNode::neighbours));
		assertTrue(walkablePositions.containsKey(CombatMapPositionBuilder.Position(4, 2)));
		assertTrue(walkablePositions.containsKey(CombatMapPositionBuilder.Position(5, 2)));
		assertEquals(List.of( //
						CombatMapPositionBuilder.Position(4, 1), //
						CombatMapPositionBuilder.Position(5, 2), //
						CombatMapPositionBuilder.Position(4, 3), //
						CombatMapPositionBuilder.Position(3, 2)), //
				walkablePositions.get(CombatMapPositionBuilder.Position(4, 2)));
		assertEquals(List.of( //
						CombatMapPositionBuilder.Position(5, 1), //
						CombatMapPositionBuilder.Position(5, 3), //
						CombatMapPositionBuilder.Position(4, 2)), //
				walkablePositions.get(CombatMapPositionBuilder.Position(5, 2)));
	}

	@Test
	void testParse2() throws Exception
	{
		final var input = """
				#######
				#E..G.#
				#...#.#
				#.G.#G#
				#######""";
		final var combatMap = CombatMap.parseInput(fromString(input));
		assertEquals(input, combatMap.toString());
		Assertions.assertEquals(List.of( //
						CombatMapPositionBuilder.Position(1, 1)), //
				getPositions(combatMap.elfs()));
		Assertions.assertEquals(List.of( //
						CombatMapPositionBuilder.Position(4, 1), //
						CombatMapPositionBuilder.Position(2, 3), //
						CombatMapPositionBuilder.Position(5, 3)), //
				getPositions(combatMap.goblins()));
		final var walkablePositions = combatMap.mapNodes().stream() //
				.collect(Collectors.toMap(MapNode::position, MapNode::neighbours));
		assertFalse(walkablePositions.containsKey(CombatMapPositionBuilder.Position(4, 2)));
		assertTrue(walkablePositions.containsKey(CombatMapPositionBuilder.Position(5, 2)));
		assertEquals(List.of( //
						CombatMapPositionBuilder.Position(5, 1), //
						CombatMapPositionBuilder.Position(5, 3)), //
				walkablePositions.get(CombatMapPositionBuilder.Position(5, 2)));
	}

	@Test
	void testSort()
	{
		final var positions = List.of( //
				CombatMapPositionBuilder.Position(2, 1), //
				CombatMapPositionBuilder.Position(3, 2), //
				CombatMapPositionBuilder.Position(2, 3), //
				CombatMapPositionBuilder.Position(4, 1), //
				CombatMapPositionBuilder.Position(1, 2), //
				CombatMapPositionBuilder.Position(5, 2), //
				CombatMapPositionBuilder.Position(4, 3));
		Assertions.assertEquals(List.of( //
						CombatMapPositionBuilder.Position(2, 1), //
						CombatMapPositionBuilder.Position(4, 1), //
						CombatMapPositionBuilder.Position(1, 2), //
						CombatMapPositionBuilder.Position(3, 2), //
						CombatMapPositionBuilder.Position(5, 2), //
						CombatMapPositionBuilder.Position(2, 3), //
						CombatMapPositionBuilder.Position(4, 3)), //
				positions.stream().sorted().toList());
	}

	@Test
	void testEvolve1() throws Exception
	{
		final var input = """
				#######
				#.E...#
				#.....#
				#...G.#
				#######""";
		final var combatMap = CombatMap.parseInput(fromString(input));
		final var evolvedMap = combatMap.evolve();
		assertEquals("""
				#######
				#..E..#
				#...G.#
				#.....#
				#######""", evolvedMap.toString());
	}

	@Test
	void testEvolve2() throws Exception
	{
		final var input = """
				#######
				#...E.#
				#..G..#
				#...G.#
				#######""";
		final var combatMap = CombatMap.parseInput(fromString(input));
		final var evolvedMap = combatMap.evolve();
		assertEquals("""
				#######
				#..E..#
				#..GG.#
				#.....#
				#######""", evolvedMap.toString());
	}

	@Test
	void testEvolve3() throws Exception
	{
		final var input = """
				#########
				#G..G..G#
				#.......#
				#.......#
				#G..E..G#
				#.......#
				#.......#
				#G..G..G#
				#########""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));
		final List<String> evolvedMaps = combatMap.evolveUntilEnd() //
				.limit(5) //
				.map(CombatMap::toString) //
				.toList();
		assertEquals(List.of( //
				// initial state
				"""
						#########
						#G..G..G#
						#.......#
						#.......#
						#G..E..G#
						#.......#
						#.......#
						#G..G..G#
						#########""",

				// step 1
				"""
						#########
						#.G...G.#
						#...G...#
						#...E..G#
						#.G.....#
						#.......#
						#G..G..G#
						#.......#
						#########""",

				// step 2
				"""
						#########
						#..G.G..#
						#...G...#
						#.G.E.G.#
						#.......#
						#G..G..G#
						#.......#
						#.......#
						#########""",

				// step 3
				"""
						#########
						#.......#
						#..GGG..#
						#..GEG..#
						#G..G...#
						#......G#
						#.......#
						#.......#
						#########""",

				// step 4
				"""
						#########
						#.......#
						#..GGG..#
						#..GEG..#
						#G..G...#
						#......G#
						#.......#
						#.......#
						#########"""), evolvedMaps);
	}

	@Test
	void testWithCombat1() throws Exception
	{
		final var input = """
				#######
				#.G...#
				#...EG#
				#.#.#G#
				#..G#E#
				#.....#
				#######""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));
		final List<CombatMap> evolvedMaps = combatMap.evolveUntilEnd() //
				.limit(24) //
				.toList();

		assertEquals(List.of( //
						// initial state
						"""
								#######
								#.G...#
								#...EG#
								#.#.#G#
								#..G#E#
								#.....#
								#######""",

						// step 1
						"""
								#######
								#..G..#
								#...EG#
								#.#G#G#
								#...#E#
								#.....#
								#######""",

						// step 2
						"""
								#######
								#...G.#
								#..GEG#
								#.#.#G#
								#...#E#
								#.....#
								#######""",

						// step 3
						"""
								#######
								#...G.#
								#..GEG#
								#.#.#G#
								#...#E#
								#.....#
								#######"""), //
				evolvedMaps.stream().limit(4).map(CombatMap::toString).toList());

		assertArrayEquals(new int[] { 200, 197, 200, 197 }, evolvedMaps.get(1).goblins().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
		assertArrayEquals(new int[] { 197, 197 }, evolvedMaps.get(1).elfs().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());

		assertArrayEquals(new int[] { 200, 200, 194, 194 }, evolvedMaps.get(2).goblins().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
		assertArrayEquals(new int[] { 188, 194 }, evolvedMaps.get(2).elfs().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());

		assertArrayEquals(new int[] { 200, 200, 131, 131 }, evolvedMaps.get(23).goblins().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
		assertArrayEquals(new int[] { 131 }, evolvedMaps.get(23).elfs().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
	}

	@Test
	void testWithCombat2() throws Exception
	{
		final var input = """
				#######
				#.G...#
				#...EG#
				#.#.#G#
				#..G#E#
				#.....#
				#######""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));

		final List<CombatMap> evolvedMaps = combatMap.evolveUntilEnd().toList();

		assertEquals(48, evolvedMaps.size());
		assertEquals(47, evolvedMaps.get(47).fullRounds());

		assertEquals("""
						#######
						#..G..#
						#...G.#
						#.#G#G#
						#...#E#
						#.....#
						#######""", //
				evolvedMaps.get(24).toString());

		assertEquals("""
						#######
						#G....#
						#.G...#
						#.#.#G#
						#...#.#
						#....G#
						#######""", //
				evolvedMaps.get(47).toString());

		assertArrayEquals(new int[] { 200, 131, 200, 128 }, evolvedMaps.get(24).goblins().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
		assertArrayEquals(new int[] { 128 }, evolvedMaps.get(24).elfs().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());

		assertArrayEquals(new int[] { 200, 131, 59, 200 }, evolvedMaps.get(47).goblins().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());
		assertArrayEquals(new int[] {}, evolvedMaps.get(47).elfs().stream() //
				.sorted(Comparator.comparing(CombatMap.Player::position)) //
				.mapToInt(CombatMap.Player::hitPoints) //
				.toArray());

		assertEquals(27730L, combatMap.computeScore());
	}

	@Test
	void testWithCombat3() throws Exception
	{
		final var input = """
				#######
				#G..#E#
				#E#E.E#
				#G.##.#
				#...#E#
				#...E.#
				#######""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));

		final CombatMap lastMap = combatMap.evolveUntilEnd().findLast().orElseThrow();
		assertEquals("""
				#######
				#...#E#
				#E#...#
				#.E##.#
				#E..#E#
				#.....#
				#######""", lastMap.toString());
		assertEquals(37, lastMap.fullRounds());

		assertEquals(36334L, combatMap.computeScore());
	}

	@Test
	void testWithCombat4() throws Exception
	{
		final var input = """
				#######
				#E..EG#
				#.#G.E#
				#E.##E#
				#G..#.#
				#..E#.#
				#######""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));

		final CombatMap lastMap = combatMap.evolveUntilEnd().findLast().orElseThrow();
		assertEquals("""
				#######
				#.E.E.#
				#.#E..#
				#E.##.#
				#.E.#.#
				#...#.#
				#######""", lastMap.toString());
		assertEquals(46, lastMap.fullRounds());

		assertEquals(39514L, combatMap.computeScore());
	}

	@Test
	void testWithCombat5() throws Exception
	{
		final var input = """
				#######
				#E.G#.#
				#.#G..#
				#G.#.G#
				#G..#.#
				#...E.#
				#######""";

		final CombatMap combatMap = CombatMap.parseInput(fromString(input));

		final CombatMap lastMap = combatMap.evolveUntilEnd().findLast().orElseThrow();
		assertEquals("""
				#######
				#G.G#.#
				#.#G..#
				#..#..#
				#...#G#
				#...G.#
				#######""", lastMap.toString());
		assertEquals(35, lastMap.fullRounds());

		assertEquals(27755L, combatMap.computeScore());
	}

	private List<CombatMap.Position> getPositions(final List<CombatMap.Player> players)
	{
		return players.stream() //
				.map(CombatMap.Player::position) //
				.toList();
	}

	private static BufferedReader fromString(final String value)
	{
		return new BufferedReader(new StringReader(value));
	}
}
