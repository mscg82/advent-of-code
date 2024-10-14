package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public record ScratchCardGame(List<ScratchCard> cards)
{
	public static ScratchCardGame parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var pattern = Pattern.compile("Card\\s+(\\d+):([^|]+)\\|(.+)");
			final List<ScratchCard> cards = in.lines() //
					.map(StreamUtils.matchOrFail(pattern, input -> "Invalid input line \"" + input + "\"")) //
					.map(matcher -> {
						final int index = Integer.parseInt(matcher.group(1).trim());
						final var winningNumbers = parseIntSet(matcher.group(2).trim());
						final var numbers = parseIntSet(matcher.group(3).trim());
						return new ScratchCard(index, winningNumbers, numbers);
					}) //
					.toList();
			return new ScratchCardGame(cards);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long getTotalPoints()
	{
		return cards.stream() //
				.mapToInt(ScratchCard::getWinningSize) //
				.filter(winningSize -> winningSize != 0) //
				.mapToLong(winningSize -> {
					long value = 1L;
					for (int i = 0, l = winningSize - 1; i < l; i++) {
						value *= 2;
					}
					return value;
				}) //
				.sum();
	}

	public long countTotalCards()
	{
		final long[] cardsForType = new long[cards.size()];
		Arrays.fill(cardsForType, 1L);

		for (int i = 0, l = cards.size(); i < l; i++) {
			final var card = cards.get(i);
			final int winningSize = card.getWinningSize();
			for (int j = i + 1, l2 = Math.min(i + 1 + winningSize, l); j < l2; j++) {
				cardsForType[j] += cardsForType[i];
			}
		}

		return Arrays.stream(cardsForType).sum();
	}

	private static IntSet parseIntSet(final String input)
	{
		return Arrays.stream(input.split(" ")) //
				.filter(not(String::isBlank)) //
				.mapToInt(Integer::parseInt) //
				.collect(IntLinkedOpenHashSet::new, IntLinkedOpenHashSet::add, IntLinkedOpenHashSet::addAll);
	}

	public record ScratchCard(int index, IntSet winningNumbers, IntSet numbers)
	{

		public int getWinningSize()
		{
			final IntSet intersection = new IntLinkedOpenHashSet(numbers);
			intersection.retainAll(winningNumbers);
			return intersection.size();
		}

	}
}
