package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

public record IdValidator(List<Span> spans)
{

	public static IdValidator parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Span> spans = Arrays.stream(in.readLine().trim().split(",")) //
					.map(str -> {
						final var parts = str.split("-");
						return new Span(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
					}) //
					.toList();

			return new IdValidator(spans);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumInvalidIdsRepeatedTwice()
	{
		return spans.stream() //
				.flatMapToLong(span -> LongStream.rangeClosed(span.start(), span.end())) //
				.parallel() //
				.filter(IdValidator::isRepeatedTwice) //
				.sum();
	}

	public long sumInvalidIdsRepeatedMultipleTimes()
	{
		return spans.stream() //
				.flatMapToLong(span -> LongStream.rangeClosed(span.start(), span.end())) //
				.parallel() //
				.filter(IdValidator::isRepeatedMultipleTimes) //
				.sum();
	}

	private static int countDecimalDigits(final long value)
	{
		int result = 0;
		long current = value;
		while (current > 0) {
			current /= 10;
			result++;
		}
		return result;
	}

	private static boolean isRepeatedTwice(final long value)
	{
		final int digits = countDecimalDigits(value);
		if (digits % 2 != 0) {
			return false;
		}
		long part1 = 0;
		long part2 = 0;
		long current = value;
		for (int i = 0; i < digits / 2 && current != 0; i++) {
			part1 = part1 * 10 + current % 10;
			current /= 10;
		}
		for (int i = 0; i < digits / 2 && current != 0; i++) {
			part2 = part2 * 10 + current % 10;
			current /= 10;
		}

		return part1 == part2;
	}

	private static boolean isRepeatedMultipleTimes(final long value)
	{
		final int digits = countDecimalDigits(value);
		final var valueAsDigits = new int[digits];
		long current = value;
		for (int i = 0; i < digits; i++) {
			valueAsDigits[digits - 1 - i] = (int) (current % 10);
			current /= 10;
		}
		for (int numBlocks = 2; numBlocks <= digits; numBlocks++) {
			if (digits % numBlocks == 0) {
				final boolean repeated = isValueRepeatedInBlocks(valueAsDigits, numBlocks);
				if (repeated) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("java:S1119")
	private static boolean isValueRepeatedInBlocks(final int[] valueAsDigits, final int numBlocks)
	{
		final int digits = valueAsDigits.length;
		final int interval = digits / numBlocks;
		boolean repeated = true;
		outerLoop:
		for (int i = 0; i < interval; i++) {
			for (int j = 1; j < numBlocks; j++) {
				if (valueAsDigits[i] != valueAsDigits[i + interval * j]) {
					repeated = false;
					break outerLoop;
				}
			}
		}
		return repeated;
	}

	public record Span(long start, long end) {}

}
