package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record CaveMap(Set<Position> rockPositions)
{

	public static CaveMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Set<Position> rockPositions = in.lines() //
					.flatMap(line -> {
						final List<Position> chain = Arrays.stream(line.split(" -> ")) //
								.map(Position::from) //
								.toList();
						return Stream.concat( //
								StreamUtils.windowed(chain, 2) //
										.flatMap(win -> {
											final var window = List.copyOf(win);
											final var delta = window.get(0).delta(window.get(1));
											return Stream.iterate(window.get(0), p -> !p.equals(window.get(1)), p -> p.add(delta));
										}), //
								Stream.of(chain.get(chain.size() - 1)));
					}) //
					.collect(Collectors.toCollection(LinkedHashSet::new));
			return new CaveMap(Collections.unmodifiableSet(rockPositions));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long pourSand()
	{
		final long maxY = rockPositions.stream() //
				.mapToLong(Position::y) //
				.max() //
				.orElseThrow();

		return pourSand(pos -> pos.y() > maxY, rockPositions::contains);
	}

	public long pourSandOnBed()
	{
		final long maxY = rockPositions.stream() //
				.mapToLong(Position::y) //
				.max() //
				.orElseThrow() + 2;

		return pourSand(pos -> pos.y() == 0, pos -> pos.y() >= maxY || rockPositions.contains(pos));
	}

	private long pourSand(final Predicate<Position> escapedTest, final Predicate<Position> rockPositionTest)
	{
		final var down = new Position(0, 1);
		final var downLeft = new Position(-1, 1);
		final var downRight = new Position(1, 1);

		final Set<Position> grains = new HashSet<>();
		boolean escaped = false;
		while (!escaped) {
			var grainPos = new Position(500, 0);
			boolean atRest = false;
			while (!atRest && !escaped) {
				final var downPos = grainPos.add(down);
				if (!rockPositionTest.test(downPos) && !grains.contains(downPos)) {
					// free fall
					grainPos = downPos;
					if (escapedTest.test(grainPos)) {
						escaped = true;
					}
				} else {
					// check if we can fall on the left
					final var downLeftPos = grainPos.add(downLeft);
					if (!rockPositionTest.test(downLeftPos) && !grains.contains(downLeftPos)) {
						// we can fall
						grainPos = downLeftPos;
						if (escapedTest.test(grainPos)) {
							escaped = true;
						}
					} else {
						// check if we can fall on the right
						final var downRightPos = grainPos.add(downRight);
						if (!rockPositionTest.test(downRightPos) && !grains.contains(downRightPos)) {
							// we can fall
							grainPos = downRightPos;
							if (escapedTest.test(grainPos)) {
								escaped = true;
							}
						} else {
							// we are at rest
							grains.add(grainPos);
							if (escapedTest.test(grainPos)) {
								escaped = true;
							}
							atRest = true;
						}
					}
				}
			}
		}

		return grains.size();
	}

	@SuppressWarnings("java:S3358")
	private static long signum(final long value)
	{
		return value > 0 ? 1 : value == 0 ? 0 : -1;
	}

	public record Position(long x, long y)
	{
		public static Position from(final String line)
		{
			final String[] parts = line.split(",");
			return new Position(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
		}

		public Position delta(final Position other)
		{
			return new Position(signum(other.x - x), signum(other.y - y));
		}

		public Position add(final Position other)
		{
			return new Position(other.x + x, other.y + y);
		}

		@Override
		public String toString()
		{
			return "(" + x + ", " + y + ")";
		}
	}

}
