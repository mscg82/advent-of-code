package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record ElfCamp(List<RangePair> pairs)
{
	public static ElfCamp parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<RangePair> pairs = in.lines() //
					.map(RangePair::from) //
					.toList();
			return new ElfCamp(pairs);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countRedundantPairs()
	{
		return pairs.stream() //
				.filter(RangePair::hasRedundant) //
				.count();
	}

	public long countOverlappingPairs()
	{
		return pairs.stream() //
				.filter(RangePair::hasOverlap) //
				.count();
	}

	record RangePair(Range first, Range second)
	{
		public static RangePair from(final String line)
		{
			final var parts = line.split(",");
			return new RangePair(Range.from(parts[0]), Range.from(parts[1]));
		}

		public boolean hasRedundant()
		{
			return first.contains(second) || second.contains(first);
		}

		public boolean hasOverlap()
		{
			return first.overlaps(second);
		}
	}

	record Range(long from, long to)
	{
		public static Range from(final String line)
		{
			final var parts = line.split("-");
			return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
		}

		Range
		{
			if (from > to) {
				throw new IllegalArgumentException("from > to (" + from + ", " + to + ")");
			}
		}

		public boolean contains(final Range other)
		{
			return other.from >= from && other.to <= to;
		}

		public boolean overlaps(final Range other)
		{
			final var first = (from <= other.from ? this : other);
			final var second = (from <= other.from ? other : this);
			return second.from <= first.to;
		}
	}

}
