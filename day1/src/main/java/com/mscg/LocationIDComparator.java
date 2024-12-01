package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record LocationIDComparator(List<Long> list1, List<Long> list2)
{
	public static LocationIDComparator parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var list1 = new ArrayList<Long>();
			final var list2 = new ArrayList<Long>();
			final var pattern = Pattern.compile("(\\d+)\\s+(\\d+)");
			in.lines() //
					.map(StreamUtils.matchOrFail(pattern, input -> "Invalid line \"" + input + "\"")) //
					.forEach(matcher -> {
						list1.add(Long.parseLong(matcher.group(1)));
						list2.add(Long.parseLong(matcher.group(2)));
					});
			return new LocationIDComparator(List.copyOf(list1), List.copyOf(list2));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long compareLists()
	{
		return Seq.seq(list1.stream().sorted()) //
				.zip(list2.stream().sorted()) //
				.mapToLong(pair -> Math.abs(pair.v1() - pair.v2())) //
				.sum();
	}

	public long computeSimilarity()
	{
		final Map<Long, Long> frequencyMap = list2.stream() //
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return list1.stream() //
				.mapToLong(val -> val * frequencyMap.getOrDefault(val, 0L)) //
				.sum();
	}
}
