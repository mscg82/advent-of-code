package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record CaveMap(int depth, Position target)
{

	public static CaveMap parseInput(final BufferedReader in) throws IOException
	{
		String line;

		line = in.readLine();
		int index = line.indexOf(':');
		final int depth = Integer.parseInt(line.substring(index + 1).trim());

		line = in.readLine();
		index = line.indexOf(':');
		final String[] parts = line.substring(index + 1).split(",");

		return new CaveMap(depth, new Position(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())));
	}

	public long computeGlobalRisk()
	{
		final Map<Position, Long> erosionLevels = computeErosionLevelsUntilTarget();

		return erosionLevels.values().stream() //
				.mapToLong(Long::longValue) //
				.map(l -> l % 3) //
				.sum();
	}

	public long computeShortestPath()
	{
		final Map<Position, Long> erosionLevels = computeErosionLevelsUntilTarget();

		final Map<PositionWithTool, Node> enqueuedNodes = new HashMap<>();
		final Set<PositionWithTool> seenNodes = new HashSet<>();
		final UpdatableQueue<Node> queue = new UpdatablePriorityQueue<>(Comparator.comparingLong(Node::getDistance));
		final Node initialNode = new Node(new PositionWithTool(Position.ORIGIN, Tools.TORCH), null, 0);
		queue.add(initialNode);
		enqueuedNodes.put(initialNode.positionWithTool, initialNode);

		while (!queue.isEmpty()) {
			final var current = queue.remove();
			enqueuedNodes.remove(current.positionWithTool);
			seenNodes.add(current.positionWithTool);

			final Position currentPosition = current.positionWithTool.position();
			if (currentPosition.equals(target) && current.positionWithTool.tool() == Tools.TORCH) {
				return current.distance;
			}

			final List<Position> neighbours = currentPosition.neighbours();
			for (final Position neighbour : neighbours) {
				final boolean canMoveIn;
				if (neighbour.equals(target) && current.positionWithTool.tool() != Tools.TORCH) {
					canMoveIn = false;
				} else {
					final Type type = Type.from(computeErosionLevel(neighbour, erosionLevels) % 3);
					final Tools tool = current.positionWithTool.tool();
					canMoveIn = areTypeAndToolCompatible(type, tool);
				}

				if (!canMoveIn) {
					continue;
				}

				final var newPositionWithTool = current.positionWithTool.withPosition(neighbour);
				if (seenNodes.contains(newPositionWithTool)) {
					continue;
				}
				enqueueNewPositionWithTool(newPositionWithTool, current, enqueuedNodes, queue, 1);
			}

			final var currentType = Type.from(computeErosionLevel(currentPosition, erosionLevels) % 3);
			for (final Tools tool : Tools.values()) {
				if (tool == current.positionWithTool.tool()) {
					continue;
				}

				if (!areTypeAndToolCompatible(currentType, tool)) {
					continue;
				}

				final var newPositionWithTool = current.positionWithTool.withTool(tool);
				if (seenNodes.contains(newPositionWithTool)) {
					continue;
				}
				enqueueNewPositionWithTool(newPositionWithTool, current, enqueuedNodes, queue, 7);
			}
		}

		throw new IllegalArgumentException("Can't find a path to target node");
	}

	private Map<Position, Long> computeErosionLevelsUntilTarget()
	{
		final Map<Position, Long> erosionLevels = new HashMap<>();
		erosionLevels.put(Position.ORIGIN, toErosionLevel(0L));
		erosionLevels.put(target, toErosionLevel(0L));

		for (int x = 1; x <= target.x(); x++) {
			computeErosionLevel(new Position(x, 0), erosionLevels);
		}
		for (int y = 1; y <= target.y(); y++) {
			computeErosionLevel(new Position(0, y), erosionLevels);
		}
		for (int y = 1; y <= target.y(); y++) {
			for (int x = 1; x <= target.x(); x++) {
				computeErosionLevel(new Position(x, y), erosionLevels);
			}
		}
		return erosionLevels;
	}

	private long computeErosionLevel(final Position position, final Map<Position, Long> erosionLevels)
	{
		Long erosionLevel;
		erosionLevel = erosionLevels.get(position);
		if (erosionLevel != null) {
			return erosionLevel;
		}

		if (position.y() == 0) {
			erosionLevel = toErosionLevel(position.x() * 16807L);
		} else if (position.x() == 0) {
			erosionLevel = toErosionLevel(position.y() * 48271L);
		} else {
			final long lvl1 = computeErosionLevel(position.withX(position.x() - 1), erosionLevels);
			final long lvl2 = computeErosionLevel(position.withY(position.y() - 1), erosionLevels);
			erosionLevel = toErosionLevel(lvl1 * lvl2);
		}

		erosionLevels.put(position, erosionLevel);
		return erosionLevel;
	}

	private long toErosionLevel(final long geologicalIndex)
	{
		return (geologicalIndex + depth) % 20183;
	}

	private static boolean areTypeAndToolCompatible(final Type type, final Tools tool)
	{
		final boolean canMoveIn;
		canMoveIn = switch (type) {
			case ROCKY -> switch (tool) {
				case CLIMBING, TORCH -> true;
				case NONE -> false;
			};

			case WET -> switch (tool) {
				case CLIMBING, NONE -> true;
				case TORCH -> false;
			};

			case NARROW -> switch (tool) {
				case TORCH, NONE -> true;
				case CLIMBING -> false;
			};
		};
		return canMoveIn;
	}

	private static void enqueueNewPositionWithTool(final PositionWithTool newPositionWithTool, final Node current,
			final Map<PositionWithTool, Node> enqueuedNodes, final UpdatableQueue<Node> queue, final long distance)
	{
		final var enqueuedNode = enqueuedNodes.get(newPositionWithTool);
		boolean needUpdate = false;
		final long newDistance = current.distance + distance;
		if (enqueuedNode != null) {
			if (enqueuedNode.distance > newDistance) {
				enqueuedNode.distance = newDistance;
				needUpdate = true;
			}
		} else {
			final var newNode = new Node(newPositionWithTool, current, newDistance);
			queue.add(newNode);
			enqueuedNodes.put(newNode.positionWithTool, newNode);
		}

		if (needUpdate) {
			queue.update();
		}
	}

	@RecordBuilder
	record Position(int x, int y) implements CaveMapPositionBuilder.With
	{

		public static final Position ORIGIN = new Position(0, 0);

		public List<Position> neighbours()
		{
			return Stream.of( //
							this.withY(y - 1), //
							this.withX(x + 1), //
							this.withY(y + 1), //
							this.withX(x - 1)) //
					.filter(p -> p.x() >= 0 && p.y() >= 0) //
					.toList();
		}

	}

	@RecordBuilder
	record PositionWithTool(Position position, Tools tool) implements CaveMapPositionWithToolBuilder.With {}

	@AllArgsConstructor
	private static class Node
	{
		final PositionWithTool positionWithTool;

		final Node parent;

		@Getter
		long distance;

		@Override
		public String toString()
		{
			return "[(" + positionWithTool.position().x() + "," + //
					positionWithTool.position().y() + "), " + positionWithTool.tool() + ", " + distance + "]";
		}
	}

	enum Tools
	{
		NONE, CLIMBING, TORCH
	}

	private enum Type
	{
		ROCKY, WET, NARROW;

		static Type from(final long value)
		{
			return switch ((int) value) {
				case 0 -> ROCKY;
				case 1 -> WET;
				case 2 -> NARROW;
				default -> throw new IllegalArgumentException("Unsupported type " + value);
			};
		}
	}

}
