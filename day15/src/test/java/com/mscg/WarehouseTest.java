package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WarehouseTest
{

	@Test
	void testExecuteMovement1() throws Exception
	{
		var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				########
				#..O.O.#
				##@.O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########
				
				<^^>>>vv<v>>v<<"""), false);

		assertEquals("""
				########
				#..O.O.#
				##@.O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		int instructionPointer = 0;
		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#..O.O.#
				##@.O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#.@O.O.#
				##..O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#.@O.O.#
				##..O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#..@OO.#
				##..O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#...@OO#
				##..O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				########
				#...@OO#
				##..O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer));
		assertEquals("""
				########
				#....OO#
				##..@..#
				#...O..#
				#.#.O..#
				#...O..#
				#...O..#
				########""", warehouse.toVisualizationString());
	}

	@Test
	void testRunAllInstructions() throws Exception
	{
		final var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				########
				#..O.O.#
				##@.O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########
				
				<^^>>>vv<v>>v<<"""), false);

		assertEquals("""
				########
				#....OO#
				##.....#
				#.....O#
				#.#O@..#
				#...O..#
				#...O..#
				########""", warehouse.runAllInstructions().toVisualizationString());
	}

	@Test
	void testSumGPSCoordinatesAfterRun1() throws Exception
	{
		final var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				########
				#..O.O.#
				##@.O..#
				#...O..#
				#.#.O..#
				#...O..#
				#......#
				########
				
				<^^>>>vv<v>>v<<"""), false);

		assertEquals(2028, warehouse.sumGPSCoordinatesAfterRun());
	}

	@Test
	void testSumGPSCoordinatesAfterRun2() throws Exception
	{
		final var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				##########
				#..O..O.O#
				#......O.#
				#.OO..O.O#
				#..O@..O.#
				#O#..O...#
				#O..O..O.#
				#.OO.O.OO#
				#....O...#
				##########
				
				<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
				vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
				><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
				<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
				^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
				^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
				>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
				<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
				^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
				v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"""), false);

		assertEquals(10092, warehouse.sumGPSCoordinatesAfterRun());
	}

	@Test
	void testParseExtended() throws Exception
	{
		final var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				##########
				#..O..O.O#
				#......O.#
				#.OO..O.O#
				#..O@..O.#
				#O#..O...#
				#O..O..O.#
				#.OO.O.OO#
				#....O...#
				##########
				
				<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
				vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
				><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
				<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
				^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
				^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
				>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
				<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
				^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
				v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"""), true);

		assertEquals("""
				####################
				##....[]....[]..[]##
				##............[]..##
				##..[][]....[]..[]##
				##....[]@.....[]..##
				##[]##....[]......##
				##[]....[]....[]..##
				##..[][]..[]..[][]##
				##........[]......##
				####################""", warehouse.toVisualizationString());
	}

	@Test
	void testExecuteMovement2() throws Exception
	{
		var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				#######
				#...#.#
				#.....#
				#..OO@#
				#..O..#
				#.....#
				#######
				
				<vv<<^^<<^^"""), true);

		assertEquals("""
				##############
				##......##..##
				##..........##
				##....[][]@.##
				##....[]....##
				##..........##
				##############""", warehouse.toVisualizationString());

		int instructionPointer = 0;
		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##..........##
				##...[][]@..##
				##....[]....##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##..........##
				##...[][]...##
				##....[].@..##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##..........##
				##...[][]...##
				##....[]....##
				##.......@..##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##..........##
				##...[][]...##
				##....[]....##
				##......@...##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##..........##
				##...[][]...##
				##....[]....##
				##.....@....##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##...[][]...##
				##....[]....##
				##.....@....##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##...[][]...##
				##....[]....##
				##.....@....##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##...[][]...##
				##....[]....##
				##....@.....##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##...[][]...##
				##....[]....##
				##...@......##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer++));
		assertEquals("""
				##############
				##......##..##
				##...[][]...##
				##...@[]....##
				##..........##
				##..........##
				##############""", warehouse.toVisualizationString());

		warehouse = warehouse.executeMovement(warehouse.instructions().get(instructionPointer));
		assertEquals("""
				##############
				##...[].##..##
				##...@.[]...##
				##....[]....##
				##..........##
				##..........##
				##############""", warehouse.toVisualizationString());
	}

	@Test
	void testSumGPSCoordinatesAfterRun3() throws Exception
	{
		final var warehouse = Warehouse.parseInput(InputUtils.readInput("""
				##########
				#..O..O.O#
				#......O.#
				#.OO..O.O#
				#..O@..O.#
				#O#..O...#
				#O..O..O.#
				#.OO.O.OO#
				#....O...#
				##########
				
				<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
				vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
				><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
				<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
				^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
				^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
				>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
				<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
				^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
				v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"""), true);

		assertEquals(9021, warehouse.sumGPSCoordinatesAfterRun());
	}

}
