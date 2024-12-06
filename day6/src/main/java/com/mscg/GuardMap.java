package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GuardMap(Set<Position> obstacles, Guard guard, int rows, int cols)
{

	public static GuardMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int rows = allLines.size();
			final int cols = allLines.getFirst().length();
			final Set<Position> obstacles = new HashSet<>();
			Guard guard = null;
			for (int y = 0; y < rows; y++) {
				final String row = allLines.get(y);
				for (int x = 0; x < cols; x++) {
					switch (row.charAt(x)) {
						case '#' -> obstacles.add(new Position(x, y));
						case '^' -> guard = new Guard(new Position(x, y), Direction.UP);
						case '.' -> { /* do nothing */}
						default -> throw new IllegalArgumentException("Invalid row: " + row);
					}
				}
			}
			return new GuardMap(Collections.unmodifiableSet(obstacles), Objects.requireNonNull(guard), rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countPositionsVisitedByGuard()
	{
		return generatePositionsVisitedByGuard().size();
	}

	public long countPositionThatGenerateLoop()
	{
		long positions = 0;
		final Set<Position> possibleObstacles = generatePositionsVisitedByGuard();
		for (final Position newObstacle : possibleObstacles) {
			if (obstacles.contains(newObstacle) || guard.position().equals(newObstacle)) {
				continue;
			}
			if (hasLoop(pos -> isObstacle(newObstacle, pos))) {
				positions++;
			}
		}
		return positions;
	}

	private Set<Position> generatePositionsVisitedByGuard()
	{
		return Stream.iterate(guard, //
						newGuard -> {
							final Position position = newGuard.position();
							return position.x() >= 0 && position.x() < cols && position.y() >= 0 && position.y() < rows;
						}, //
						prevGuard -> prevGuard.move(obstacles::contains)) //
				.map(Guard::position) //
				.collect(Collectors.toSet());
	}

	private boolean isObstacle(final Position otherObstacle, final Position toTest)
	{
		return toTest.equals(otherObstacle) || obstacles.contains(toTest);
	}

	private boolean hasLoop(final Predicate<Position> obstacleTester)
	{
		final Set<Guard> visited = HashSet.newHashSet(rows * cols * 4);
		var status = guard;
		while (!visited.contains(status)) {
			visited.add(status);
			status = status.move(obstacleTester);
			final Position position = status.position();
			if (position.x() < 0 || position.x() >= cols || position.y() < 0 || position.y() >= rows) {
				return false;
			}
		}
		return true;
	}

	public record Guard(Position position, Direction direction)
	{
		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Guard other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			int hash = position.hashCode();
			hash = hash << 2 | direction.ordinal();
			return hash;
		}

		public Guard move(final Predicate<Position> obstacleTester)
		{
			final Position advancedPosition = position.advance(direction);
			if (obstacleTester.test(advancedPosition)) {
				return new Guard(position, direction.rotate());
			}
			return new Guard(advancedPosition, direction);
		}
	}

	public record Position(int x, int y)
	{
		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Position other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return y << 8 | x;
		}

		public Position advance(final Direction direction)
		{
			return switch (direction) {
				case UP -> new Position(x, y - 1);
				case RIGHT -> new Position(x + 1, y);
				case DOWN -> new Position(x, y + 1);
				case LEFT -> new Position(x - 1, y);
			};
		}
	}

	public enum Direction
	{
		UP, RIGHT, DOWN, LEFT;

		public Direction rotate()
		{
			return switch (this) {
				case UP -> RIGHT;
				case RIGHT -> DOWN;
				case DOWN -> LEFT;
				case LEFT -> UP;
			};
		}
	}
}
