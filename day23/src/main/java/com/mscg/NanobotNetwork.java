package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

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

	public record Position(long x, long y, long z)
	{

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
