package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record NanobotNetwork(List<Nanobot> nanobots)
{
	public static NanobotNetwork parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Nanobot> nanobots = in.lines() //
					.map(Nanobot::from) //
					.toList();
			return new NanobotNetwork(nanobots);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findNanobotsInRange()
	{
		final Nanobot maxNanobot = nanobots.stream() //
				.max(Comparator.comparingLong(Nanobot::radius)) //
				.orElseThrow();
		return nanobots.stream() //
				.filter(bot -> bot.position().distance(maxNanobot.position()) <= maxNanobot.radius()) //
				.count();
	}

	public long findDistanceToMaxOverllappedPoint()
	{
		final Map<Long, Integer> distanceToCount = nanobots.stream() //
				.flatMap(nanobot -> {
					final long distanceFromOrigin = nanobot.position().distance(Position.ORIGIN);
					return Stream.of( //
							Map.entry(Math.max(distanceFromOrigin - nanobot.radius(), 0L), 1), //
							Map.entry(distanceFromOrigin + nanobot.radius(), -1));
				}) //
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));

		int count = 0;
		int maxCount = Integer.MIN_VALUE;
		long result = Long.MIN_VALUE;

		for (final var entry : distanceToCount.entrySet()) {
			count += entry.getValue();
			if (count > maxCount) {
				result = entry.getKey();
				maxCount = count;
			}
		}

		return result;
	}

	public record Position(long x, long y, long z)
	{

		public static final Position ORIGIN = new Position(0, 0, 0);

		public long distance(final Position other)
		{
			return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
		}

	}

	public record Nanobot(Position position, long radius)
	{
		public static Nanobot from(final String line)
		{
			final var pattern = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");
			final var matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Unuspported definition: " + line);
			}
			return new Nanobot( //
					new Position(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)),
							Long.parseLong(matcher.group(3))), //
					Long.parseLong(matcher.group(4)));
		}
	}

}
