package com.mscg;

import com.mscg.utils.queue.UpdatablePriorityQueue;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.lambda.function.Function3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public record CrucibleMap(List<List<Integer>> heatLosses)
{
	public static CrucibleMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<Integer>> blockWeights = in.lines() //
					.map(line -> line.chars() //
							.map(c -> c - '0') //
							.boxed() //
							.toList()) //
					.toList();
			return new CrucibleMap(blockWeights);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findMinHeatLoss()
	{
		return findMinHeatLoss(null);
	}

	public long findMinHeatLossBigCrucible()
	{
		return findMinHeatLossBigCrucible(null);
	}

	long findMinHeatLoss(final Consumer<String> pathElementPrinter)
	{
		return internalFindMinHeatLoss(pathElementPrinter, CrucibleStatus::nextStatuses, _ -> true);
	}

	long findMinHeatLossBigCrucible(final Consumer<String> pathElementPrinter)
	{
		return internalFindMinHeatLoss(pathElementPrinter, CrucibleStatus::nextStatusesBigCrucible, //
				status -> status.stepsInDirection() >= 4);
	}

	private long internalFindMinHeatLoss(final Consumer<String> pathElementPrinter, //
			final Function3<CrucibleStatus, Integer, Integer, List<CrucibleStatus>> nextStatusGenerator, //
			final Predicate<CrucibleStatus> validStatus)
	{
		final int yMax = heatLosses.size();
		final int xMax = heatLosses.getFirst().size();

		final var targetPosition = new Position(xMax - 1, yMax - 1);
		Node minHeatLossStatusNode = null;

		final var queue = new UpdatablePriorityQueue<>(Comparator.comparingLong(Node::getTotalHeatLoss));
		final var visitedNodes = HashSet.<CrucibleStatus>newHashSet(800_000);
		final var parentNode = new Node(new CrucibleStatus(new Position(0, 0), Direction.UP), 0, null);
		queue.add(Node.fromHeatLosses(new CrucibleStatus(new Position(1, 0), Direction.RIGHT), heatLosses, parentNode));
		queue.add(Node.fromHeatLosses(new CrucibleStatus(new Position(0, 1), Direction.DOWN), heatLosses, parentNode));

		while (!queue.isEmpty()) {
			final var node = queue.poll();
			final CrucibleStatus status = node.getStatus();
			if (visitedNodes.contains(status)) {
				continue;
			}
			visitedNodes.add(status);

			final Position position = status.position();
			if (position.equals(targetPosition) && //
					validStatus.test(status)) {
				minHeatLossStatusNode = node;
				break;
			}

			final List<CrucibleStatus> nextStatuses = nextStatusGenerator.apply(status, xMax, yMax);
			for (final CrucibleStatus neigh : nextStatuses) {
				final int heatLossAtPosition = heatLosses.get(neigh.position().y()).get(neigh.position().x());
				final long newHeatLoss = node.getTotalHeatLoss() + heatLossAtPosition;
				final Node neighNode = new Node(neigh, newHeatLoss, node);
				queue.add(neighNode);
			}
		}

		if (minHeatLossStatusNode == null) {
			throw new IllegalStateException("Min heat loss path not found");
		}

		if (pathElementPrinter != null) {
			printPath(pathElementPrinter, minHeatLossStatusNode);
		}

		return minHeatLossStatusNode.getTotalHeatLoss();
	}

	private static void printPath(final Consumer<String> pathElementPrinter, final Node minHeatLossStatusNode)
	{
		final List<String> path = Stream.iterate(minHeatLossStatusNode, Objects::nonNull, Node::getParent) //
				.map(node -> "(%d, %d - %s, %d) - %d".formatted( //
						node.getStatus().position().x(), //
						node.getStatus().position().y(), //
						node.getStatus().direction(), //
						node.getStatus().stepsInDirection(), //
						node.getTotalHeatLoss())) //
				.collect(Collector.<String, ArrayDeque<String>, List<String>>of( //
						ArrayDeque::new, //
						ArrayDeque::addFirst, //
						(_, _) -> {
							throw new IllegalStateException("");
						}, //
						ArrayList::new));
		path.forEach(pathElementPrinter);
	}

	@RecordBuilder
	record Position(int x, int y) implements CrucibleMapPositionBuilder.With
	{
		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Position other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return y << 8 | x;
		}

		boolean isInsideBounds(final int xMax, final int yMax)
		{
			return x >= 0 && x < xMax && y >= 0 && y < yMax;
		}

		Position move(final Direction direction)
		{
			return switch (direction) {
				case UP -> this.withY(y - 1);
				case RIGHT -> this.withX(x + 1);
				case DOWN -> this.withY(y + 1);
				case LEFT -> this.withX(x - 1);
			};
		}
	}

	@RecordBuilder
	record CrucibleStatus(Position position, Direction direction, int stepsInDirection)
			implements CrucibleMapCrucibleStatusBuilder.With
	{
		CrucibleStatus(final Position position, final Direction direction)
		{
			this(position, direction, 1);
		}

		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final CrucibleStatus other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			int hash = stepsInDirection;
			hash = hash << 2 | direction.ordinal();
			hash = hash << 16 | position.hashCode();
			return hash;
		}

		List<CrucibleStatus> nextStatuses(final int xMax, final int yMax)
		{
			return Stream.of( //
							this.with(s -> {
								s.position(s.position().move(s.direction()));
								s.stepsInDirection(s.stepsInDirection() + 1);
							}),

							this.with(s -> {
								s.direction(s.direction().turnLeft());
								s.position(s.position().move(s.direction()));
								s.stepsInDirection(1);
							}),

							this.with(s -> {
								s.direction(s.direction().turnRight());
								s.position(s.position().move(s.direction()));
								s.stepsInDirection(1);
							})) //
					.filter(s -> s.position().isInsideBounds(xMax, yMax) && s.stepsInDirection() <= 3) //
					.toList();
		}

		List<CrucibleStatus> nextStatusesBigCrucible(final int xMax, final int yMax)
		{
			final List<CrucibleStatus> nextStatuses = new ArrayList<>(3);
			if (stepsInDirection < 10) {
				final CrucibleStatus forward = this.with(s -> {
					s.position(s.position().move(s.direction()));
					s.stepsInDirection(s.stepsInDirection() + 1);
				});
				if (forward.position.isInsideBounds(xMax, yMax)) {
					nextStatuses.add(forward);
				}
			}
			if (stepsInDirection >= 4) {
				final CrucibleStatus right = this.with(s -> {
					s.direction(s.direction().turnLeft());
					s.position(s.position().move(s.direction()));
					s.stepsInDirection(1);
				});
				if (right.position.isInsideBounds(xMax, yMax)) {
					nextStatuses.add(right);
				}

				final CrucibleStatus left = this.with(s -> {
					s.direction(s.direction().turnRight());
					s.position(s.position().move(s.direction()));
					s.stepsInDirection(1);
				});
				if (left.position.isInsideBounds(xMax, yMax)) {
					nextStatuses.add(left);
				}
			}

			return Collections.unmodifiableList(nextStatuses);
		}
	}

	@Data
	@AllArgsConstructor
	private static class Node
	{
		private final CrucibleStatus status;

		private long totalHeatLoss;

		private Node parent;

		static Node fromHeatLosses(final CrucibleStatus status, final List<List<Integer>> heatLosses, final Node parent)
		{
			final Position position = status.position();
			return new Node(status, heatLosses.get(position.y()).get(position.x()), parent);
		}
	}

	enum Direction
	{
		UP, DOWN, LEFT, RIGHT;

		public Direction turnRight()
		{
			return switch (this) {
				case UP -> RIGHT;
				case RIGHT -> DOWN;
				case DOWN -> LEFT;
				case LEFT -> UP;
			};
		}

		public Direction turnLeft()
		{
			return switch (this) {
				case UP -> LEFT;
				case RIGHT -> UP;
				case DOWN -> RIGHT;
				case LEFT -> DOWN;
			};
		}
	}
}
