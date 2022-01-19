package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record GameConsole(IntcodeV6 computer)
{

	public static GameConsole parseInput(final BufferedReader in) throws IOException
	{
		return new GameConsole(IntcodeV6.parseInput(in));
	}

	public long runAndCountBlocks()
	{
		final Map<Position, Tile> screen = new HashMap<>();
		var computer = this.computer;
		while (true) {
			computer = computer.execute(Collections.emptyIterator(), 3);
			if (computer.halted()) {
				break;
			}
			final var position = new Position(computer.outputs()[0], computer.outputs()[1]);
			final var tile = Tile.from((int) computer.outputs()[2]);
			screen.put(position, tile);
		}

		return screen.values().stream() //
				.filter(tile -> tile == Tile.BLOCK) //
				.count();
	}

	public long runAndFindMaxScore()
	{
		final var positionHolder = new PositionsHolder();

		var computer = this.computer.withUpdatedData(data -> data[0] = 2);
		long score = Long.MIN_VALUE;
		final IntcodeV6.InputGenerator input = () -> Long.signum(
				Long.compare(positionHolder.ballPosition.x(), positionHolder.paddlePosition.x()));
		while (true) {
			computer = computer.execute(input, 3);
			if (computer.halted()) {
				break;
			}
			if (computer.outputs()[0] == -1L && computer.outputs()[1] == 0) {
				score = computer.outputs()[2];
			} else {
				final var position = new Position(computer.outputs()[0], computer.outputs()[1]);
				final var tile = Tile.from((int) computer.outputs()[2]);
				if (tile == Tile.BALL) {
					positionHolder.ballPosition = position;
				} else if (tile == Tile.PADDLE) {
					positionHolder.paddlePosition = position;
				}
			}
		}

		return score;
	}

	public record Position(long x, long y)
	{

	}

	public enum Tile
	{
		EMPTY, WALL, BLOCK, PADDLE, BALL;

		public static Tile from(final int value)
		{
			return switch (value) {
				case 0 -> EMPTY;
				case 1 -> WALL;
				case 2 -> BLOCK;
				case 3 -> PADDLE;
				case 4 -> BALL;
				default -> throw new IllegalArgumentException("Unsupported value " + value);
			};
		}
	}

	private static class PositionsHolder
	{
		Position ballPosition = new Position(0, 0);

		Position paddlePosition = new Position(0, 0);
	}
}
