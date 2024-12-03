package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record ShopComputer(List<String> memory)
{
	public static ShopComputer parseInput(final BufferedReader in) throws IOException
	{
		try {
			return new ShopComputer(in.lines().toList());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumMultiplicationResults()
	{
		return sumMultiplicationResultsOnList(memory);
	}

	public long sumEnabledMultiplicationResults()
	{
		final String joinedMemory = String.join("", memory);

		final var commandPattern = Pattern.compile("(do(n't)?\\(\\))");
		final String[] splittedMemory = commandPattern.splitWithDelimiters(joinedMemory, 0);
		final var validMemoryParts = new ArrayList<String>();
		boolean valid = true;
		for (final String memoryPart : splittedMemory) {
			final boolean currentlyValid = valid;
			switch (memoryPart) {
				case "do()" -> valid = true;
				case "don't()" -> valid = false;
				case final String line when currentlyValid -> validMemoryParts.add(line);
				case final String _ -> { /* do nothing */ }
			}
		}

		return sumMultiplicationResultsOnList(validMemoryParts);
	}

	private static long sumMultiplicationResultsOnList(final List<String> memory)
	{
		final String joinedMemory = String.join("", memory);

		final var pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
		final var matcher = pattern.matcher(joinedMemory);
		long result = 0L;
		while (matcher.find()) {
			final long v1 = Long.parseLong(matcher.group(1));
			final long v2 = Long.parseLong(matcher.group(2));
			result += v1 * v2;
		}
		return result;
	}
}
