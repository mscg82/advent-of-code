package com.mscg;

import com.mscg.utils.Position8Bits;
import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Warehouse(Set<Crate> crates, Map<Position8Bits, Crate> cratesOccupiedPositions, Set<Position8Bits> walls,
						Position8Bits robot, List<Direction> instructions, int rows, int cols)
{
	public static Warehouse parseInput(final BufferedReader in, final boolean expand) throws IOException
	{
		try {
			final List<List<String>> blocks = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.toList();

			@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
			final List<String> mapLines = blocks.get(0).stream() //
					.map(line -> {
						if (expand) {
							final StringBuilder expandedLine = new StringBuilder(line.length() * 2);
							for (int i = 0, l = line.length(); i < l; i++) {
								expandedLine.append(switch (line.charAt(i)) {
									case '#' -> "##";
									case 'O' -> "[]";
									case '@' -> "@.";
									case '.' -> "..";
									case final char c -> throw new IllegalArgumentException("invalid map character: '" + c + "'");
								});
							}
							return expandedLine.toString();
						} else {
							return line;
						}
					}).toList();

			final Set<Crate> crates = new HashSet<>();
			final Set<Position8Bits> walls = new HashSet<>();
			Position8Bits robot = null;

			final int rows = mapLines.size();
			final int cols = mapLines.getFirst().length();

			for (int y = 0, l = mapLines.size(); y < l; y++) {
				final String line = mapLines.get(y);
				for (int x = 0, w = line.length(); x < w; x++) {
					switch (line.charAt(x)) {
						case '#' -> walls.add(new Position8Bits(x, y));
						case 'O' -> crates.add(new Crate(new Position8Bits(x, y), new Position8Bits(x, y)));
						case '[' -> crates.add(new Crate(new Position8Bits(x, y), new Position8Bits(x + 1, y)));
						case ']' -> { /* do nothing, we handled this case in the [ branch */ }
						case '@' -> robot = new Position8Bits(x, y);
						case '.' -> { /* do nothing */ }
						case final char c -> throw new IllegalArgumentException("invalid map character: '" + c + "'");
					}

				}
			}

			final List<Direction> instructions = blocks.get(1).stream() //
					.flatMapToInt(String::chars) //
					.mapToObj(c -> Direction.from((char) c)) //
					.toList();

			return new Warehouse(Collections.unmodifiableSet(crates), Collections.unmodifiableSet(walls), robot, instructions, rows,
					cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public Warehouse(final Set<Crate> crates, final Set<Position8Bits> walls, final Position8Bits robot,
			final List<Direction> instructions, final int rows, final int cols)
	{
		this(crates, createCratesOccupiedPositions(crates), walls, robot, instructions, rows, cols);
	}

	public long sumGPSCoordinatesAfterRun()
	{
		final var warehouse = runAllInstructions();
		return warehouse.crates.stream() //
				.mapToLong(crate -> crate.left().y() * 100L + crate.left().x()) //
				.sum();
	}

	Warehouse runAllInstructions()
	{
		var warehouse = this;
		for (final Direction instruction : instructions) {
			warehouse = warehouse.executeMovement(instruction);
		}
		return warehouse;
	}

	Warehouse executeMovement(final Direction direction)
	{
		final var newRobotPosition = direction.move(robot);

		if (walls.contains(newRobotPosition)) {
			return this;
		}

		final var crate = cratesOccupiedPositions.get(newRobotPosition);
		if (crate != null) {
			final Set<Crate> cratesToMove = new HashSet<>();
			cratesToMove.add(crate);
			final var queue = new ArrayDeque<Position8Bits>();
			crate.positionStream(direction).forEach(queue::add);
			while (!queue.isEmpty()) {
				final var current = queue.pop();
				final var newPos = direction.move(current);
				if (walls.contains(newPos)) {
					return this;
				}
				final var nextCrate = cratesOccupiedPositions.get(newPos);
				if (nextCrate != null) {
					cratesToMove.add(nextCrate);
					nextCrate.positionStream(direction).forEach(queue::add);
				}
			}

			final var newCrates = new HashSet<>(crates);
			newCrates.removeAll(cratesToMove);
			cratesToMove.stream() //
					.map(crateToMove -> new Crate(direction.move(crateToMove.left()), direction.move(crateToMove.right()))) //
					.forEach(newCrates::add);
			return new Warehouse(Collections.unmodifiableSet(newCrates), walls, newRobotPosition, instructions, rows, cols);
		}

		return new Warehouse(crates, walls, newRobotPosition, instructions, rows, cols);
	}

	String toVisualizationString()
	{
		return IntStream.range(0, rows) //
				.mapToObj(y -> IntStream.range(0, cols) //
						.mapToObj(x -> new Position8Bits(x, y)) //
						.map(pos -> {
							if (walls.contains(pos)) {
								return "#";
							}
							final var crate = cratesOccupiedPositions.get(pos);
							if (crate != null) {
								return crate.toStringAtPosition(pos);
							}
							if (robot.equals(pos)) {
								return "@";
							}
							return ".";
						}) //
						.collect(Collectors.joining()) //
				) //
				.collect(Collectors.joining("\n"));
	}

	private static Map<Position8Bits, Crate> createCratesOccupiedPositions(final Set<Crate> crates)
	{
		final Map<Position8Bits, Crate> cratesOccupiedPositions = HashMap.newHashMap(crates.size() * 2);
		for (final Crate crate : crates) {
			cratesOccupiedPositions.put(crate.left(), crate);
			cratesOccupiedPositions.put(crate.right(), crate);
		}
		return cratesOccupiedPositions;
	}

	public record Crate(Position8Bits left, Position8Bits right)
	{
		public Stream<Position8Bits> positionStream(final Direction direction)
		{
			if (left.equals(right)) {
				return Stream.of(left);
			}
			return switch (direction) {
				case UP, DOWN -> Stream.of(left, right);
				case LEFT -> Stream.of(left);
				case RIGHT -> Stream.of(right);
			};
		}

		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Position8Bits other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return left.hashCode() << 16 | right.hashCode();
		}

		String toStringAtPosition(final Position8Bits position)
		{
			if (left.equals(right)) {
				return "O";
			}
			if (left.equals(position)) {
				return "[";
			}
			return "]";
		}
	}

	public enum Direction
	{
		UP, RIGHT, DOWN, LEFT;

		public static Direction from(final char c)
		{
			return switch (c) {
				case '^' -> UP;
				case '>' -> RIGHT;
				case 'v' -> DOWN;
				case '<' -> LEFT;
				default -> throw new IllegalArgumentException("invalid direction: '" + c + "'");
			};
		}

		public Position8Bits move(final Position8Bits position)
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
