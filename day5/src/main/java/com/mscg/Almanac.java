package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mscg.utils.StringTemplates.ILLEGAL_ARGUMENT_EXC;
import static java.util.function.Predicate.not;

public record Almanac(long[] seeds, Map<String, AlmanacMap> almanacMaps)
{

	public static Almanac parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> blocks = in.lines().collect(StreamUtils.splitAt(String::isBlank));
			final String seedsStr = blocks.get(0).get(0);

			final var seeds = Arrays.stream(seedsStr.substring(seedsStr.indexOf(':') + 1).split(" ")) //
					.map(String::trim) //
					.filter(not(String::isBlank)) //
					.mapToLong(Long::parseLong) //
					.toArray();

			final var mapTypePattern = Pattern.compile("([^-]+)-to-([^ ]+) map:");
			final LinkedHashMap<String, AlmanacMap> almanacMaps = blocks.stream() //
					.skip(1) //
					.map(block -> {
						final var mapTypeMatcher = mapTypePattern.matcher(block.get(1));
						if (!mapTypeMatcher.matches()) {
							throw ILLEGAL_ARGUMENT_EXC."Unsupported map type format \"\{block.get(1)}\"";
						}
						final var source = mapTypeMatcher.group(1);
						final var target = mapTypeMatcher.group(2);
						final var rangeEntries = block.stream() //
								.skip(2) //
								.map(line -> {
									final String[] parts = line.split(" ");
									final long valueStart = Long.parseLong(parts[0]);
									final long keyStart = Long.parseLong(parts[1]);
									final long keyEnd = keyStart + Long.parseLong(parts[2]) - 1;
									return new RangeEntry(keyStart, keyEnd, valueStart);
								}) //
								.toList();
						return new AlmanacMap(source, target, new RangeMap(rangeEntries));
					}) //
					.collect(Collectors.toMap(AlmanacMap::source, Function.identity(), (m1, m2) -> m1, LinkedHashMap::new));

			return new Almanac(seeds, Collections.unmodifiableMap(almanacMaps));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findMinLocation()
	{
		final LongUnaryOperator seedToLocation = computeSeedToLocationFunction();

		return Arrays.stream(seeds) //
				.map(seedToLocation) //
				.min() //
				.orElseThrow();
	}

	public long findMinLocationByRanges()
	{
		final var ranges = new ArrayList<Range>();
		for (int i = 0; i < seeds.length; i += 2) {
			ranges.add(new Range(seeds[i], seeds[i] + seeds[i + 1] - 1));
		}

		final Function<List<Range>, List<Range>> rangeMapperFunction = computeSeedToLocationByRangesFunction();

		return ranges.stream() //
				.map(List::of) //
				.map(rangeMapperFunction) //
				.mapToLong(mappedRanges -> mappedRanges.stream() //
						.mapToLong(Range::min) //
						.min() //
						.orElseThrow()) //
				.min() //
				.orElseThrow();
	}

	private List<Range> mapRanges(final List<Range> ranges, final AlmanacMap almanacMap)
	{
		final List<Range> mapped = new ArrayList<>();
		for (final Range range : ranges) {
			long start = range.min();
			while (start < range.max()) {
				for (int i = 0; i < almanacMap.mapping().entries().size() && start < range.max(); i++) {
					final RangeEntry rangeEntry = almanacMap.mapping().entries().get(i);
					if (rangeEntry.contains(start)) {
						final long end = Math.min(rangeEntry.keyEnd(), range.max());
						mapped.add(new Range(rangeEntry.map(start), rangeEntry.map(end)));
						start = end + 1;
					}
				}
			}
		}

		return List.copyOf(mapped);
	}

	private Map<String, AlmanacMap> getExpandedAlmanacMaps()
	{
		return almanacMaps.values().stream() //
				.map(almanacMap -> {
					final List<RangeEntry> sortedRangeEntries = almanacMap.mapping().entries().stream() //
							.sorted(Comparator.comparingLong(RangeEntry::keyStart)) //
							.toList();
					final List<RangeEntry> ranges = new ArrayList<>();
					for (int i = 0; i < sortedRangeEntries.size() - 1; i++) {
						final var current = sortedRangeEntries.get(i);
						final var next = sortedRangeEntries.get(i + 1);
						ranges.add(current);
						if (current.keyEnd() + 1 != next.keyStart()) {
							ranges.add(new RangeEntry(current.keyEnd() + 1, next.keyStart() - 1, current.keyEnd() + 1));
						}
					}
					if (ranges.getFirst().keyStart() != 0L) {
						ranges.add(0, new RangeEntry(0L, ranges.getFirst().keyStart() - 1, 0));
					}
					ranges.add(sortedRangeEntries.getLast());
					ranges.add(new RangeEntry(sortedRangeEntries.getLast().keyEnd() + 1, Long.MAX_VALUE,
							sortedRangeEntries.getLast().keyEnd() + 1));
					return almanacMap.withMapping(new RangeMap(List.copyOf(ranges)));
				}) //
				.collect(Collectors.toMap(AlmanacMap::source, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
	}

	private @NonNull LongUnaryOperator computeSeedToLocationFunction()
	{
		LongUnaryOperator seedToLocation = null;
		String source = "seed";
		do {
			final var almanacMap = almanacMaps.get(source);
			if (almanacMap == null) {
				return Objects.requireNonNull(seedToLocation);
			}
			final LongUnaryOperator mapping = almanacMap.mapping()::get;
			seedToLocation = (seedToLocation == null) ? mapping : seedToLocation.andThen(mapping);
			source = almanacMap.target();
		} while (true);
	}

	private @NonNull Function<List<Range>, List<Range>> computeSeedToLocationByRangesFunction()
	{
		final Map<String, AlmanacMap> expandedAlmanacMaps = getExpandedAlmanacMaps();

		Function<List<Range>, List<Range>> seedToLocation = null;
		String source = "seed";
		do {
			final var almanacMap = expandedAlmanacMaps.get(source);
			if (almanacMap == null) {
				return Objects.requireNonNull(seedToLocation);
			}
			final Function<List<Range>, List<Range>> mapping = ranges -> mapRanges(ranges, almanacMap);
			seedToLocation = (seedToLocation == null) ? mapping : seedToLocation.andThen(mapping);
			source = almanacMap.target();
		} while (true);
	}

	public record RangeEntry(long keyStart, long keyEnd, long valueStart)
	{
		public boolean contains(final long value)
		{
			return keyStart <= value && keyEnd >= value;
		}

		public long map(final long value)
		{
			return valueStart + (value - keyStart);
		}
	}

	public record RangeMap(List<RangeEntry> entries)
	{
		public long get(final long key)
		{
			for (final RangeEntry rangeEntry : entries) {
				if (rangeEntry.contains(key)) {
					return rangeEntry.map(key);
				}
			}
			return key;
		}
	}

	@RecordBuilder
	public record AlmanacMap(String source, String target, RangeMap mapping) implements AlmanacAlmanacMapBuilder.With {}

	private record Range(long min, long max) {}
}
