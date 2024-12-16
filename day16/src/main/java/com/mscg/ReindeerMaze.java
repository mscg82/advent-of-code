package com.mscg;

import com.mscg.utils.CollectionUtils;
import com.mscg.utils.Position8Bits;
import com.mscg.utils.queue.UpdatablePriorityQueue;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToLongFunction;

@RecordBuilder
public record ReindeerMaze(Set<Position8Bits> walls, Position8Bits startPosition, Position8Bits endPosition, int rows, int cols)
{

	public static ReindeerMaze parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines() //
					.toList();
			final ReindeerMazeBuilder mazeBuilder = ReindeerMazeBuilder.builder();
			mazeBuilder.rows(allLines.size());
			mazeBuilder.cols(allLines.getFirst().length());
			mazeBuilder.walls(new HashSet<>());

			for (int y = 0; y < mazeBuilder.rows(); y++) {
				final String line = allLines.get(y);
				for (int x = 0; x < mazeBuilder.cols(); x++) {
					switch (line.charAt(x)) {
						case '#' -> mazeBuilder.walls().add(new Position8Bits(x, y));
						case 'S' -> mazeBuilder.startPosition(new Position8Bits(x, y));
						case 'E' -> mazeBuilder.endPosition(new Position8Bits(x, y));
						case '.' -> { /* do nothing */ }
						case final char c -> throw new IllegalArgumentException("Invalid character in map line '" + c + "'");
					}
				}
			}

			return mazeBuilder.build();
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public ReindeerMaze
	{
		walls = Collections.unmodifiableSet(walls);
		Objects.requireNonNull(startPosition, "Start position was not found in the map");
		Objects.requireNonNull(endPosition, "End position was not found in the map");
	}

	public long findBestPathCost()
	{
		return findPathToDestinationAndThen(node -> node.weight);
	}

	public long findTilesWithBestPath()
	{
		return findPathToDestinationAndThen(node -> {
			final var uniquePositions = new HashSet<Position8Bits>();
			uniquePositions.add(startPosition);
			uniquePositions.add(endPosition);
			final var queue = new ArrayDeque<Node>();
			queue.add(node);
			while (!queue.isEmpty()) {
				final var current = queue.poll();
				uniquePositions.add(current.status.position());
				queue.addAll(current.parents);
			}
			return uniquePositions.size();
		});
	}

	private long findPathToDestinationAndThen(final ToLongFunction<Node> analyzeFinalNode)
	{
		final var statusToNode = new HashMap<Status, Node>();
		final var queue = new UpdatablePriorityQueue<Node>( //
				Comparator.comparingLong(n -> n.weight));

		final var initialNode = new Node(new Status(startPosition, Direction.EAST), 0, List.of(), true);
		statusToNode.put(initialNode.status, initialNode);
		queue.add(initialNode);

		Node finalNode = null;
		while (!queue.isEmpty()) {
			final var current = queue.poll();
			current.enqueued = false;
			final var currentStatus = current.status;
			if (currentStatus.position().equals(endPosition)) {
				finalNode = current;
				break;
			}

			final List<Status> neighbours = findNeighbours(currentStatus);

			boolean needUpdate = false;

			for (final var neighbour : neighbours) {
				final var existingNode = statusToNode.get(neighbour);
				final long movementCost = neighbour.direction() == currentStatus.direction() ? 1 : 1000;
				final long newTotalCost = current.weight + movementCost;
				if (existingNode == null) {
					final var node = new Node(neighbour, newTotalCost, List.of(current), true);
					statusToNode.put(node.status, node);
					queue.add(node);
				} else {
					if (newTotalCost < existingNode.weight) {
						existingNode.weight = newTotalCost;
						existingNode.parents = List.of(current);
						needUpdate = true;
					} else if (newTotalCost == existingNode.weight) {
						existingNode.parents = CollectionUtils.append(existingNode.parents, current);
					}
				}
			}

			if (needUpdate) {
				queue.update();
			}
		}

		if (finalNode == null) {
			throw new IllegalStateException("Unable to find path to final node");
		}

		return analyzeFinalNode.applyAsLong(finalNode);
	}

	private List<Status> findNeighbours(final Status currentStatus)
	{
		final var neighbours = new ArrayList<Status>(3);
		final var forwardStatus = new Status(currentStatus.direction().move(currentStatus.position()), currentStatus.direction());
		if (!walls.contains(forwardStatus.position())) {
			neighbours.add(forwardStatus);
		}
		var newDir = currentStatus.direction().rotateClockwise();
		if (!walls.contains(newDir.move(currentStatus.position()))) {
			neighbours.add(new Status(currentStatus.position(), newDir));
		}
		newDir = currentStatus.direction().rotateCounterClockwise();
		if (!walls.contains(newDir.move(currentStatus.position()))) {
			neighbours.add(new Status(currentStatus.position(), newDir));
		}
		return neighbours;
	}

	private record Status(Position8Bits position, Direction direction)
	{
		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Status other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return direction.ordinal() << 16 | position.hashCode();
		}

		DirectionalStatus toDirectional()
		{
			return new DirectionalStatus(position, switch (direction) {
				case NORTH, SOUTH -> false;
				case EAST, WEST -> true;
			});
		}
	}

	@AllArgsConstructor
	@ToString(exclude = "parents")
	private static class Node
	{
		Status status;

		long weight;

		List<Node> parents;

		boolean enqueued;
	}

	private record DirectionalStatus(Position8Bits position, boolean horizontal)
	{
		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final DirectionalStatus other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return (horizontal ? 1 : 0) << 16 | position.hashCode();
		}
	}

	private enum Direction
	{
		NORTH, EAST, SOUTH, WEST;

		public Direction rotateClockwise()
		{
			return switch (this) {
				case NORTH -> Direction.EAST;
				case EAST -> Direction.SOUTH;
				case SOUTH -> Direction.WEST;
				case WEST -> Direction.NORTH;
			};
		}

		public Direction rotateCounterClockwise()
		{
			return switch (this) {
				case NORTH -> Direction.WEST;
				case EAST -> Direction.NORTH;
				case SOUTH -> Direction.EAST;
				case WEST -> Direction.SOUTH;
			};
		}

		public Position8Bits move(final Position8Bits position)
		{
			return switch (this) {
				case NORTH -> position.withY(position.y() - 1);
				case EAST -> position.withX(position.x() + 1);
				case SOUTH -> position.withY(position.y() + 1);
				case WEST -> position.withX(position.x() - 1);
			};
		}
	}

}
