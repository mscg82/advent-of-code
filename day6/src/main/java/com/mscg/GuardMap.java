package com.mscg;

import com.mscg.utils.Position8Bits;

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

public record GuardMap(Set<Position8Bits> obstacles, Guard guard, int rows, int cols)
{

	public static GuardMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int rows = allLines.size();
			final int cols = allLines.getFirst().length();
			final Set<Position8Bits> obstacles = new HashSet<>();
			Guard guard = null;
			for (int y = 0; y < rows; y++) {
				final String row = allLines.get(y);
				for (int x = 0; x < cols; x++) {
					switch (row.charAt(x)) {
						case '#' -> obstacles.add(new Position8Bits(x, y));
						case '^' -> guard = new Guard(new Position8Bits(x, y), Direction.UP);
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
		final Set<Position8Bits> possibleObstacles = generatePositionsVisitedByGuard();
		for (final Position8Bits newObstacle : possibleObstacles) {
			if (obstacles.contains(newObstacle) || guard.position().equals(newObstacle)) {
				continue;
			}
			if (hasLoop(pos -> isObstacle(newObstacle, pos))) {
				positions++;
			}
		}
		return positions;
	}

	private Set<Position8Bits> generatePositionsVisitedByGuard()
	{
		return Stream.iterate(guard, //
						newGuard -> {
							final Position8Bits position = newGuard.position();
							return position.x() >= 0 && position.x() < cols && position.y() >= 0 && position.y() < rows;
						}, //
						prevGuard -> prevGuard.move(obstacles::contains)) //
				.map(Guard::position) //
				.collect(Collectors.toSet());
	}

	private boolean isObstacle(final Position8Bits otherObstacle, final Position8Bits toTest)
	{
		return toTest.isValid(rows, cols) && (toTest.equals(otherObstacle) || obstacles.contains(toTest));
	}

	private boolean hasLoop(final Predicate<Position8Bits> obstacleTester)
	{
		final Set<Guard> visited = HashSet.newHashSet(rows * cols * 4);
		var status = guard;
		while (!visited.contains(status)) {
			visited.add(status);
			status = status.move(obstacleTester);
			final Position8Bits position = status.position();
			if (!position.isValid(rows, cols)) {
				return false;
			}
		}
		return true;
	}

	public record Guard(Position8Bits position, Direction direction)
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

		public Guard move(final Predicate<Position8Bits> obstacleTester)
		{
			final Position8Bits advancedPosition = direction.advance(position);
			if (obstacleTester.test(advancedPosition)) {
				return new Guard(position, direction.rotate());
			}
			return new Guard(advancedPosition, direction);
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

		public Position8Bits advance(final Position8Bits position)
		{
			return switch (this) {
				case UP -> position.withY(position.y() - 1);
				case RIGHT -> position.withX(position.x() + 1);
				case DOWN -> position.withY(position.y() + 1);
				case LEFT -> position.withX(position.x() - 1);
			};
		}
	}
}
