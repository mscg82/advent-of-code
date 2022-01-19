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
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Vault(VaultMap map, Map<Position, List<Position>> adjacencyMap, int rows, int cols)
{

	public static Vault parseInput(final BufferedReader in) throws IOException
	{
		final List<String> allLines;
		try {
			allLines = in.lines().toList();
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final Map<Position, Key> doors = new HashMap<>();
		final Map<Position, Key> keys = new HashMap<>();
		final Set<Position> corridors = new LinkedHashSet<>();
		Position entrance = null;

		for (int y = 0, l = allLines.size(); y < l; y++) {
			final String line = allLines.get(y);
			for (int x = 0, s = line.length(); x < s; x++) {
				final char c = line.charAt(x);
				final Position position = new Position(x, y);
				if (c == '.') {
					corridors.add(position);
				} else if (c == '@') {
					entrance = position;
					corridors.add(position);
				} else if (c >= 'a' && c <= 'z') {
					keys.put(position, Key.from(c));
					corridors.add(position);
				} else if (c >= 'A' && c <= 'Z') {
					doors.put(position, Key.from(c));
					corridors.add(position);
				} else if (c != '#') {
					throw new IllegalArgumentException("Unsupported character " + c);
				}
			}
		}

		if (entrance == null) {
			throw new IllegalArgumentException("Map doesn't have an entrance");
		}

		final int rows = allLines.size();
		final int cols = allLines.get(0).length();

		final Map<Position, List<Position>> adjacencyMap = buildAdjacencyMap(corridors, rows, cols);

		return new Vault(new VaultMap(Map.copyOf(doors), Map.copyOf(keys), Set.copyOf(corridors), entrance),
				Map.copyOf(adjacencyMap), rows, cols);
	}

	private static Map<Position, List<Position>> buildAdjacencyMap(final Set<Position> corridors, final int rows, final int cols)
	{
		final Map<Position, List<Position>> adjacencyMap = new LinkedHashMap<>();

		for (final Position pos : corridors) {
			final List<Position> neighbours = pos.neighbours(rows, cols) //
					.filter(corridors::contains) //
					.toList();
			adjacencyMap.put(pos, neighbours);
		}
		return adjacencyMap;
	}

	private static <T> List<T> replace(final List<T> list, final T oldValue, final T newValue)
	{
		return list.stream() //
				.map(v -> v == oldValue ? newValue : v) //
				.toList();
	}

	private static <T> List<T> concat(final List<T> list, final T newElement)
	{
		return Stream.concat(list.stream(), Stream.of(newElement)).toList();
	}

	private static <T> Set<T> concat(final Set<T> list, final T newElement)
	{
		return Stream.concat(list.stream(), Stream.of(newElement)).collect(Collectors.toUnmodifiableSet());
	}

	public Solution getMinPath()
	{
		final var keysAndEntrance = concat(map.keys().keySet(), map.entrance());
		final Map<Position, List<Connection>> positionToConnections = keysAndEntrance.stream() //
				.collect(Collectors.toMap(pos -> pos, pos -> executeBFS(pos, keysAndEntrance, adjacencyMap)));

		return findSolution(positionToConnections,
				new Status(new PositionWithKeys(List.of(map.entrance()), Key.newSet()), 0, List.of()));
	}

	public Solution getMinPath2()
	{
		final Set<Position> corridorsToRemove = Stream.concat(Stream.of(map.entrance()), map.entrance().neighbours(rows, cols)) //
				.collect(Collectors.toUnmodifiableSet());
		final Set<Position> corridors = map.corridors().stream() //
				.filter(pos -> !corridorsToRemove.contains(pos)) //
				.collect(Collectors.toUnmodifiableSet());
		final Map<Position, List<Position>> adjacencyMap = buildAdjacencyMap(corridors, rows, cols);
		final var entrances = List.of( //
				map.entrance().with(pos -> pos.x(pos.x() - 1).y(pos.y() - 1)),
				map.entrance().with(pos -> pos.x(pos.x() + 1).y(pos.y() - 1)),
				map.entrance().with(pos -> pos.x(pos.x() - 1).y(pos.y() + 1)),
				map.entrance().with(pos -> pos.x(pos.x() + 1).y(pos.y() + 1)));

		final var keysAndEntrance = Stream.concat(map.keys().keySet().stream(), entrances.stream()) //
				.collect(Collectors.toUnmodifiableSet());
		final Map<Position, List<Connection>> positionToConnections = keysAndEntrance.stream() //
				.collect(Collectors.toMap(pos -> pos, pos -> executeBFS(pos, keysAndEntrance, adjacencyMap)));

		return findSolution(positionToConnections, new Status(new PositionWithKeys(entrances, Key.newSet()), 0, List.of()));
	}

	@SuppressWarnings({ "java:S135", "java:S3776" })
	private Solution findSolution(final Map<Position, List<Connection>> positionToConnections, final Status initialStatus)
	{
		final Set<PositionWithKeys> seenStatuses = new HashSet<>();
		final Map<PositionWithKeys, Status> enqueuedStatuses = new HashMap<>();
		final UpdatableQueue<Status> queue = new UpdatablePriorityQueue<>(Comparator.comparingLong(Status::totalDistance));
		queue.add(initialStatus);
		enqueuedStatuses.put(initialStatus.node(), initialStatus);

		while (!queue.isEmpty()) {
			final var current = queue.poll();
			enqueuedStatuses.remove(current.node());
			seenStatuses.add(current.node());
			if (current.ownedKeys().size() == map.keys().size()) {
				return new Solution(current.totalDistance(), current.ownedKeys());
			}

			boolean needUpdate = false;
			for (final var position : current.node().positions()) {
				final List<Connection> connections = positionToConnections.get(position);
				for (final Connection connection : connections) {
					final var targetPosition = connection.targetPosition();
					final var targetPositions = replace(current.node().positions(), position, targetPosition);
					final var key = map.keys().get(targetPosition);
					if (key == null || current.node().ownedKeys().contains(key)) {
						continue;
					}

					final List<Key> newKeys;
					final Set<Key> newKeysSet;
					if (!current.node().ownedKeys().contains(key)) {
						newKeys = concat(current.ownedKeys(), key);
						newKeysSet = Key.concat(current.node().ownedKeys(), key);
					} else {
						newKeys = current.ownedKeys();
						newKeysSet = current.node().ownedKeys();
					}
					if (!newKeysSet.containsAll(connection.requiredKeys())) {
						continue;
					}

					final long newDistance = current.totalDistance() + connection.length();
					final var newStatus = new Status(new PositionWithKeys(targetPositions, newKeysSet), newDistance, newKeys);
					if (seenStatuses.contains(newStatus.node())) {
						continue;
					}

					final var existingStatus = enqueuedStatuses.get(newStatus.node());
					if (existingStatus != null) {
						if (existingStatus.totalDistance() > newDistance) {
							enqueuedStatuses.remove(existingStatus.node());
							existingStatus.setNode(newStatus.node());
							enqueuedStatuses.put(existingStatus.node(), existingStatus);
							existingStatus.setTotalDistance(newDistance);
							existingStatus.setOwnedKeys(newKeys);
							needUpdate = true;
						}
					} else {
						queue.add(newStatus);
						enqueuedStatuses.put(newStatus.node(), newStatus);
					}
				}
			}

			if (needUpdate) {
				queue.update();
			}
		}

		throw new IllegalStateException("Can't find path to collect all keys");
	}

	@SuppressWarnings({ "java:S117", "java:S1481" })
	private List<Connection> executeBFS(final Position startNode, final Set<Position> targetNodes,
			final Map<Position, List<Position>> adjacencyMap)
	{
		final Map<Position, Key> doorsAndKeys = new HashMap<>(map.doors());
		doorsAndKeys.putAll(map.keys());

		final List<Connection> connections = new ArrayList<>();

		final Set<Position> seenPositions = new HashSet<>();
		record BFSStatus(Position position, long distance, Set<Key> requiredKeys) {}
		final Deque<BFSStatus> queue = new ArrayDeque<>();
		queue.add(new BFSStatus(startNode, 0, Key.newSet()));
		seenPositions.add(startNode);

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			if (current.distance() != 0 && targetNodes.contains(current.position())) {
				connections.add(new Connection(current.position(), current.distance(), current.requiredKeys()));
			}

			final List<Position> neighbours = adjacencyMap.get(current.position());
			neighbours.stream() //
					.filter(pos -> !seenPositions.contains(pos)) //
					.forEach(pos -> {
						final var doorOrKey = doorsAndKeys.get(pos);
						final var newKeys = Key.concat(current.requiredKeys(), doorOrKey);
						queue.add(new BFSStatus(pos, current.distance() + 1, newKeys));
						seenPositions.add(pos);
					});
		}

		return List.copyOf(connections);
	}

	private record PositionWithKeys(List<Position> positions, Set<Key> ownedKeys) {}

	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	private static final class Status
	{
		@Setter
		private PositionWithKeys node;

		@Setter
		private long totalDistance;

		@Setter
		private List<Key> ownedKeys;

		public PositionWithKeys node()
		{
			return node;
		}

		public long totalDistance()
		{
			return totalDistance;
		}

		public List<Key> ownedKeys()
		{
			return ownedKeys;
		}
	}

	private record Connection(Position targetPosition, long length, Set<Key> requiredKeys) {}

	@SuppressWarnings("unused")
	public enum Key
	{
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

		public static Key from(final char c)
		{
			return Key.valueOf(String.valueOf(c).toUpperCase());
		}

		public static Set<Key> newSet()
		{
			return EnumSet.noneOf(Key.class);
		}

		public static Set<Key> toSet(final Collection<Key> source)
		{
			return source.isEmpty() ? EnumSet.noneOf(Key.class) : EnumSet.copyOf(source);
		}

		public static Set<Key> concat(final Set<Key> set, final Key newKey)
		{
			if (newKey == null || set.contains(newKey)) {
				return set;
			} else {
				final var newSet = EnumSet.copyOf(set);
				newSet.add(newKey);
				return newSet;
			}
		}
	}

	public record Solution(long pathLength, List<Key> ownedKeys) {}

	@RecordBuilder
	public record Position(int x, int y) implements VaultPositionBuilder.With
	{

		public Stream<Position> neighbours(final int rows, final int cols)
		{
			return Stream.of( //
							this.withY(y - 1), //
							this.withX(x + 1), //
							this.withY(y + 1), //
							this.withX(x - 1)) //
					.filter(pos -> pos.x >= 0 && pos.x < cols && pos.y >= 0 && pos.y < rows);
		}

	}

	public record VaultMap(Map<Position, Key> doors, Map<Position, Key> keys, Set<Position> corridors, Position entrance) {}

}
