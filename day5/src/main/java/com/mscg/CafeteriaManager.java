package com.mscg;

import com.mscg.utils.InclusiveRange;
import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Stream;

public record CafeteriaManager(List<InclusiveRange> freshRanges, LongList ingredients)
{

	public static CafeteriaManager parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> blocks = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.toList();

			final List<InclusiveRange> freshRanges = blocks.getFirst().stream() //
					.map(InclusiveRange::from) //
					.toList();

			final LongList ingredients = blocks.getLast().stream() //
					.mapToLong(Long::parseLong) //
					.collect(LongArrayList::new, LongList::add, LongList::addAll);

			return new CafeteriaManager(freshRanges, ingredients);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countFreshIngredients()
	{
		return ingredients.longStream() //
				.filter(ingredient -> freshRanges.stream().anyMatch(range -> range.contains(ingredient))) //
				.count();
	}

	public long countAllFreshIngredients()
	{
		final var nonOverlappingRanges = InclusiveRange.mergeIntoNonOverlappingRanges(freshRanges);

		return nonOverlappingRanges.stream() //
				.mapToLong(InclusiveRange::size) //
				.sum();
	}

}
