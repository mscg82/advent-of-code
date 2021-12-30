package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RecordBuilder
public record RepairDroid(IntcodeV6 computer) implements RepairDroidBuilder.With
{

	public static RepairDroid parseInput(final BufferedReader in) throws IOException
	{
		final var computer = IntcodeV6.parseInput(in);
		return new RepairDroid(computer);
	}

	public List<Direction> findPathToOxygenSystem()
	{
		final var result = explore(true);
		return result.path();
	}

	public long findFillTime()
	{
		final var result = explore(false);

		long maxDistance = 0;
		final Set<Position> seenPositions = new HashSet<>();

		record Status(Position position, long distance) {}
		final Deque<Status> queue = new ArrayDeque<>();
		queue.add(new Status(result.oxygenSystem(), 0));

		final var allDirections = Arrays.asList(Direction.values());

		while (!queue.isEmpty()) {
			final var status = queue.pop();
			if (seenPositions.contains(status.position())) {
				continue;
			}
			seenPositions.add(status.position());
			if (maxDistance < status.distance()) {
				maxDistance = status.distance();
			}
			allDirections.stream() //
					.map(status.position()::move) //
					.filter(result.corridors::contains) //
					.filter(pos -> !seenPositions.contains(pos)) //
					.forEach(pos -> queue.add(new Status(pos, status.distance() + 1)));
		}

		return maxDistance;
	}

	private ExplorationResult explore(final boolean stopAtOxygen)
	{
		List<Direction> path = null;
		Position oxygenSystem = null;
		final Set<Position> corridors = new HashSet<>();
		final Set<Position> walls = new HashSet<>();
		final Set<Position> seenPositions = new HashSet<>();

		record Status(Position position, RepairDroid droid, List<Direction> path) {}
		final Deque<Status> queue = new ArrayDeque<>();
		queue.add(new Status(new Position(0, 0), this, List.of()));

		final var allDirections = Arrays.asList(Direction.values());

		mainLoop:
		while (!queue.isEmpty()) {
			final var status = queue.pop();
			final RepairDroid droid = status.droid();
			final Position position = status.position();
			seenPositions.add(position);

			final var directionsToExplore = allDirections.stream() //
					.filter(dir -> !walls.contains(position.move(dir))) //
					.toList();

			for (final var direction : directionsToExplore) {
				final var computer = droid.computer().execute(direction, 1);
				final Position newPosition = position.move(direction);
				switch ((int) computer.outputs()[0]) {
					case 0 -> walls.add(newPosition); // we found a new wall
					case 1 -> {
						corridors.add(newPosition);
						if (!seenPositions.contains(newPosition)) {
							final var newStatus = new Status(newPosition, droid.withComputer(computer),
									append(status.path(), direction));
							queue.add(newStatus);
						}
					}
					case 2 -> {
						// we found the oxygen system
						oxygenSystem = newPosition;
						path = append(status.path, direction);
						if (stopAtOxygen) {
							break mainLoop;
						}
					}
					default -> throw new IllegalStateException("Unvalid output " + computer.outputs()[0]);
				}
			}

		}

		if (oxygenSystem == null || path == null) {
			throw new IllegalStateException("Unable to find oxygen system");
		}
		return new ExplorationResult(corridors, path, oxygenSystem);
	}

	private <E> List<E> append(final List<E> source, final E elem)
	{
		return Stream.concat(source.stream(), Stream.of(elem)).toList();
	}

	private record ExplorationResult(Set<Position> corridors, List<Direction> path, Position oxygenSystem) {}

	@RecordBuilder
	public record Position(long x, long y) implements RepairDroidPositionBuilder.With
	{

		public Position move(final Direction direction)
		{
			return switch (direction) {
				case NORTH -> this.withY(this.y() - 1);
				case SOUTH -> this.withY(this.y() + 1);
				case WEST -> this.withX(this.x() - 1);
				case EAST -> this.withX(this.x() + 1);
			};
		}

	}

	@RequiredArgsConstructor
	public enum Direction implements IntcodeV6.InputGenerator
	{
		NORTH(1), SOUTH(2), WEST(3), EAST(4);

		private final long input;

		@Override
		public long next()
		{
			return input;
		}
	}

}
