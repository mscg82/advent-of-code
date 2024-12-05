package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

public record ManualPagePrinter(Int2ObjectMap<IntList> sortingRules, List<IntList> pagesToPrint)
{
	public static ManualPagePrinter parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> blocks = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.toList();
			final Int2ObjectMap<IntList> sortingRules = blocks.get(0).stream() //
					.map(line -> line.split("\\|")) //
					.map(parts -> IntIntPair.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))) //
					.collect(Collector.<IntIntPair, Int2ObjectMap<IntList>, Int2ObjectMap<IntList>>of( //
							Int2ObjectOpenHashMap::new, //
							(map, pair) -> map.computeIfAbsent(pair.leftInt(), _ -> new IntArrayList()).add(pair.rightInt()), //
							StreamUtils.unsupportedMerger(), //
							map -> {
								map.replaceAll((_, list) -> IntImmutableList.of(list.toIntArray()));
								return Int2ObjectMaps.unmodifiable(map);
							}));

			final List<IntList> pagesToPrint = blocks.get(1).stream() //
					.map(line -> IntImmutableList.toList(Arrays.stream(line.split(",")) //
							.mapToInt(Integer::parseInt))) //
					.map(IntList.class::cast) //
					.toList();

			return new ManualPagePrinter(sortingRules, pagesToPrint);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumMidValuesOfValidLines()
	{
		return pagesToPrint.stream() //
				.filter(this::isValidLine) //
				.mapToLong(line -> line.getInt(line.size() / 2)) //
				.sum();
	}

	public long sumMidValuesOfFixedLines()
	{
		return pagesToPrint.stream() //
				.map(IntArrayList::new) //
				.flatMap(line -> fixInvalidLine(line).stream()) //
				.mapToLong(line -> line.getInt(line.size() / 2)) //
				.sum();
	}

	private boolean isValidLine(final IntList line)
	{
		for (int i = 0, l = line.size(); i < l; i++) {
			final int value = line.getInt(i);
			final IntList following = sortingRules.get(value);
			for (int j = 0, l2 = following.size(); j < l2; j++) {
				final int followingValue = following.getInt(j);
				final int followingPosition = line.indexOf(followingValue);
				if (followingPosition >= 0 && followingPosition < i) {
					return false;
				}
			}
		}
		return true;
	}

	private Optional<IntList> fixInvalidLine(final IntList line)
	{
		for (int i = 0, l = line.size(); i < l; i++) {
			final int value = line.getInt(i);
			final IntList following = sortingRules.get(value);
			for (int j = 0, l2 = following.size(); j < l2; j++) {
				final int followingValue = following.getInt(j);
				final int followingPosition = line.indexOf(followingValue);
				if (followingPosition >= 0 && followingPosition < i) {
					line.set(i, followingValue);
					line.set(followingPosition, value);
					fixInvalidLine(line);
					return Optional.of(line);
				}
			}
		}
		return Optional.empty();
	}

}
