package com.mscg;

import com.mscg.utils.Position8Bits;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record RaceCondition(Set<Position8Bits> walls, Position8Bits start, Position8Bits end, int rows, int cols)
{

	public static RaceCondition parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int rows = allLines.size();
			final int cols = allLines.getFirst().length();
			final Set<Position8Bits> walls = new HashSet<>();
			Position8Bits start = null;
			Position8Bits end = null;
			for (int y = 0; y < rows; y++) {
				final String line = allLines.get(y);
				for (int x = 0; x < cols; x++) {
					switch (line.charAt(x)) {
						case '#' -> walls.add(new Position8Bits(x, y));
						case 'S' -> start = new Position8Bits(x, y);
						case 'E' -> end = new Position8Bits(x, y);
						case '.' -> { /* do nothing */ }
						case final char c -> throw new IllegalArgumentException("Invalid character: '" + c + "'");
					}
				}
			}
			return new RaceCondition(Collections.unmodifiableSet(walls), //
					Objects.requireNonNull(start, "Start position not found"), //
					Objects.requireNonNull(end, "End position not found"), //
					rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countCheatsThatSaveAtLeast(final int minTimeToSave)
	{
		final Object2IntMap<Position8Bits> position2Distance = findPositionToDistance();

		return position2Distance.keySet().stream() //
				.flatMapToInt(cheatPosition -> timesSavedWithCheatAt(cheatPosition, position2Distance)) //
				.filter(timeSaved -> timeSaved >= minTimeToSave) //
				.count();
	}

	public long countLongCheatsThatSaveAtLeast(final int minTimeToSave)
	{
		final Object2IntMap<Position8Bits> position2Distance = findPositionToDistance();

		return position2Distance.keySet().stream() //
				.flatMapToInt(cheatPosition -> {
					final int cheatDistance = position2Distance.getInt(cheatPosition);
					return findShortcutsDestinations(cheatPosition).stream() //
							.flatMapToInt(destination -> {
								final int cheatLength = stepsDistance(cheatPosition, destination);
								final int distanceAfterCheat = position2Distance.getInt(destination);
								return distanceAfterCheat < cheatDistance ? //
										IntStream.of(cheatDistance - distanceAfterCheat - cheatLength) : //
										IntStream.empty();
							});
				}) //
				.filter(timeSaved -> timeSaved >= minTimeToSave) //
				.count();
	}

	private Object2IntMap<Position8Bits> findPositionToDistance()
	{
		final int baseTime = (rows * cols) - walls.size() - 1;

		final Object2IntMap<Position8Bits> position2Distance = new Object2IntOpenHashMap<>();
		position2Distance.defaultReturnValue(Integer.MAX_VALUE);
		position2Distance.put(start, baseTime);

		var current = start;
		var currentTime = baseTime;
		while (!current.equals(end)) {
			final Position8Bits next = adjacentPositions(current, rows, cols) //
					.filter(pos -> !position2Distance.containsKey(pos) && !walls.contains(pos)) //
					.findAny() //
					.orElseThrow(() -> new IllegalStateException("Unable to find base path next position"));
			position2Distance.put(next, --currentTime);
			current = next;
		}
		return position2Distance;
	}

	private IntStream timesSavedWithCheatAt(final Position8Bits cheatPosition, final Object2IntMap<Position8Bits> position2Distance)
	{
		final int cheatDistance = position2Distance.getInt(cheatPosition);
		return adjacentPositions(cheatPosition, rows, cols) //
				.filter(walls::contains) //
				.flatMapToInt(wallPos -> adjacentPositions(wallPos, rows, cols) //
						.filter(not(walls::contains)) //
						.mapToInt(position2Distance::getInt)) //
				.filter(distanceAfterCheat -> distanceAfterCheat < cheatDistance) //
				.map(distanceAfterCheat -> cheatDistance - distanceAfterCheat - 2);
	}

	private List<Position8Bits> findShortcutsDestinations(final Position8Bits startPosition)
	{
		final var destinations = new ArrayList<Position8Bits>();

		final var visitedPositions = HashSet.<Position8Bits>newHashSet(800);
		final var queue = new ArrayDeque<Position8Bits>(rows * cols);
		queue.add(startPosition);
		visitedPositions.add(startPosition);

		final var adjacentPositions = new ArrayList<Position8Bits>(4);

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			if (!walls.contains(current)) {
				destinations.add(current);
			}

			adjacentPositions.clear();
			adjacentPositionsIntoList(current, adjacentPositions, rows, cols);

			for (int i = 0, l = adjacentPositions.size(); i < l; i++) {
				final var pos = adjacentPositions.get(i);
				if (visitedPositions.contains(pos)) {
					continue;
				}

				final int distance = stepsDistance(pos, startPosition);
				if (distance > 20 || (distance == 20 && walls.contains(pos))) {
					continue;
				}

				queue.add(pos);
				visitedPositions.add(pos);
			}
		}

		return destinations;
	}

	private static int stepsDistance(final Position8Bits first, final Position8Bits second)
	{
		return Math.abs(first.x() - second.x()) + Math.abs(first.y() - second.y());
	}

	private static Stream<Position8Bits> adjacentPositions(final Position8Bits current, final int rows, final int cols)
	{
		return Stream.of( //
						current.withY(current.y() - 1), //
						current.withX(current.x() + 1), //
						current.withY(current.y() + 1), //
						current.withX(current.x() - 1)) //
				.filter(pos -> pos.isValid(rows, cols));
	}

	private static void adjacentPositionsIntoList(final Position8Bits current, final List<Position8Bits> result, //
			final int rows, final int cols)
	{
		var next = current.withY(current.y() - 1);
		if (next.isValid(rows, cols)) {
			result.add(next);
		}

		next = current.withX(current.x() + 1);
		if (next.isValid(rows, cols)) {
			result.add(next);
		}

		next = current.withY(current.y() + 1);
		if (next.isValid(rows, cols)) {
			result.add(next);
		}

		next = current.withX(current.x() - 1);
		if (next.isValid(rows, cols)) {
			result.add(next);
		}
	}

}
