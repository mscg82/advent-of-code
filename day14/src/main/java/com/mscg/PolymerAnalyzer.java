package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public record PolymerAnalyzer(String initialPolymer, Map<Pair, Character> substitutions)
{

	public static Map<Pair, Long> expand(final Map<Pair, Long> pairsInPolymerToFrequency,
			final Map<Pair, List<Pair>> pairToSubstituted)
	{
		final Map<Pair, Long> newFrequencies = new HashMap<>();

		pairsInPolymerToFrequency.forEach((pair, value) -> {
			final List<Pair> replaced = pairToSubstituted.get(pair);
			if (replaced != null) {
				newFrequencies.merge(replaced.get(0), value, Long::sum);
				newFrequencies.merge(replaced.get(1), value, Long::sum);
			}
		});
		return newFrequencies;
	}

	public static PolymerAnalyzer parseInput(final BufferedReader in) throws IOException
	{
		final String initialPolymer = in.readLine();
		final Map<Pair, Character> substitutions = in.lines() //
				.skip(1) // ignore empty line
				.map(line -> line.split(" -> ")) //
				.map(parts -> Map.entry(new Pair(parts[0].charAt(0), parts[0].charAt(1)), parts[1].charAt(0))) //
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

		return new PolymerAnalyzer(initialPolymer, substitutions);
	}

	public long expandAndAnalyze(final int steps)
	{
		final Map<Pair, List<Pair>> pairToSubstituted = substitutions.entrySet().stream() //
				.map(entry -> {
					final Pair pair = entry.getKey();
					final char replace = entry.getValue();
					return Map.entry(pair, List.of(new Pair(pair.el1(), replace), new Pair(replace, pair.el2())));
				}) //
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

		final List<Pair> pairsInPolymer = new ArrayList<>();
		for (int i = 0, bound = initialPolymer.length() - 1; i < bound; i++) {
			pairsInPolymer.add(new Pair(initialPolymer.charAt(i), initialPolymer.charAt(i + 1)));
		}

		Map<Pair, Long> finalPolymerPairsFrequency = pairsInPolymer.stream() //
				.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		for (int i = 0; i < steps; i++) {
			finalPolymerPairsFrequency = expand(finalPolymerPairsFrequency, pairToSubstituted);
		}
		final Map<Character, Long> charToFrequency = new HashMap<>();
		finalPolymerPairsFrequency.forEach((pair, freq) -> {
			charToFrequency.merge(pair.el1(), freq, Long::sum);
			charToFrequency.merge(pair.el2(), freq, Long::sum);
		});
		charToFrequency.replaceAll((c, f) -> {
			final char ch = c;
			if (ch == initialPolymer.charAt(0) || ch == initialPolymer.charAt(initialPolymer.length() - 1)) {
				return (f + 1) / 2;
			} else {
				return f / 2;
			}
		});

		final LongSummaryStatistics frequencyStats = charToFrequency.values().stream() //
				.mapToLong(v -> v) //
				.summaryStatistics();

		return frequencyStats.getMax() - frequencyStats.getMin();
	}

	public record Pair(char el1, char el2)
	{

	}

}
