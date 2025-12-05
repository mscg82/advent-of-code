package com.mscg.utils;

import io.soabase.recordbuilder.core.RecordBuilderFull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RecordBuilderFull
public record InclusiveRange(long low, long high) implements Comparable<InclusiveRange>, InclusiveRangeBuilder.With
{

	private static final Comparator<InclusiveRange> COMPARATOR = Comparator //
			.comparingLong(InclusiveRange::low) //
			.thenComparingLong(InclusiveRange::high);

	public static InclusiveRange from(final String line)
	{
		final var parts = line.split("-");
		return new InclusiveRange(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
	}

	public static List<InclusiveRange> mergeIntoNonOverlappingRanges(final List<InclusiveRange> ranges)
	{
		final var sortedRanges = new ArrayList<>(ranges);
		sortedRanges.sort(Comparator.naturalOrder());
		final var nonOverlappingRanges = new ArrayList<InclusiveRange>();
		var currentRangeBuilder = InclusiveRangeBuilder.builder(sortedRanges.getFirst());
		for (int i = 1, l = sortedRanges.size(); i < l; i++) {
			final InclusiveRange range = sortedRanges.get(i);
			if (range.low() > currentRangeBuilder.high()) {
				nonOverlappingRanges.add(currentRangeBuilder.build());
				currentRangeBuilder = InclusiveRangeBuilder.builder(range);
			} else {
				currentRangeBuilder.high(Math.max(currentRangeBuilder.high(), range.high()));
			}
		}
		nonOverlappingRanges.add(currentRangeBuilder.build());

		return nonOverlappingRanges;
	}

	public boolean contains(final long value)
	{
		return low <= value && value <= high;
	}

	public long size()
	{
		return high - low + 1;
	}

	public boolean overlaps(final InclusiveRange other)
	{
		return high >= other.low && other.high >= low;
	}

	@Override
	public int compareTo(final InclusiveRange other)
	{
		return COMPARATOR.compare(this, other);
	}

}
