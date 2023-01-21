package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public record BlizzardBasin(List<Blizzard> blizzards, int rows, int columns)
{

	public static BlizzardBasin parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final List<Blizzard> blizzards = Seq.zipWithIndex(allLines.stream()) //
					.flatMap(idxRow -> {
						final int y = idxRow.v2().intValue();
						final String line = idxRow.v1();
						return Seq.zipWithIndex(line.chars().boxed()) //
								.flatMap(idxChar -> {
									final int x = idxChar.v2().intValue();
									final char c = (char) idxChar.v1().intValue();
									return Direction.from(c) //
											.map(d -> new Blizzard(new Position(x, y), d)) //
											.stream();
								});
					}) //
					.toList();
			return new BlizzardBasin(blizzards, allLines.size(), allLines.get(0).length());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int computeShortestPath()
	{
		final Position startPosition = new Position(1, 0);
		final Position targetPosition = new Position(columns - 2, rows - 1);

		final List<Set<Position>> allBlizzards = computeAllBlizzards();

		final StatusWithParent finalStatus = travel( //
				new StatusWithParent(new Status(startPosition, 0), null, 0), targetPosition, allBlizzards);
		return finalStatus.steps();
	}

	public int computeShortestPath3Way()
	{
		final Position startPosition = new Position(1, 0);
		final Position targetPosition = new Position(columns - 2, rows - 1);

		final List<Set<Position>> allBlizzards = computeAllBlizzards();

		final StatusWithParent firstTrip = travel( //
				new StatusWithParent(new Status(startPosition, 0), null, 0), targetPosition, allBlizzards);

		final StatusWithParent secondTrip = travel(firstTrip, startPosition, allBlizzards);

		final StatusWithParent thridTrip = travel(secondTrip, targetPosition, allBlizzards);

		return thridTrip.steps();
	}

	@SuppressWarnings("java:S117")
	private StatusWithParent travel(final StatusWithParent start, final Position targetPosition,
			final List<Set<Position>> allBlizzards)
	{
		// Manual implementation of the BFS here is faster than the general implementation
		// in BfsVisitor

		final Deque<StatusWithParent> queue = new ArrayDeque<>(2000);
		final Set<Status> visited = new HashSet<>();
		queue.add(start);
		visited.add(start.status());

		while (!queue.isEmpty()) {
			final StatusWithParent current = queue.pop();
			final Position position = current.status().elf();
			if (position.equals(targetPosition)) {
				return current;
			}

			final var candidatePositions = Stream.of( //
							position.move(Direction.UP), //
							position.move(Direction.RIGHT), //
							position.move(Direction.DOWN), //
							position.move(Direction.LEFT), //
							position) //
					.filter(pos -> isAllowedPosition(pos, rows, columns)) //
					.toList();

			// move blizzards
			final int nextSteps = current.steps() + 1;
			final int blizzardStatusIndex = nextSteps % allBlizzards.size();
			final Set<Position> occupiedPositions = allBlizzards.get(blizzardStatusIndex);

			for (final Position candidatePosition : candidatePositions) {
				if (!occupiedPositions.contains(candidatePosition)) {
					final Status nextStatus = new Status(candidatePosition, blizzardStatusIndex);
					if (!visited.contains(nextStatus)) {
						final StatusWithParent nextStatusWithParent = new StatusWithParent(nextStatus, current, nextSteps);
						queue.add(nextStatusWithParent);
						visited.add(nextStatusWithParent.status());
					}
				}
			}
		}

		throw new IllegalStateException("Cannot find a path to target position");
	}

	private List<Set<Position>> computeAllBlizzards()
	{
		// reducing use of streams in this method makes it faster,
		// so we'll stick to this less readable version

		final Set<List<Blizzard>> seenBlizzards = new HashSet<>();
		final List<Set<Position>> allBlizzards = new ArrayList<>();
		List<Blizzard> current = blizzards;

		while (!seenBlizzards.contains(current)) {
			seenBlizzards.add(current);
			allBlizzards.add(current.stream() //
					.map(Blizzard::position) //
					.collect(StreamUtils.toUnmodifiableHashSet()));
			final List<Blizzard> movedBlizzards = new ArrayList<>(current.size());
			for (final Blizzard blizzard : current) {
				movedBlizzards.add(blizzard.move(rows, columns));
			}
			current = Collections.unmodifiableList(movedBlizzards);
		}

		return List.copyOf(allBlizzards);
	}

	private static boolean isAllowedPosition(final Position p, final int rows, final int columns)
	{
		if (p.x() == 1 && p.y() == 0) {
			return true;
		}
		if (p.x() == columns - 2 && p.y() == rows - 1) {
			return true;
		}
		return p.x() >= 1 && p.x() <= columns - 2 && p.y() >= 1 && p.y() <= rows - 2;
	}

	@RecordBuilder
	public record Position(int x, int y) implements BlizzardBasinPositionBuilder.With
	{

		public Position move(final Direction direction)
		{
			return switch (direction) {
				case UP -> this.withY(y - 1);
				case RIGHT -> this.withX(x + 1);
				case DOWN -> this.withY(y + 1);
				case LEFT -> this.withX(x - 1);
			};
		}

		@Override
		public boolean equals(final Object o)
		{
			if (!(o instanceof Position other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return x << 14 | y;
		}
	}

	@RecordBuilder
	public record Blizzard(Position position, Direction direction) implements BlizzardBasinBlizzardBuilder.With
	{
		public Blizzard move(final int rows, final int columns)
		{
			final var newPosition = position.move(direction);
			if (!isAllowedPosition(newPosition, rows, columns)) {
				return this.withPosition(switch (direction) {
					case UP -> position.withY(rows - 2);
					case RIGHT -> position.withX(1);
					case DOWN -> position.withY(1);
					case LEFT -> position.withX(columns - 2);
				});
			} else {
				return this.withPosition(newPosition);
			}
		}

		@Override
		public boolean equals(final Object o)
		{
			if (!(o instanceof Blizzard other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return position.hashCode() << 2 | direction.ordinal();
		}
	}

	private record Status(Position elf, int blizzardStatusIndex)
	{
		@Override
		public boolean equals(final Object o)
		{
			if (!(o instanceof Status other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return elf.x() << 20 | elf.y() << 10 | blizzardStatusIndex;
		}
	}

	private record StatusWithParent(Status status, StatusWithParent parent, int steps) {}

	public enum Direction
	{
		UP, RIGHT, DOWN, LEFT;

		public static Optional<Direction> from(final char c)
		{
			return switch (c) {
				case '^' -> Optional.of(UP);
				case '>' -> Optional.of(RIGHT);
				case 'v' -> Optional.of(DOWN);
				case '<' -> Optional.of(LEFT);
				default -> Optional.empty();
			};
		}
	}

}
