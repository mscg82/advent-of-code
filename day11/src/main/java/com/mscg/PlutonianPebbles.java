package com.mscg;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongImmutableList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public record PlutonianPebbles(LongList pebbles)
{
	public static PlutonianPebbles parseInput(final BufferedReader in) throws IOException
	{
		final var pebbles = LongImmutableList.toList(Arrays.stream(in.readLine().split("\\s+")) //
				.mapToLong(Long::parseLong));
		return new PlutonianPebbles(pebbles);
	}

	public long countPebblesAfterIterations(final int iterations)
	{
		final var cache = new Long2ObjectOpenHashMap<LongList>();
		long pebblesCount = 0;
		for (int i = 0, l = pebbles.size(); i < l; i++) {
			var pebbles = new Long2LongOpenHashMap();
			pebbles.put(this.pebbles.getLong(i), 1L);
			for (int iter = 0; iter < iterations; iter++) {
				final var newPebbles = new Long2LongOpenHashMap();
				for (final Long2LongMap.Entry entry : pebbles.long2LongEntrySet()) {
					final long pebble = entry.getLongKey();
					final long count = entry.getLongValue();
					final LongList splitPebble = cache.computeIfAbsent(pebble, PlutonianPebbles::splitPebble);
					for (int j = 0, ls = splitPebble.size(); j < ls; j++) {
						newPebbles.merge(splitPebble.getLong(j), count, Long::sum);
					}
				}
				pebbles = newPebbles;
			}
			pebblesCount += pebbles.values().longStream().sum();
		}
		return pebblesCount;
	}

	private static LongList splitPebble(final long pebble)
	{
		if (pebble == 0) {
			return LongImmutableList.of(1);
		}

		final int digitsCount = (int) (Math.log10(pebble) + 1);
		if (digitsCount % 2 == 0) {
			long left = pebble;
			long right = 0;
			long multiplier = 1;
			for (int i = 0, half = digitsCount / 2; i < half; i++) {
				right = (left % 10) * multiplier + right;
				left /= 10;
				multiplier *= 10;
			}
			return LongImmutableList.of(left, right);
		}

		return LongImmutableList.of(pebble * 2024);
	}
}
