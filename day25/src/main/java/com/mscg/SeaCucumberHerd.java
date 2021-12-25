package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SeaCucumberHerd(Map<Type, List<Position>> initialPositions, int rows, int cols)
{

	public static SeaCucumberHerd parseInput(final BufferedReader in) throws IOException
	{
		final Map<Type, List<Position>> initialPositions = new EnumMap<>(Type.class);
		initialPositions.put(Type.EAST, new ArrayList<>());
		initialPositions.put(Type.SOUTH, new ArrayList<>());

		int y = 0;
		String line;
		int cols = 0;
		while ((line = in.readLine()) != null) {
			cols = Math.max(cols, line.length());
			for (int x = 0, l = line.length(); x < l; x++) {
				switch (line.charAt(x)) {
					case '>' -> initialPositions.get(Type.EAST).add(new Position(x, y));
					case 'v' -> initialPositions.get(Type.SOUTH).add(new Position(x, y));
					case '.' -> {
						// do nothing here
					}
					default -> throw new IllegalArgumentException(
							"Unsupported char " + line.charAt(x) + " at position (" + x + ", " + y + ")");
				}
			}
			y++;
		}
		initialPositions.replaceAll((type, pos) -> List.copyOf(pos));

		return new SeaCucumberHerd(Collections.unmodifiableMap(initialPositions), y, cols);
	}

	public long findStepsToHalt()
	{
		Map<Type, List<Position>> positions = initialPositions;
		Set<Position> occupiedPositions = positions.values().stream() //
				.flatMap(List::stream) //
				.collect(Collectors.toSet());

		long steps = 0;
		while (true) {
			steps++;

			final Map<Type, List<Position>> newPositions = new EnumMap<>(Type.class);
			newPositions.put(Type.EAST, new ArrayList<>());
			newPositions.put(Type.SOUTH, new ArrayList<>());

			boolean changed = false;
			final List<Position> newPositionsEast = newPositions.get(Type.EAST);
			for (final var pos : positions.get(Type.EAST)) {
				final var newPos = pos.next(Type.EAST, rows, cols);
				if (occupiedPositions.contains(newPos)) {
					newPositionsEast.add(pos);
				} else {
					newPositionsEast.add(newPos);
					changed = true;
				}
			}
			occupiedPositions = Stream.concat(newPositionsEast.stream(), positions.get(Type.SOUTH).stream()) //
					.collect(Collectors.toSet());

			final List<Position> newPositionsSouth = newPositions.get(Type.SOUTH);
			for (final var pos : positions.get(Type.SOUTH)) {
				final var newPos = pos.next(Type.SOUTH, rows, cols);
				if (occupiedPositions.contains(newPos)) {
					newPositionsSouth.add(pos);
				} else {
					newPositionsSouth.add(newPos);
					changed = true;
				}
			}

			if (!changed) {
				break;
			}

			positions = newPositions;
			occupiedPositions = positions.values().stream() //
					.flatMap(List::stream) //
					.collect(Collectors.toSet());
		}

		return steps;
	}

	public enum Type
	{
		SOUTH, EAST
	}

	@RecordBuilder
	public record Position(int x, int y) implements SeaCucumberHerdPositionBuilder.With
	{

		public Position next(final Type type, final int rows, final int cols)
		{
			return switch (type) {
				case EAST -> this.withX((x + 1) % cols);
				case SOUTH -> this.withY((y + 1) % rows);
			};
		}

	}

}
