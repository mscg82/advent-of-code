package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record BatteryArray(List<BatteryBank> arrays)
{

	public static BatteryArray parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<BatteryBank> arrays = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.map(BatteryBank::from) //
					.toList();
			return new BatteryArray(arrays);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeTotalJoltage()
	{
		return arrays.stream() //
				.mapToLong(BatteryBank::computeMaxJoltage) //
				.sum();
	}

	public long computeTotalOverrideJoltage()
	{
		return arrays.stream() //
				.mapToLong(BatteryBank::computeOverrideJoltage) //
				.sum();
	}

	public record BatteryBank(IntList values)
	{

		public static BatteryBank from(final String line)
		{
			final IntList values = line.chars() //
					.map(c -> (char) c - '0') //
					.collect(IntArrayList::new, IntArrayList::add, IntArrayList::addAll);
			return new BatteryBank(new IntImmutableList(values));
		}

		public long computeMaxJoltage()
		{
			return computeJoltageWithDigits(2);
		}

		public long computeOverrideJoltage()
		{
			return computeJoltageWithDigits(12);
		}

		private long computeJoltageWithDigits(final int digits)
		{
			long joltage = 0;
			int idx = -1;
			for (int d = digits - 1; d >= 0; d--) {
				int maxDigit = 0;
				for (int i = idx + 1, max = values.size() - d; i < max; i++) {
					if (values.getInt(i) > maxDigit) {
						maxDigit = values.getInt(i);
						idx = i;
					}
				}
				joltage = joltage * 10 + maxDigit;
			}
			return joltage;
		}

	}

}
