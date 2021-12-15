package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record CaveMap(Map<Position, List<Neighbour>> adjacencyMap, int rows, int cols)
{

	public static CaveMap parseInput(final BufferedReader in, final boolean expand) throws IOException
	{
		try {
			int[][] parsedInputArray = in.lines() //
					.map(line -> line.chars() //
							.map(c -> c - '0') //
							.toArray()) //
					.toArray(int[][]::new);

			if (expand) {
				final int[][] expandedInputArray = new int[parsedInputArray.length * 5][parsedInputArray[0].length * 5];

				final int rows = parsedInputArray.length;
				final int cols = parsedInputArray[0].length;

				for (int offsetY = 0; offsetY < 5; offsetY++) {
					for (int offsetX = 0; offsetX < 5; offsetX++) {
						for (int y = 0; y < rows; y++) {
							for (int x = 0; x < cols; x++) {
								int newValue = parsedInputArray[y][x] + offsetX + offsetY;
								while (newValue > 9) {
									newValue -= 9;
								}
								expandedInputArray[y + offsetY * rows][x + offsetX * cols] = newValue;
							}
						}
					}
				}

				parsedInputArray = expandedInputArray;
			}

			final int[][] inputArray = parsedInputArray;

			final int rows = inputArray.length;
			final int cols = inputArray[0].length;

			final Map<Position, List<Neighbour>> adjacencyMap = new LinkedHashMap<>();
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					final var position = new Position(x, y);
					final List<Neighbour> neighbours = position.getNeighbours(rows, cols) //
							.map(pos -> new Neighbour(pos, inputArray[pos.y()][pos.x()])) //
							.toList();
					adjacencyMap.put(position, neighbours);
				}
			}

			return new CaveMap(Collections.unmodifiableMap(adjacencyMap), rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int findLowestRiskPath()
	{
		final var startPos = new Position(0, 0);
		final var endPos = new Position(cols - 1, rows - 1);

		final Set<Position> visitedPositions = new HashSet<>();
		final Map<Position, Node> positionToNode = new HashMap<>();
		final UpdatableQueue<Node> queue = new UpdatablePriorityQueue<>(Comparator.comparingInt(Node::getWeight));
		final Node startingNode = new Node(startPos, 0, null);
		queue.add(startingNode);
		positionToNode.put(startingNode.getPosition(), startingNode);

		Node endNode = null;
		while (!queue.isEmpty()) {
			final Node node = queue.poll();
			if (node.getPosition().equals(endPos)) {
				endNode = node;
				break;
			}

			visitedPositions.add(node.getPosition());

			boolean needUpdate = false;
			for (final Neighbour neigh : adjacencyMap.get(node.getPosition())) {
				if (visitedPositions.contains(neigh.position())) {
					continue;
				}

				var neighNode = positionToNode.get(neigh.position());
				if (neighNode == null) {
					neighNode = new Node(neigh.position(), node.getWeight() + neigh.weight(), node);
					positionToNode.put(neighNode.getPosition(), neighNode);
					queue.add(neighNode);
				} else {
					if (node.getWeight() + neigh.weight() < neighNode.getWeight()) {
						neighNode.setWeight(node.getWeight() + neigh.weight());
						neighNode.setParent(node);
						needUpdate = true;
					}
				}
			}
			if (needUpdate) {
				queue.update();
			}
		}

		if (endNode == null) {
			throw new IllegalStateException("Can't find path to end position");
		}

		return endNode.getWeight();
	}

	@AllArgsConstructor
	@Getter
	@Setter
	private static final class Node
	{
		private final Position position;

		private int weight;

		private Node parent;
	}

	public record Position(int x, int y)
	{

		public Stream<Position> getNeighbours(final int rows, final int cols)
		{
			return Stream.of(new Position(x, y - 1), //
					new Position(x + 1, y), //
					new Position(x, y + 1), //
					new Position(x - 1, y)) //
					.filter(pos -> pos.x >= 0 && pos.x < cols && pos.y >= 0 && pos.y < rows);
		}

	}

	public record Neighbour(Position position, int weight)
	{

	}

}
