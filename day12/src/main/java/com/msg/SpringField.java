package com.msg;

import com.mscg.utils.CollectionUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mscg.utils.CollectionUtils.append;
import static com.mscg.utils.CollectionUtils.tail;
import static com.mscg.utils.StringTemplates.ILLEGAL_ARGUMENT_EXC;

public record SpringField(List<SpringRow> springRows)
{
	public static SpringField parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<SpringRow> springRows = in.lines() //
					.map(SpringField::parseSpringRow) //
					.toList();
			return new SpringField(springRows);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public static SpringRow expandSpringRow(final SpringRow springRow)
	{
		final List<SpringType> expandedTypes = new ArrayList<>(springRow.types().size() * 5 + 4);
		final IntList expandedDamagedBlocks = new IntArrayList(springRow.damagedBlocks().size() * 5);
		for (int i = 0; i < 5; i++) {
			if (i != 0) {
				expandedTypes.add(SpringType.UNKNOWN);
			}
			expandedTypes.addAll(springRow.types());
			expandedDamagedBlocks.addAll(springRow.damagedBlocks());
		}
		return new SpringRow(expandedTypes, expandedDamagedBlocks);
	}

	public static SpringRow parseSpringRow(final String line)
	{
		final String[] parts = line.split(" ");
		final List<SpringType> types = parts[0].codePoints() //
				.mapToObj(cp -> SpringType.from((char) cp)) //
				.toList();
		final IntList damagedBlocks = new IntImmutableList(Arrays.stream(parts[1].split(",")) //
				.mapToInt(Integer::parseInt) //
				.toArray());
		return new SpringRow(types, damagedBlocks);
	}

	public long sumAllArrangements()
	{
		return springRows.stream() //
				.mapToLong(this::countArrangements) //
				.sum();
	}

	public long sumAllUnfoldedArrangements()
	{
		final List<SpringRow> expandedSpringRows = springRows.stream() //
				.map(SpringField::expandSpringRow) //
				.toList();

		return expandedSpringRows.stream() //
				.parallel() //
				.mapToLong(this::countArrangements) //
				.sum();
	}

	private long countArrangements(final SpringRow springRow)
	{
		final List<List<SpringType>> validArrangements = new ArrayList<>();

		final var queue = new ArrayDeque<Status>();
		queue.addFirst(new Status(springRow, 0, false, List.of()));

		while (!queue.isEmpty()) {
			final var current = queue.pop();
			final var currentRow = current.springRow();

			final SpringType type = currentRow.types().isEmpty() ? SpringType.END : currentRow.types().getFirst();
			switch (type) {
				case END -> {
					if (currentRow.damagedBlocks().size() >= 2 || //
							(!currentRow.damagedBlocks().isEmpty() && current.damagedCount() != currentRow.damagedBlocks()
									.getInt(0))) {
						// invalid arrangement, discard
						continue;
					}

					// valid arrangement, count it
					validArrangements.add(current.processed());
				}

				case OPERATIONAL -> {
					if (current.countingDamaged()) {
						if (currentRow.damagedBlocks().isEmpty()) {
							// invalid arrangement, discard
							continue;
						}
						final int expectedDamaged = currentRow.damagedBlocks().getInt(0);

						if (current.damagedCount() < expectedDamaged) {
							// invalid arrangement, discard
							continue;
						}
						if (expectedDamaged == current.damagedCount()) {
							if (currentRow.types().isEmpty()) {
								// valid arrangement, count and continue;
								validArrangements.add(current.processed());
								continue;
							}
							queue.addFirst(current.with(status -> {
								final SpringRow newSpringRow = status.springRow().with(row -> {
									row.damagedBlocks(tail(row.damagedBlocks()));
									row.types(tail(row.types()));
								});
								status.springRow(newSpringRow);
								status.damagedCount(0);
								status.countingDamaged(false);
								status.processed(append(status.processed(), type));
							}));
						}
					} else {
						queue.addFirst(current.with(status -> {
							status.springRow(status.springRow().withTypes(tail(status.springRow().types())));
							status.processed(append(status.processed(), type));
						}));
					}
				}

				case DAMAGED -> {
					if (currentRow.damagedBlocks().isEmpty()) {
						// invalid arrangement, discard
						continue;
					}
					final int expectedDamaged = currentRow.damagedBlocks().getInt(0);

					final int newDamaged = current.damagedCount() + 1;
					if (newDamaged > expectedDamaged) {
						// invalid arrangement, discard
						continue;
					}
					queue.addFirst(current.with(status -> {
						status.countingDamaged(true);
						status.springRow(status.springRow().withTypes(tail(status.springRow().types())));
						status.processed(append(status.processed(), type));
						status.damagedCount(newDamaged);
					}));
				}

				case UNKNOWN -> {
					queue.addFirst(current.withSpringRow(
							currentRow.withTypes(CollectionUtils.replaceHead(SpringType.DAMAGED, currentRow.types()))));
					queue.addFirst(current.withSpringRow(
							currentRow.withTypes(CollectionUtils.replaceHead(SpringType.OPERATIONAL, currentRow.types()))));
				}
			}
		}

		return validArrangements.size();
	}

	@RecordBuilder
	public record SpringRow(List<SpringType> types, IntList damagedBlocks) implements SpringFieldSpringRowBuilder.With
	{
		@Override
		public String toString()
		{
			final var typesStr = types.stream() //
					.map(type -> switch (type) {
						case OPERATIONAL -> ".";
						case DAMAGED -> "#";
						case UNKNOWN -> "?";
						case END -> "";
					}) //
					.collect(Collectors.joining());
			final var damagedStr = damagedBlocks.intStream() //
					.mapToObj(Integer::toString) //
					.collect(Collectors.joining(","));
			return STR."\{typesStr} \{damagedStr}";
		}
	}

	@RecordBuilder
	protected record Status(SpringRow springRow, int damagedCount, boolean countingDamaged, List<SpringType> processed)
			implements SpringFieldStatusBuilder.With {}

	public enum SpringType
	{
		OPERATIONAL, DAMAGED, UNKNOWN, END;

		public static SpringType from(final char c)
		{
			return switch (c) {
				case '.' -> OPERATIONAL;
				case '#' -> DAMAGED;
				case '?' -> UNKNOWN;
				default -> throw ILLEGAL_ARGUMENT_EXC."Unsupported sprng type '\{c}'";
			};
		}
	}

}
