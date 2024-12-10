package com.mscg;

import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public record HikingMap(List<IntList> heightsMap)
{

	public static HikingMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<IntList> heightsMap = in.lines() //
					.map(line -> IntImmutableList.of(line.chars() //
							.map(h -> h - '0') //
							.toArray())) //
					.map(IntList.class::cast) //
					.toList();
			return new HikingMap(heightsMap);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long scoreHikingTrails()
	{
		return walkMap(false);
	}

	public long rateHikingTrails()
	{
		return walkMap(true);
	}

	private long walkMap(final boolean computeRating)
	{
		final int rows = heightsMap.size();
		final int cols = heightsMap.getFirst().size();

		record Position(int x, int y) {}
		record Status(Position position, Position endPosition, Status previous) {}
		record HikeExtremes(Position start, Position end) {}

		final var positionToHikes = new Object2IntOpenHashMap<Position>();
		final var discoveredHikes = new HashSet<HikeExtremes>();
		final var queue = new ArrayDeque<Status>();

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				final var height = heightsMap.get(y).getInt(x);
				if (height == 9) {
					final Position endPosition = new Position(x, y);
					queue.add(new Status(endPosition, endPosition, null));
				}
			}
		}

		while (!queue.isEmpty()) {
			final var current = queue.poll();
			final var currentPosition = current.position();
			final int currentHeight = heightsMap.get(currentPosition.y()).getInt(currentPosition.x());
			if (currentHeight == 0) {
				final var hike = new HikeExtremes(currentPosition, current.endPosition());
				if (computeRating || !discoveredHikes.contains(hike)) {
					positionToHikes.mergeInt(currentPosition, 1, Integer::sum);
					discoveredHikes.add(hike);
				}
				continue;
			}

			Stream.of(new Position(currentPosition.x() + 1, currentPosition.y()), //
							new Position(currentPosition.x(), currentPosition.y() + 1), //
							new Position(currentPosition.x() - 1, currentPosition.y()), //
							new Position(currentPosition.x(), currentPosition.y() - 1)) //
					.filter(pos -> pos.x() >= 0 && pos.x() < cols && pos.y() >= 0 && pos.y() < rows) //
					.filter(pos -> {
						final var height = heightsMap.get(pos.y()).getInt(pos.x());
						return height == currentHeight - 1;
					}) //
					.map(pos -> new Status(pos, current.endPosition(), current)) //
					.forEach(queue::add);
		}

		return positionToHikes.values().intStream() //
				.mapToLong(n -> (long) n) //
				.sum();
	}

}
