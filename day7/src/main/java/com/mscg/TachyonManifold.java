package com.mscg;

import com.mscg.utils.Position8Bits;
import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record TachyonManifold(Position8Bits start, Set<Position8Bits> splitters, int rows, int cols)
{
	public static TachyonManifold parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.toList();
			Position8Bits start = null;
			final Set<Position8Bits> splitters = new HashSet<>();
			final int cols = allLines.size();
			final int rows = allLines.getFirst().length();

			for (int y = 0; y < cols; y++) {
				final String line = allLines.get(y);
				for (int x = 0; x < rows; x++) {
					final char c = line.charAt(x);
					switch (c) {
						case 'S' -> start = new Position8Bits(x, y);
						case '^' -> splitters.add(new Position8Bits(x, y));
						case '.' -> { /* empty space */ }
						default -> throw new IllegalArgumentException("Unsupported character: " + c);
					}
				}
			}
			return new TachyonManifold(start, Collections.unmodifiableSet(splitters), rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countSplits()
	{
		final Set<Position8Bits> usedSplitters = new HashSet<>();

		var rays = Set.of(start);
		while (!rays.isEmpty()) {
			final var newRays = new HashSet<Position8Bits>();
			for (final Position8Bits ray : rays) {
				final var movedDown = ray.withY(ray.y() + 1);
				if (movedDown.y() >= cols) {
					continue;
				}

				if (splitters.contains(movedDown)) {
					if (usedSplitters.add(movedDown)) {
						newRays.add(movedDown.withX(movedDown.x() + 1));
						newRays.add(movedDown.withX(movedDown.x() - 1));
					}
				} else {
					newRays.add(movedDown);
				}
			}
			rays = newRays;
		}

		return usedSplitters.size();
	}

	public long countTimelines()
	{
		final List<Position8Bits> sortedSplitters = splitters.stream() //
				.sorted(Comparator.comparingInt(Position8Bits::y).reversed()) //
				.toList();

		final Object2LongMap<Position8Bits> cache = new Object2LongOpenHashMap<>();
		for (final Position8Bits splitter : sortedSplitters) {
			cache.put(splitter, countTimelinesFromPosition(splitter, cache));
		}

		return countTimelinesFromPosition(start, cache);
	}

	private long countTimelinesFromPosition(final Position8Bits pos, final Object2LongMap<Position8Bits> cache)
	{
		long timelines = 0;

		final var queue = new ArrayDeque<Position8Bits>();
		queue.add(pos);
		while (!queue.isEmpty()) {
			final var ray = queue.poll();
			if (splitters.contains(ray)) {
				final long cachedValue = cache.getOrDefault(ray, -1);
				if (cachedValue != -1) {
					timelines += cachedValue;
				} else {
					queue.addFirst(ray.withX(ray.x() + 1));
					queue.addFirst(ray.withX(ray.x() - 1));
				}
			} else {
				final var movedDown = ray.withY(ray.y() + 1);
				if (movedDown.y() >= cols) {
					timelines++;
					continue;
				}
				queue.addFirst(movedDown);
			}
		}

		return timelines;
	}
}
