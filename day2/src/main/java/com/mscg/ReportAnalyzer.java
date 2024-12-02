package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record ReportAnalyzer(List<List<Integer>> readings)
{
	public static ReportAnalyzer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<Integer>> readings = in.lines() //
					.map(line -> Arrays.stream(line.split("\\s+")) //
							.map(Integer::parseInt) //
							.toList()) //
					.toList();
			return new ReportAnalyzer(readings);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countSafeReadings()
	{
		return readings.stream() //
				.filter(ReportAnalyzer::isLineSafe) //
				.count();
	}

	public long countSafeReadingsUsingDampener()
	{
		return readings.stream() //
				.filter(line -> {
					if (isLineSafe(line)) {
						return true;
					}
					for (int i = 0, l = line.size(); i < l; i++) {
						final var shortLine = new ArrayList<Integer>(l - 1);
						shortLine.addAll(line.subList(0, i));
						shortLine.addAll(line.subList(i + 1, l));
						if (isLineSafe(shortLine)) {
							return true;
						}
					}
					return false;
				}) //
				.count();
	}

	private static boolean isLineSafe(final List<Integer> line)
	{
		final boolean increasing = line.get(0) < line.get(1);
		for (int i = 0, l = line.size(); i < l - 1; i++) {
			final int v1 = line.get(i);
			final int v2 = line.get(i + 1);
			final int diff = v2 - v1;
			if (diff == 0) {
				return false;
			}
			if (increasing && diff < 0) {
				return false;
			}
			if (!increasing && diff > 0) {
				return false;
			}
			if (Math.abs(diff) > 3) {
				return false;
			}
		}
		return true;
	}
}
