package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Predicate.not;

public record RegularMap(Map<Position, Set<Position>> adjacencyMap)
{

	public static RegularMap parseInput(final BufferedReader in) throws IOException
	{
		String line = in.readLine();
		line = line.substring(1, line.length() - 1); // skip ^ and $ delimiters
		final Map<Position, Set<Position>> adjacencyMap = new HashMap<>();
		followPaths(new Position(0, 0), line, 0, adjacencyMap);
		adjacencyMap.replaceAll((pos, adjs) -> Collections.unmodifiableSet(adjs));
		return new RegularMap(Collections.unmodifiableMap(adjacencyMap));
	}

	public long findLongestPath()
	{
		final Map<Position, List<Position>> paths = computePaths();

		return paths.values().stream() //
				.mapToLong(List::size) //
				.max() //
				.orElseThrow();
	}

	public long findRoomsWithDistanceGreaterThan(final int minDistance)
	{
		final Map<Position, List<Position>> paths = computePaths();

		return paths.values().stream() //
				.filter(path -> path.size() >= minDistance) //
				.count();
	}

	private Map<Position, List<Position>> computePaths()
	{
		final Map<Position, Position> parents = computeParentsInShortestPaths();

		return followParentsUntilInitialNode(parents);
	}

	private Map<Position, Position> computeParentsInShortestPaths()
	{
		final Map<Position, Position> parents = new HashMap<>();

		final Set<Position> seenPositions = new HashSet<>();
		final Deque<Position> queue = new ArrayDeque<>();
		final Position initialPosition = new Position(0, 0);
		queue.add(initialPosition);
		seenPositions.add(initialPosition);

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			adjacencyMap.get(current).stream() //
					.filter(not(seenPositions::contains)) //
					.forEach(pos -> {
						seenPositions.add(pos);
						queue.add(pos);
						parents.put(pos, current);
					});
		}
		return parents;
	}

	private Map<Position, List<Position>> followParentsUntilInitialNode(final Map<Position, Position> parents)
	{
		final Map<Position, List<Position>> paths = new HashMap<>();
		for (final var position : adjacencyMap.keySet()) {
			final ArrayList<Position> path = new ArrayList<>();
			paths.put(position, path);
			var parent = parents.get(position);
			while (parent != null) {
				path.add(parent);
				parent = parents.get(parent);
			}
		}
		return paths;
	}

	private static int followPaths(final Position startPosition, final String line, int index,
			final Map<Position, Set<Position>> adjacencyMap)
	{
		Position currentPosition = startPosition;
		while (index < line.length()) {
			final char c = line.charAt(index);
			if (c == '(') {
				index = followPaths(currentPosition, line, index + 1, adjacencyMap);
			} else if (c == ')') {
				index++;
				break;
			} else if (c == '|') {
				index++;
				currentPosition = startPosition;
			} else {
				final var newPosition = currentPosition.move(Direction.from(c));
				adjacencyMap.computeIfAbsent(currentPosition, __ -> new LinkedHashSet<>()).add(newPosition);
				adjacencyMap.computeIfAbsent(newPosition, __ -> new LinkedHashSet<>()).add(currentPosition);
				currentPosition = newPosition;
				index++;
			}
		}
		return index;
	}

	@RecordBuilder
	public record Position(int x, int y) implements RegularMapPositionBuilder.With
	{

		public Position move(final Direction direction)
		{
			return switch (direction) {
				case NORTH -> this.withY(y - 1);
				case EAST -> this.withX(x + 1);
				case SOUTH -> this.withY(y + 1);
				case WEST -> this.withX(x - 1);
			};
		}

		@Override
		public String toString()
		{
			return "(" + x + ", " + y + ")";
		}
	}

	public enum Direction
	{
		NORTH, EAST, SOUTH, WEST;

		public static Direction from(final char c)
		{
			return switch (c) {
				case 'N' -> NORTH;
				case 'E' -> EAST;
				case 'S' -> SOUTH;
				case 'W' -> WEST;
				default -> throw new IllegalArgumentException("Unsupported direction " + c);
			};
		}
	}
}
