package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public record RucksackOrganizer(List<Rucksack> rucksacks)
{
	public static RucksackOrganizer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Rucksack> rucksacks = in.lines() //
					.map(Rucksack::from) //
					.toList();
			return new RucksackOrganizer(rucksacks);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumAllDuplicatePriorities()
	{
		return rucksacks.stream() //
				.mapToLong(Rucksack::sumDuplicatedPriorities) //
				.sum();
	}

	public long sumSecurityBadges()
	{
		return StreamUtils.partitioned(rucksacks, 3) //
				.map(window -> window.stream() //
						.map(Rucksack::asList) //
						.reduce(PrioList::intersect) //
						.orElseThrow()) //
				.mapToLong(intersection -> intersection.stream() //
						.distinct() //
						.sum()) //
				.sum();
	}

	public static final class PrioList
	{
		private final int[] priorities;

		public static PrioList from(final String line)
		{
			return new PrioList( //
					line.chars() //
							.map(c -> {
								if (Character.isLowerCase(c)) {
									return c - 'a' + 1;
								} else {
									return c - 'A' + 27;
								}
							}) //
							.toArray() //
			);
		}

		private PrioList(final int[] priorities)
		{
			this.priorities = priorities;
			Arrays.sort(this.priorities);
		}

		public boolean contains(final int priority)
		{
			return Arrays.binarySearch(priorities, priority) >= 0;
		}

		public PrioList joinTo(final PrioList other)
		{
			final int[] mergedPriorities = new int[this.priorities.length + other.priorities.length];
			System.arraycopy(this.priorities, 0, mergedPriorities, 0, this.priorities.length);
			System.arraycopy(other.priorities, 0, mergedPriorities, this.priorities.length, other.priorities.length);
			return new PrioList(mergedPriorities);
		}

		public IntStream intersection(final IntStream other)
		{
			return other.filter(this::contains);
		}

		public PrioList intersect(final PrioList other)
		{
			return new PrioList(this.intersection(other.stream()).toArray());
		}

		public IntStream stream()
		{
			return Arrays.stream(priorities);
		}

		@Override
		public String toString()
		{
			return Arrays.toString(priorities);
		}
	}

	public record Rucksack(PrioList left, PrioList right)
	{
		public static Rucksack from(final String line)
		{
			final int midIndex = line.length() / 2;
			return new Rucksack( //
					PrioList.from(line.substring(0, midIndex)), //
					PrioList.from(line.substring(midIndex)));
		}

		public long sumDuplicatedPriorities()
		{
			return left.intersection(right.stream()) //
					.distinct() //
					.sum();
		}

		public PrioList asList()
		{
			return left.joinTo(right);
		}
	}

}
