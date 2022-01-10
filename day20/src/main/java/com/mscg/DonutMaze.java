package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DonutMaze(Position start, Position exit, Set<Position> corridors, Map<String, List<Position>> labels, int rows,
						int cols)
{
	@SuppressWarnings("java:S3776")
	public static DonutMaze parseInput(final BufferedReader in) throws IOException
	{
		final int[][] inputChars;
		try {
			inputChars = in.lines() //
					.map(line -> line.chars().toArray()) //
					.toArray(int[][]::new);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final int rows = inputChars.length - 4;
		final int cols = (int) Arrays.stream(inputChars[2]).filter(c -> (char) c != ' ').count();

		final Set<Position> corridors = new LinkedHashSet<>();
		final Map<String, List<Position>> labelsToPositions = new HashMap<>();

		for (int y = 0; y < inputChars.length; y++) {
			final int[] row = inputChars[y];
			for (int x = 0; x < row.length; x++) {
				final char c = (char) row[x];
				if (c == '.') {
					corridors.add(new Position(x - 2, y - 2, 0));
				} else if (Character.isAlphabetic(c)) {
					// check if it's first char in label
					boolean isHor = true;
					char n = x < row.length - 1 ? (char) row[x + 1] : '\0';
					if (!Character.isAlphabetic(n)) {
						isHor = false;
						if (y < inputChars.length - 1) {
							final int[] upperRow = inputChars[y + 1];
							if (x < upperRow.length) {
								n = (char) upperRow[x];
							} else {
								n = '\0';
							}
						} else {
							n = '\0';
						}
					}

					if (Character.isAlphabetic(n)) {
						final String label = String.valueOf(c) + n;
						final Position p;
						if (isHor) {
							if (x != 0 && row[x - 1] == '.') {
								p = new Position(x - 2, y - 2, 0);
							} else {
								p = new Position(x - 1, y - 2, 0);
							}
						} else {
							if (y != 0 && inputChars[y - 1][x] == '.') {
								p = new Position(x - 2, y - 2, 0);
							} else {
								p = new Position(x - 2, y - 1, 0);
							}
						}
						labelsToPositions.computeIfAbsent(label, ignore -> new ArrayList<>()).add(p);
					}
				} else if (c != ' ' && c != '#') {
					throw new IllegalArgumentException("Unsupported character " + c);
				}
			}
		}

		final Position start = nearestCorridor(labelsToPositions.get("AA").get(0), corridors);
		final Position exit = nearestCorridor(labelsToPositions.get("ZZ").get(0), corridors);

		return new DonutMaze(start, exit, corridors, Map.copyOf(labelsToPositions), rows, cols);
	}

	private static <T> List<T> concat(final List<T> list, final T newElement)
	{
		return Stream.concat(list.stream(), Stream.of(newElement)).toList();
	}

	private static Position nearestCorridor(final Position pos, final Set<Position> corridors)
	{
		return pos.neighbours() //
				.filter(corridors::contains) //
				.findFirst() //
				.orElseThrow();
	}

	@SuppressWarnings("java:S1481")
	public List<Position> findShortestPath()
	{
		final Map<Position, Position> portals = labels.values().stream() //
				.filter(positions -> positions.size() == 2) //
				.flatMap(positions -> Stream.of( //
						Map.entry(positions.get(0), nearestCorridor(positions.get(1), corridors)), //
						Map.entry(positions.get(1), nearestCorridor(positions.get(0), corridors)))) //
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		final Map<Position, List<Position>> adjacencyMap = corridors.stream() //
				.map(pos -> Map.entry(pos, pos.neighbours() //
						.map(neig -> portals.getOrDefault(neig, neig)) //
						.filter(p -> p.x() >= 0 && p.x() < cols && p.y() >= 0 && p.y() < rows) //
						.filter(corridors::contains) //
						.toList())) //
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l1, l2) -> l1, LinkedHashMap::new));

		record Status(Position position, List<Position> path) {}
		final Set<Position> seenPositions = new HashSet<>();
		final Deque<Status> queue = new ArrayDeque<>();
		queue.add(new Status(start, List.of()));

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			seenPositions.add(current.position());
			if (current.position().equals(exit)) {
				return concat(current.path(), exit);
			}

			final List<Position> neighbours = adjacencyMap.get(current.position());
			neighbours.stream() //
					.filter(pos -> !seenPositions.contains(pos)) //
					.forEach(pos -> queue.add(new Status(pos, concat(current.path(), current.position()))));
		}

		throw new IllegalStateException("Can't find shortest path");
	}

	@SuppressWarnings({ "java:S1481", "java:S3776" })
	public long findRecursiveShortestPath()
	{
		final Map<Integer, Map<Position, List<Position>>> levels = new HashMap<>();
		final Map<Position, List<Position>> level0AdjacencyMap = levels.computeIfAbsent(0, this::generateLevel);
		final Set<Position> portals = labels.values().stream() //
				.flatMap(List::stream) //
				.map(pos -> nearestCorridor(pos, corridors)) //
				.collect(Collectors.toUnmodifiableSet());
		final Map<Position, List<Connection>> positionToConnections = portals.stream() //
				.collect(Collectors.toMap(portal -> portal, portal -> {
					final List<Connection> connections = executeBFS(portal, portals, level0AdjacencyMap);
					return connections.stream() //
							.map(con -> new Connection(con.targetPosition().withZ(-1), con.length())) //
							.toList();
				}));

		final Set<Position> seenPositions = new HashSet<>();

		final Map<Position, Status> enqueuedStatuses = new HashMap<>();
		final UpdatableQueue<Status> queue = new UpdatablePriorityQueue<>(Comparator.comparingLong(Status::distance));
		final Status initialStatus = new Status(start, 0);
		queue.add(initialStatus);
		enqueuedStatuses.put(initialStatus.position(), initialStatus);

		while (!queue.isEmpty()) {
			final var current = queue.poll();
			enqueuedStatuses.remove(current.position());
			seenPositions.add(current.position());
			if (current.position().equals(exit)) {
				return current.distance();
			}

			final Map<Position, List<Position>> level = levels.computeIfAbsent(current.position().z(), this::generateLevel);
			final var levelSwitches = level.get(current.position()).stream() //
					.filter(pos -> pos.z() != current.position().z()) //
					.map(pos -> new Connection(pos, 1)) //
					.toList();

			boolean needUpdate = false;
			final List<Connection> connections = Stream.concat( //
							positionToConnections.get(current.position().withZ(0)).stream(), //
							levelSwitches.stream()) //
					.toList();
			for (final Connection connection : connections) {
				final Position targetPosition = connection.targetPosition().with(pos -> {
					if (pos.z() == -1) {
						pos.z(current.position().z());
					}
				});
				if (seenPositions.contains(targetPosition)) {
					continue;
				}

				final long newDistance = current.distance() + connection.length();
				final var existingStatus = enqueuedStatuses.get(targetPosition);
				if (existingStatus != null) {
					if (existingStatus.distance() > newDistance) {
						existingStatus.setDistance(newDistance);
						needUpdate = true;
					}
				} else {
					final var newStatus = new Status(targetPosition, newDistance);
					queue.add(newStatus);
					enqueuedStatuses.put(newStatus.position(), newStatus);
				}
			}
			if (needUpdate) {
				queue.update();
			}
		}

		throw new IllegalStateException("Can't find shortest path");
	}

	@SuppressWarnings({ "UnnecessaryLocalVariable", "java:S1481" })
	private Map<Position, List<Position>> generateLevel(final int level)
	{
		final Map<String, List<Position>> labelsInLevel = labels.entrySet().stream() //
				.map(entry -> Map.entry(entry.getKey(), entry.getValue().stream() //
						.map(pos -> pos.withZ(level)) //
						.toList())) //
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

		record Position2D(int x, int y) {}
		final Set<Position> corridorsInLevel = corridors.stream() //
				.map(pos -> pos.withZ(level)) //
				.collect(Collectors.toUnmodifiableSet());
		final Set<Position2D> corridors2D = corridors.stream() //
				.map(pos -> new Position2D(pos.x(), pos.y())) //
				.collect(Collectors.toUnmodifiableSet());

		final Map<Position, Position> portals = labelsInLevel.values().stream() //
				.filter(positions -> positions.size() == 2) //
				.flatMap(positions -> Stream.of( //
						Map.entry(positions.get(0), nearestCorridor(positions.get(1), corridorsInLevel) //
								.withZ(positions.get(0).isOnOuterBord(rows, cols) ? level - 1 : level + 1)), //
						Map.entry(positions.get(1), nearestCorridor(positions.get(0), corridorsInLevel) //
								.withZ(positions.get(1).isOnOuterBord(rows, cols) ? level - 1 : level + 1)))) //
				.filter(entry -> entry.getValue().z() >= 0) //
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		final Map<Position, List<Position>> adjacencyMap = corridorsInLevel.stream() //
				.map(pos -> Map.entry(pos, pos.neighbours() //
						.map(neig -> portals.getOrDefault(neig, neig)) //
						.filter(p -> p.x() >= 0 && p.x() < cols && p.y() >= 0 && p.y() < rows) //
						.filter(p -> corridors2D.contains(new Position2D(p.x(), p.y()))) //
						.toList())) //
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l1, l2) -> l1, LinkedHashMap::new));

		return adjacencyMap;
	}

	@SuppressWarnings({ "java:S117", "java:S1481" })
	private List<Connection> executeBFS(final Position startNode, final Set<Position> targetNodes,
			final Map<Position, List<Position>> adjacencyMap)
	{
		final List<Connection> connections = new ArrayList<>();

		final Set<Position> seenPositions = new HashSet<>();
		record Status(Position position, long distance) {}
		final Deque<Status> queue = new ArrayDeque<>();
		queue.add(new Status(startNode, 0));
		seenPositions.add(startNode);

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			if (current.distance() != 0 && targetNodes.contains(current.position())) {
				connections.add(new Connection(current.position(), current.distance()));
			}

			final List<Position> neighbours = adjacencyMap.get(current.position());
			if (neighbours != null) {
				neighbours.stream() //
						.filter(pos -> !seenPositions.contains(pos)) //
						.forEach(pos -> {
							queue.add(new Status(pos, current.distance() + 1));
							seenPositions.add(pos);
						});
			}
		}

		return List.copyOf(connections);
	}

	public record Connection(Position targetPosition, long length) {}

	@RecordBuilder
	public record Position(int x, int y, int z) implements DonutMazePositionBuilder.With
	{

		public Stream<Position> neighbours()
		{
			return Stream.of( //
					this.withY(y - 1), //
					this.withX(x + 1), //
					this.withY(y + 1), //
					this.withX(x - 1));
		}

		boolean isOnOuterBord(final int rows, final int cols)
		{
			return x <= 0 || y <= 0 || x >= cols - 1 || y >= rows - 1;
		}

	}

	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	private static final class Status
	{
		private final Position position;

		@Setter
		private long distance;

		public Position position()
		{
			return position;
		}

		public long distance()
		{
			return distance;
		}
	}
}
