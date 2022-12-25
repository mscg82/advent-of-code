package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record Snafu(List<String> lines)
{
	public static Snafu parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> lines = in.lines() //
					.toList();
			return new Snafu(lines);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public String sumValues()
	{
		final long sum = lines.stream() //
				.mapToLong(Snafu::snafuToLong) //
				.sum();
		return longToSnafu(sum);
	}

	private static long snafuToLong(final String value)
	{
		long numValue = 0L;
		for (int i = 0, l = value.length(); i < l; i++) {
			final char c = value.charAt(i);
			numValue = numValue * 5 + switch (c) {
				case '=' -> -2;
				case '-' -> -1;
				case '0' -> 0;
				case '1' -> 1;
				case '2' -> 2;
				default -> throw new IllegalArgumentException("Invalid snafu number " + value);
			};
		}
		return numValue;
	}

	private static String longToSnafu(final long value)
	{
		final StringBuilder snafu = new StringBuilder();
		long current = value;
		long carry;
		while (current > 0) {
			final long digit = current % 5;
			snafu.insert(0, switch ((int) digit) {
				case 0 -> '0';
				case 1 -> '1';
				case 2 -> '2';
				case 3 -> '=';
				case 4 -> '-';
				default -> throw new IllegalStateException("Cannot happen");
			});
			carry = switch ((int) digit) {
				case 0, 1, 2 -> 0;
				case 3, 4 -> 1;
				default -> throw new IllegalStateException("Cannot happen");
			};
			current = (current / 5) + carry;
		}
		return snafu.toString();
	}
}
