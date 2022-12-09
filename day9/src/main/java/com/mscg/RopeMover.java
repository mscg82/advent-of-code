package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public record RopeMover(List<Instruction> instructions)
{
	public static RopeMover parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = in.lines() //
					.map(Instruction::from) //
					.toList();
			return new RopeMover(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countTailPositions()
	{
		return simulateRope(2);
	}

	public long countLongTailPositions()
	{
		return simulateRope(10);
	}

	private long simulateRope(final int length)
	{
		final var knots = Stream.generate(() -> Position.origin) //
				.limit(length) //
				.toArray(Position[]::new);

		final var tailPositions = new HashSet<Position>();
		tailPositions.add(knots[length - 1]);

		for (final var instruction : instructions) {
			for (int i = 0; i < instruction.amount(); i++) {
				knots[0] = knots[0].move(instruction.direction());
				for (int j = 1; j < knots.length; j++) {
					if (!knots[j].isAdjacent(knots[j - 1])) {
						final int dx = (int) Math.signum((float) knots[j - 1].x() - knots[j].x());
						final int dy = (int) Math.signum((float) knots[j - 1].y() - knots[j].y());
						knots[j] = knots[j].moveBy(dx, dy);
					}
				}
				tailPositions.add(knots[length - 1]);
			}
		}

		return tailPositions.size();
	}

	@RecordBuilder
	public record Position(int x, int y) implements RopeMoverPositionBuilder.With
	{
		public static final Position origin = new Position(0, 0);

		public boolean isAdjacent(final Position other)
		{
			final int distX = Math.abs(x - other.x);
			final int distY = Math.abs(y - other.y);
			final int distance = distX + distY;
			return distance <= 1 || (distance == 2 && distX == 1 && distY == 1);
		}

		public Position moveBy(final int dx, final int dy)
		{
			return this.with(pos -> pos //
					.x(pos.x() + dx) //
					.y(pos.y() + dy));
		}

		public Position move(final Direction direction)
		{
			return switch (direction) {
				case UP -> moveBy(0, -1);
				case RIGTH -> moveBy(1, 0);
				case DOWN -> moveBy(0, 1);
				case LEFT -> moveBy(-1, 0);
			};
		}
	}

	public record Instruction(Direction direction, int amount)
	{
		public static Instruction from(final String value)
		{
			final String[] parts = value.split(" ");
			return new Instruction(Direction.from(parts[0]), Integer.parseInt(parts[1]));
		}
	}

	public enum Direction
	{
		UP, RIGTH, DOWN, LEFT;

		public static Direction from(final String value)
		{
			return switch (value) {
				case "U" -> UP;
				case "R" -> RIGTH;
				case "D" -> DOWN;
				case "L" -> LEFT;
				default -> throw new IllegalArgumentException("Unsupported direction " + value);
			};
		}
	}
}
