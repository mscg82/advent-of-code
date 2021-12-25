package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record AmphipodsHouse(Map<Position, Node> structure, Map<AmphipodType, List<Position>> rooms, //
							 List<Amphipod> initialA, List<Amphipod> initialB, List<Amphipod> initialC, List<Amphipod> initialD, //
							 int maxX)
{
	public static AmphipodsHouse parseInput1(final BufferedReader in) throws IOException
	{
		// nodes
		final var h1_1 = new Node(Hallway.INSTANCE, new Position(1, 1), new ArrayList<>()); // NOSONAR
		final var h1_2 = new Node(Hallway.INSTANCE, new Position(1, 2), new ArrayList<>()); // NOSONAR
		final var h1_4 = new Node(Hallway.INSTANCE, new Position(1, 4), new ArrayList<>()); // NOSONAR
		final var h1_6 = new Node(Hallway.INSTANCE, new Position(1, 6), new ArrayList<>()); // NOSONAR
		final var h1_8 = new Node(Hallway.INSTANCE, new Position(1, 8), new ArrayList<>()); // NOSONAR
		final var h1_10 = new Node(Hallway.INSTANCE, new Position(1, 10), new ArrayList<>()); // NOSONAR
		final var h1_11 = new Node(Hallway.INSTANCE, new Position(1, 11), new ArrayList<>()); // NOSONAR

		final var r2_3 = new Node(Room.ROOM_A, new Position(2, 3), new ArrayList<>()); // NOSONAR
		final var r3_3 = new Node(Room.ROOM_A, new Position(3, 3), new ArrayList<>()); // NOSONAR
		final var r2_5 = new Node(Room.ROOM_B, new Position(2, 5), new ArrayList<>()); // NOSONAR
		final var r3_5 = new Node(Room.ROOM_B, new Position(3, 5), new ArrayList<>()); // NOSONAR
		final var r2_7 = new Node(Room.ROOM_C, new Position(2, 7), new ArrayList<>()); // NOSONAR
		final var r3_7 = new Node(Room.ROOM_C, new Position(3, 7), new ArrayList<>()); // NOSONAR
		final var r2_9 = new Node(Room.ROOM_D, new Position(2, 9), new ArrayList<>()); // NOSONAR
		final var r3_9 = new Node(Room.ROOM_D, new Position(3, 9), new ArrayList<>()); // NOSONAR

		final var hallways = List.of(h1_1, h1_2, h1_4, h1_6, h1_8, h1_10, h1_11);
		final var rooms = List.of(r2_3, r3_3, r2_5, r3_5, r2_7, r3_7, r2_9, r3_9);

		createConnections(hallways, rooms);

		final var structure = Stream.concat(hallways.stream(), rooms.stream()) //
				.collect(Collectors.toMap(Node::position, n -> n, (n1, n2) -> n1, LinkedHashMap::new));

		final List<Position> as = new ArrayList<>(2);
		final List<Position> bs = new ArrayList<>(2);
		final List<Position> cs = new ArrayList<>(2);
		final List<Position> ds = new ArrayList<>(2);
		try {
			final List<String> allLines = in.lines().toList();
			rooms.stream() //
					.map(Node::position) //
					.forEach(position -> {
						final char type = allLines.get(position.x()).charAt(position.y());
						switch (type) {
							case 'A' -> as.add(position);
							case 'B' -> bs.add(position);
							case 'C' -> cs.add(position);
							case 'D' -> ds.add(position);
							default -> throw new IllegalArgumentException("Unsupported type " + type);
						}
					});
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final Map<AmphipodType, List<Position>> roomsPerType = new EnumMap<>(AmphipodType.class);
		roomsPerType.put(AmphipodType.A, Stream.of(r2_3, r3_3).map(Node::position).toList());
		roomsPerType.put(AmphipodType.B, Stream.of(r2_5, r3_5).map(Node::position).toList());
		roomsPerType.put(AmphipodType.C, Stream.of(r2_7, r3_7).map(Node::position).toList());
		roomsPerType.put(AmphipodType.D, Stream.of(r2_9, r3_9).map(Node::position).toList());

		return new AmphipodsHouse(structure, roomsPerType, //
				as.stream().map(p -> new Amphipod(AmphipodType.A, p)).toList(), //
				bs.stream().map(p -> new Amphipod(AmphipodType.B, p)).toList(), //
				cs.stream().map(p -> new Amphipod(AmphipodType.C, p)).toList(), //
				ds.stream().map(p -> new Amphipod(AmphipodType.D, p)).toList(), //
				3);
	}

	public static AmphipodsHouse parseInput2(final BufferedReader in) throws IOException
	{
		// nodes
		final var h1_1 = new Node(Hallway.INSTANCE, new Position(1, 1), new ArrayList<>()); // NOSONAR
		final var h1_2 = new Node(Hallway.INSTANCE, new Position(1, 2), new ArrayList<>()); // NOSONAR
		final var h1_4 = new Node(Hallway.INSTANCE, new Position(1, 4), new ArrayList<>()); // NOSONAR
		final var h1_6 = new Node(Hallway.INSTANCE, new Position(1, 6), new ArrayList<>()); // NOSONAR
		final var h1_8 = new Node(Hallway.INSTANCE, new Position(1, 8), new ArrayList<>()); // NOSONAR
		final var h1_10 = new Node(Hallway.INSTANCE, new Position(1, 10), new ArrayList<>()); // NOSONAR
		final var h1_11 = new Node(Hallway.INSTANCE, new Position(1, 11), new ArrayList<>()); // NOSONAR

		final var r2_3 = new Node(Room.ROOM_A, new Position(2, 3), new ArrayList<>()); // NOSONAR
		final var r3_3 = new Node(Room.ROOM_A, new Position(3, 3), new ArrayList<>()); // NOSONAR
		final var r4_3 = new Node(Room.ROOM_A, new Position(4, 3), new ArrayList<>()); // NOSONAR
		final var r5_3 = new Node(Room.ROOM_A, new Position(5, 3), new ArrayList<>()); // NOSONAR
		final var r2_5 = new Node(Room.ROOM_B, new Position(2, 5), new ArrayList<>()); // NOSONAR
		final var r3_5 = new Node(Room.ROOM_B, new Position(3, 5), new ArrayList<>()); // NOSONAR
		final var r4_5 = new Node(Room.ROOM_B, new Position(4, 5), new ArrayList<>()); // NOSONAR
		final var r5_5 = new Node(Room.ROOM_B, new Position(5, 5), new ArrayList<>()); // NOSONAR
		final var r2_7 = new Node(Room.ROOM_C, new Position(2, 7), new ArrayList<>()); // NOSONAR
		final var r3_7 = new Node(Room.ROOM_C, new Position(3, 7), new ArrayList<>()); // NOSONAR
		final var r4_7 = new Node(Room.ROOM_C, new Position(4, 7), new ArrayList<>()); // NOSONAR
		final var r5_7 = new Node(Room.ROOM_C, new Position(5, 7), new ArrayList<>()); // NOSONAR
		final var r2_9 = new Node(Room.ROOM_D, new Position(2, 9), new ArrayList<>()); // NOSONAR
		final var r3_9 = new Node(Room.ROOM_D, new Position(3, 9), new ArrayList<>()); // NOSONAR
		final var r4_9 = new Node(Room.ROOM_D, new Position(4, 9), new ArrayList<>()); // NOSONAR
		final var r5_9 = new Node(Room.ROOM_D, new Position(5, 9), new ArrayList<>()); // NOSONAR

		final var hallways = List.of(h1_1, h1_2, h1_4, h1_6, h1_8, h1_10, h1_11);
		final var rooms = List.of( //
				r2_3, r3_3, r4_3, r5_3, //
				r2_5, r3_5, r4_5, r5_5, //
				r2_7, r3_7, r4_7, r5_7, //
				r2_9, r3_9, r4_9, r5_9);

		createConnections(hallways, rooms);

		final var structure = Stream.concat(hallways.stream(), rooms.stream()) //
				.collect(Collectors.toMap(Node::position, n -> n, (n1, n2) -> n1, LinkedHashMap::new));

		final List<Position> as = new ArrayList<>(4);
		final List<Position> bs = new ArrayList<>(4);
		final List<Position> cs = new ArrayList<>(4);
		final List<Position> ds = new ArrayList<>(4);
		try {
			final List<String> allLines = in.lines().collect(Collectors.toCollection(ArrayList::new));
			allLines.add(3, "  #D#C#B#A#  ");
			allLines.add(4, "  #D#B#A#C#  ");
			rooms.stream() //
					.map(Node::position) //
					.forEach(position -> {
						final char type = allLines.get(position.x()).charAt(position.y());
						switch (type) {
							case 'A' -> as.add(position);
							case 'B' -> bs.add(position);
							case 'C' -> cs.add(position);
							case 'D' -> ds.add(position);
							default -> throw new IllegalArgumentException("Unsupported type " + type);
						}
					});
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final Map<AmphipodType, List<Position>> roomsPerType = new EnumMap<>(AmphipodType.class);
		roomsPerType.put(AmphipodType.A, Stream.of(r2_3, r3_3, r4_3, r5_3).map(Node::position).toList());
		roomsPerType.put(AmphipodType.B, Stream.of(r2_5, r3_5, r4_5, r5_5).map(Node::position).toList());
		roomsPerType.put(AmphipodType.C, Stream.of(r2_7, r3_7, r4_7, r5_7).map(Node::position).toList());
		roomsPerType.put(AmphipodType.D, Stream.of(r2_9, r3_9, r4_9, r5_9).map(Node::position).toList());

		return new AmphipodsHouse(structure, roomsPerType, //
				as.stream().map(p -> new Amphipod(AmphipodType.A, p)).toList(), //
				bs.stream().map(p -> new Amphipod(AmphipodType.B, p)).toList(), //
				cs.stream().map(p -> new Amphipod(AmphipodType.C, p)).toList(), //
				ds.stream().map(p -> new Amphipod(AmphipodType.D, p)).toList(), //
				5);
	}

	private static void createConnections(final List<Node> hallways, final List<Node> rooms)
	{
		for (final Node hallway : hallways) {
			for (final Node room : rooms) {
				// build intermediate positions
				final Stream<Position> hallwayPositions = new Range(hallway.position().y(), room.position().y()).stream() //
						.mapToObj(y -> hallway.position().withY(y));
				final Stream<Position> roomPositions = IntStream.rangeClosed(2, room.position().x()) //
						.mapToObj(x -> room.position().withX(x));
				final List<Position> intermediatePositions = Stream.concat(hallwayPositions, roomPositions) //
						.toList();

				// connect hallway and room
				hallway.connections().add(new Connection(room.position(),
						intermediatePositions.stream().filter(n -> !n.equals(hallway.position())).toList()));
				room.connections().add(new Connection(hallway.position(),
						intermediatePositions.stream().filter(n -> !n.equals(room.position())).toList()));
			}
		}
	}

	public long findMinCost()
	{
		final Map<List<Amphipod>, Status> seenStatuses = new HashMap<>();
		final Deque<Status> queue = new ArrayDeque<>(70_000);
		final List<Amphipod> amphipods = Seq.seq(initialA.stream()).concat(initialB.stream()).concat(initialC.stream())
				.concat(initialD.stream()).toList();
		final Status initialStatus = new Status(false, 0, amphipods);
		queue.add(initialStatus);
		seenStatuses.put(amphipods, initialStatus);

		long minCost = Long.MAX_VALUE;
		while (!queue.isEmpty()) {
			final var status = queue.poll();
			status.visited = true;

			if (status.totalCost() > minCost) {
				continue;
			}
			final boolean allAtHome = status.amphipods().stream().allMatch(Amphipod::isAtHome);
			if (allAtHome) {
				if (minCost > status.totalCost()) {
					minCost = status.totalCost();
				}
				continue;
			}

			final Map<Position, Amphipod> occupiedPositions = status.amphipods().stream() //
					.collect(Collectors.toMap(Amphipod::position, a -> a));

			for (final var amphipod : status.amphipods()) {
				final Node sourceNode = structure.get(amphipod.position());
				final List<Connection> connections = sourceNode.connections().stream() //
						.sorted(Comparator.comparingInt(conn -> conn.targetPosition().distance(sourceNode.position()))) //
						.toList();
				for (final Connection connection : connections) {
					if (!isConnectionValid(connection, amphipod, occupiedPositions)) {
						continue;
					}

					final Node targetNode = structure.get(connection.targetPosition());
					final long movementCost = (long) targetNode.position().distance(amphipod.position()) * amphipod.type().cost;
					final long totalCost = status.totalCost() + movementCost;
					if (totalCost > minCost) {
						continue;
					}

					final var newAmphipod = amphipod.withPosition(targetNode.position());
					final List<Amphipod> newAmphipods = status.amphipods().stream() //
							.map(amp -> amp == amphipod ? newAmphipod : amp) //
							.toList();

					final Status existingStatus = seenStatuses.get(newAmphipods);
					if (existingStatus != null) {
						if (!existingStatus.visited && existingStatus.totalCost() > totalCost) {
							existingStatus.setTotalCost(totalCost);
						}
					} else {
						final var newStatus = new Status(false, totalCost, newAmphipods);
						queue.add(newStatus);
						seenStatuses.put(newStatus.amphipods(), newStatus);
					}

				}
			}
		}
		return minCost;
	}

	private boolean isConnectionValid(final Connection connection, final Amphipod amphipod,
			final Map<Position, Amphipod> occupiedPositions)
	{
		final boolean roadBlocked = connection.intermediateSteps().stream().anyMatch(occupiedPositions::containsKey);
		if (roadBlocked) {
			// road blocked, skip connection
			return false;
		}

		final Node sourceNode = structure.get(amphipod.position());
		if (sourceNode.type() instanceof Room r && r.type == amphipod.type()) {
			// amphipod is in correct room. If no wrong amphipod in room, we don't need to move
			final List<Position> room = rooms.get(r.type);
			final boolean roomHasAnyWrongAmphipod = room.stream().anyMatch(pos -> {
				final Amphipod amphipodInRoom = occupiedPositions.get(pos);
				return amphipodInRoom != null && amphipodInRoom.type() != r.type;
			});
			if (!roomHasAnyWrongAmphipod) {
				return false;
			}
		}

		final Node targetNode = structure.get(connection.targetPosition());
		if (targetNode.type() instanceof Room r) {
			if (r.type != amphipod.type()) {
				// target is a room of an invalid type, skip connection
				return false;
			}

			final List<Position> room = rooms.get(r.type);

			// if room contains invalid amphipods, don't enter
			final boolean roomHasAnyWrongAmphipod = room.stream().anyMatch(pos -> {
				final Amphipod amphipodInRoom = occupiedPositions.get(pos);
				return amphipodInRoom != null && amphipodInRoom.type() != r.type;
			});
			if (roomHasAnyWrongAmphipod) {
				return false;
			}

			// when moving into the right room, always go to the deepest available position
			final Position deepestPositionInRoom = room.stream() //
					.filter(pos -> !occupiedPositions.containsKey(pos)) //
					.max(Comparator.comparingInt(Position::x)) //
					.orElseThrow();
			return deepestPositionInRoom.equals(targetNode.position());
		}

		return true;
	}

	@RequiredArgsConstructor
	public enum AmphipodType
	{
		A(1), B(10), C(100), D(1000);

		public final int cost;
	}

	@RecordBuilder
	public record Amphipod(AmphipodType type, Position position) implements AmphipodsHouseAmphipodBuilder.With
	{

		public boolean isAtHome()
		{
			return switch (type) {
				case A -> position.y() == 3;
				case B -> position.y() == 5;
				case C -> position.y() == 7;
				case D -> position.y() == 9;
			};
		}

		@Override
		public String toString()
		{
			return type + "(" + position.x() + ", " + position.y() + ")";
		}
	}

	public sealed interface NodeType permits Hallway, Room {}

	@RequiredArgsConstructor
	public enum Room implements NodeType
	{
		ROOM_A(AmphipodType.A), ROOM_B(AmphipodType.B), ROOM_C(AmphipodType.C), ROOM_D(AmphipodType.D);

		public final AmphipodType type;
	}

	public enum Hallway implements NodeType
	{
		INSTANCE;

		@Override
		public String toString()
		{
			return "Hallway";
		}
	}

	@RecordBuilder
	public record Position(int x, int y) implements AmphipodsHousePositionBuilder.With
	{

		public int distance(final Position other)
		{
			return Math.abs(x - other.x) + Math.abs(y - other.y);
		}

	}

	public record Connection(Position targetPosition, List<Position> intermediateSteps) {}

	public record Node(NodeType type, Position position, List<Connection> connections)
	{
		@Override
		public String toString()
		{
			return "Node{type=" + type + ", position=" + position + ", connections=" + connections.size() + '}';
		}
	}

	private record Range(int min, int max)
	{

		public Range
		{
			if (min > max) {
				final int tmp = min;
				min = max;
				max = tmp;
			}
		}

		public IntStream stream()
		{
			return IntStream.rangeClosed(min, max);
		}

	}

	@AllArgsConstructor
	private static final class Status
	{
		private boolean visited;

		private long totalCost;

		private final List<Amphipod> amphipods;

		public long totalCost()
		{
			return totalCost;
		}

		public void setTotalCost(final long totalCost)
		{
			this.totalCost = totalCost;
		}

		public List<Amphipod> amphipods()
		{
			return amphipods;
		}

		@Override
		public String toString()
		{
			return "Status{" + "visited=" + visited + ", totalCost=" + totalCost + ", amphipods=" + amphipods + '}';
		}
	}

}
