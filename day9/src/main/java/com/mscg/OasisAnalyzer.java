package com.mscg;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record OasisAnalyzer(List<LongList> readings)
{

	public static OasisAnalyzer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<LongList> readings = in.lines() //
					.map(line -> {
						final long[] sensorReadings = Arrays.stream(line.split(" ")) //
								.filter(not(String::isBlank)) //
								.mapToLong(Long::parseLong) //
								.toArray();
						return LongArrayList.wrap(sensorReadings);
					}) //
					.map(LongList.class::cast) //
					.toList();
			return new OasisAnalyzer(readings);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumExtrapolatedNextValues()
	{
		return readings.stream() //
				.mapToLong(OasisAnalyzer::computeNextValue) //
				.sum();
	}

	public long sumExtrapolatedPreviousValues()
	{
		return readings.stream() //
				.mapToLong(OasisAnalyzer::computePreviousValue) //
				.sum();
	}

	private static void evolveSequence(final LongList reading, final Consumer<LongList> currentListConsumer)
	{
		Stream.iterate(reading, //
						currentList -> currentList.longStream().anyMatch(v -> v != 0), //
						currentList -> {
							final LongList diffList = new LongArrayList(currentList.size() - 1);
							for (int i = 1, l = currentList.size(); i < l; i++) {
								diffList.add(currentList.getLong(i) - currentList.getLong(i - 1));
							}
							return diffList;
						}) //
				.forEach(currentListConsumer);
	}

	private static long computeNextValue(final LongList reading)
	{
		final LongList lastValues = new LongArrayList();
		evolveSequence(reading, currentList -> lastValues.add(currentList.getLong(currentList.size() - 1)));
		return lastValues.longStream().sum();
	}

	private static long computePreviousValue(final LongList reading)
	{
		final LongList firstValues = new LongArrayList();
		evolveSequence(reading, currentList -> firstValues.add(0, currentList.getLong(0)));
		return firstValues.longStream() //
				.reduce(0, (acc, val) -> val - acc);
	}

}
