package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SpaceTimeConstellation(List<Point> points)
{
	public static SpaceTimeConstellation parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Point> points = in.lines() //
					.map(line -> {
						final String[] parts = line.split(",");
						return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
								Integer.parseInt(parts[3]));
					}) //
					.toList();
			return new SpaceTimeConstellation(points);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	@SuppressWarnings("java:S117")
	public long countConstellations()
	{
		final Map<Point, Map<Point, Long>> distances = new HashMap<>();
		for (int i = 0, l = points.size(); i < l - 1; i++) {
			final Point p1 = points.get(i);
			for (int j = i + 1; j < l; j++) {
				final Point p2 = points.get(j);
				final long distance = p1.distance(p2);
				distances.computeIfAbsent(p1, __ -> new HashMap<>()).put(p2, distance);
				distances.computeIfAbsent(p2, __ -> new HashMap<>()).put(p1, distance);
			}
		}

		List<Constellation> constellations = points.stream() //
				.map(Constellation::of) //
				.toList();

		while (true) {
			final List<Constellation> mergedConstellations = new ArrayList<>();
			for (final Constellation constellation : constellations) {
				tryMerge(constellation, mergedConstellations).ifPresentOrElse( //
						targetConstellation -> targetConstellation.points().addAll(constellation.points), //
						() -> mergedConstellations.add(constellation));
			}

			if (mergedConstellations.size() == constellations.size()) {
				break;
			}
			constellations = mergedConstellations;
		}

		return constellations.size();
	}

	private Optional<Constellation> tryMerge(final Constellation constellation, final List<Constellation> mergedConstellations)
	{
		for (final Point constellationPoint : constellation.points()) {
			for (final Constellation mergedConstellation : mergedConstellations) {
				for (final Point targetPoint : mergedConstellation.points()) {
					if (targetPoint.distance(constellationPoint) <= 3) {
						return Optional.of(mergedConstellation);
					}
				}
			}
		}
		return Optional.empty();
	}

	public record Constellation(List<Point> points)
	{

		public static Constellation of(final Point point)
		{
			return new Constellation(new ArrayList<>(List.of(point)));
		}

	}

	public record Point(int x, int y, int z, int t)
	{

		public long distance(final Point other)
		{
			return (long) Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z) + Math.abs(t - other.t);
		}

	}

}
