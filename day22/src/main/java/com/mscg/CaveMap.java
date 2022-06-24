package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

		return erosionLevels.values().stream() //
				.mapToLong(Long::longValue) //
				.map(l -> l % 3) //
				.sum();
	}

	private long computeErosionLevel(final Position position, final Map<Position, Long> erosionLevels)
	{
		return erosionLevels.computeIfAbsent(position, p -> {
			if (p.y() == 0) {
				return toErosionLevel(p.x() * 16807L);
			} else if (p.x() == 0) {
				return toErosionLevel(p.y() * 48271L);
			} else {
				final long lvl1 = computeErosionLevel(p.withX(p.x() - 1), erosionLevels);
				final long lvl2 = computeErosionLevel(p.withY(p.y() - 1), erosionLevels);
				return toErosionLevel(lvl1 * lvl2);
			}
		});
	}

	private long toErosionLevel(final long geologicalIndex)
	{
		return (geologicalIndex + depth) % 20183;
	}

	@RecordBuilder
	record Position(int x, int y) implements CaveMapPositionBuilder.With
	{

		public static final Position ORIGIN = new Position(0, 0);

	}

}
