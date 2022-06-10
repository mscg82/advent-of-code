package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@RecordBuilder
public record TerrainMap(Set<Position> clayPositions, Set<Position> waterPositions, Set<Position> dripPositions, int minY, int maxY)
		implements TerrainMapBuilder.With
{
	public static TerrainMap parseInput(final BufferedReader in) throws IOException
	{
		final var pattern = Pattern.compile("([xy])=(\\d+), [xy]=(\\d+)\\.\\.(\\d+)");
		try {
			final var clayPositions = in.lines() //
					.map(pattern::matcher) //
					.filter(Matcher::matches) //
					.flatMap(matcher -> {
						final var firstVar = matcher.group(1);
						final int firstVal = Integer.parseInt(matcher.group(2));
						final int secondLowBound = Integer.parseInt(matcher.group(3));
						final int secondHighBound = Integer.parseInt(matcher.group(4));
						return IntStream.rangeClosed(secondLowBound, secondHighBound) //
								.mapToObj(secondVal -> "x".equalsIgnoreCase(firstVar) ?
										new Position(firstVal, secondVal) :
										new Position(secondVal, firstVal));
					}) //
					.collect(Collectors.toUnmodifiableSet());
			final var stats = clayPositions.stream() //
					.mapToInt(Position::y) //
					.summaryStatistics();
			return new TerrainMap(clayPositions, Set.of(), Set.of(new Position(500, 0)), stats.getMin(), stats.getMax());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public TerrainMap evolve()
	{
		TerrainMap lastMap = null;
		final Deque<TerrainMap> queue = new ArrayDeque<>();
		queue.add(this.with(map -> {
			map.clayPositions(new HashSet<>(clayPositions));
			map.waterPositions(new HashSet<>(waterPositions));
			map.dripPositions(new HashSet<>(dripPositions));
		}));
		while (!queue.isEmpty()) {
			lastMap = queue.pop();
			final boolean anyDripInsideBoundaries = lastMap.dripPositions.stream().anyMatch(pos -> pos.y() < maxY);
			if (anyDripInsideBoundaries) {
				final TerrainMap evolvedMap = lastMap.letWaterDrip();
				queue.addFirst(evolvedMap);
			}
		}
		return Objects.requireNonNull(lastMap).with(map -> {
			map.clayPositions(Collections.unmodifiableSet(map.clayPositions()));
			map.waterPositions(Collections.unmodifiableSet(map.waterPositions()));
			map.dripPositions(Collections.unmodifiableSet(map.dripPositions()));
		});
	}

	@Override
	public String toString()
	{
		return "{ clayPositions = " + clayPositions.size() + ", " + //
				"waterPositions = " + waterPositions.size() + "," + //
				"minY = " + minY + ", " + //
				"maxY = " + maxY + " }";
	}

	public String asMapString()
	{
		final var xStats = Stream.concat(clayPositions.stream(), waterPositions.stream()) //
				.mapToInt(Position::x) //
				.summaryStatistics();
		final var yStats = Stream.concat(clayPositions.stream(), waterPositions.stream()) //
				.mapToInt(Position::y) //
				.summaryStatistics();
		final var clayPositions = new HashSet<>(this.clayPositions);
		final var waterPositions = new HashSet<>(this.waterPositions);

		final int lineWidth = xStats.getMax() - xStats.getMin();
		final int height = yStats.getMax() - yStats.getMin();
		final StringBuilder map = new StringBuilder(height * (lineWidth + 1));
		final int bound = yStats.getMax();
		for (int y = yStats.getMin(); y <= bound; y++) {
			final StringBuilder line = new StringBuilder(lineWidth);
			for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
				final var pos = new Position(x, y);
				if (clayPositions.contains(pos)) {
					line.append('#');
				} else if (waterPositions.contains(pos)) {
					line.append("w");
				} else {
					line.append(".");
				}
			}
			map.append(line).append('\n');
		}
		return map.substring(0, map.length() - 1);
	}

	TerrainMap letWaterDrip()
	{
		final Set<Position> newWaterPositions = new HashSet<>(waterPositions);
		final Set<Position> newDripPositions = new HashSet<>();

		final Iterable<Position> validDripPositions = () -> dripPositions.stream() //
				.filter(pos -> pos.y() < maxY) //
				.iterator();
		for (final Position position : validDripPositions) {
			final var downPosition = position.withY(position.y() + 1);
			if (clayPositions.contains(downPosition) || waterPositions.contains(downPosition)) {
				// expand water horizzontally
				final var newHorPositions = expandWaterHorizontally(position);
				final var left = newHorPositions.first();
				final var right = newHorPositions.last();
				if (clayPositions.contains(left) && clayPositions.contains(right)) {
					// we are in a basin, so fill the line and go up
					newWaterPositions.addAll(newHorPositions.subSet(left, false, right, false));
					newDripPositions.add(position.withY(position.y() - 1));
				} else if (!clayPositions.contains(left) && !clayPositions.contains(right)) {
					// we spill from both sides, but we must have clay supporting on both sides
					final var leftDownRight = left.with(p -> {
						p.y(p.y() + 1);
						p.x(p.x() + 1);
					});
					final var rightDownLeft = right.with(p -> {
						p.y(p.y() + 1);
						p.x(p.x() - 1);
					});
					if (clayPositions.contains(leftDownRight) && clayPositions.contains(rightDownLeft)) {
						newWaterPositions.addAll(newHorPositions);
						if (!waterPositions.contains(left)) {
							newDripPositions.add(left);
						}
						if (!waterPositions.contains(right)) {
							newDripPositions.add(right);
						}
					}
				} else {
					// we spill from one side only and under the spill position there must be clay
					final Position spillPosition;
					final Position spillDown;
					if (clayPositions.contains(left)) {
						spillPosition = right;
						spillDown = right.with(p -> {
							p.y(p.y() + 1);
							p.x(p.x() - 1);
						});
					} else {
						spillPosition = left;
						spillDown = left.with(p -> {
							p.y(p.y() + 1);
							p.x(p.x() + 1);
						});
					}

					if (clayPositions.contains(spillDown)) {
						newHorPositions.stream() //
								.filter(not(clayPositions::contains)) //
								.forEach(newWaterPositions::add);
						if (!waterPositions.contains(spillPosition)) {
							newDripPositions.add(spillPosition);
						}
					}
				}
			} else {
				// water falls vertically
				newWaterPositions.add(downPosition);
				newDripPositions.add(downPosition);
			}
		}

		return this.with(m -> {
			m.waterPositions(newWaterPositions);
			m.dripPositions(newDripPositions);
		});
	}

	private TreeSet<Position> expandWaterHorizontally(final Position position)
	{
		final Position leftPosition = findHorizontalPosition(position, -1);
		final Position rightPosition = findHorizontalPosition(position, 1);
		return IntStream.rangeClosed(leftPosition.x(), rightPosition.x()) //
				.mapToObj(x -> new Position(x, position.y())) //
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Position::x))));
	}

	private Position findHorizontalPosition(final Position position, final int dx)
	{
		var lastPosition = position;
		while (!clayPositions.contains(lastPosition) && isPositionSupported(lastPosition)) {
			lastPosition = lastPosition.withX(lastPosition.x() + dx);
		}
		return lastPosition;
	}

	private boolean isPositionSupported(final Position position)
	{
		final var downPosition = position.withY(position.y() + 1);
		return clayPositions.contains(downPosition) || waterPositions.contains(downPosition);
	}

	@RecordBuilder
	public record Position(int x, int y) implements TerrainMapPositionBuilder.With {}

}
