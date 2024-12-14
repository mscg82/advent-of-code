package com.mscg;

import com.mscg.utils.Position8Bits;
import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record ElfBathrooms(List<Guard> guards)
{

	public static ElfBathrooms parseInput(final BufferedReader in) throws IOException
	{
		final var pattern = Pattern.compile("^p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)$");
		try {
			final List<Guard> guards = in.lines() //
					.map(StreamUtils.matchOrFail(pattern, line -> "Invalid line format: " + line)) //
					.map(matcher -> new Guard( //
							new Position8Bits(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))), //
							Integer.parseInt(matcher.group(3)), //
							Integer.parseInt(matcher.group(4)))) //
					.toList();
			return new ElfBathrooms(guards);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeSecurityFactorAfter100Steps(final int rows, final int cols)
	{
		final var evolvedBathrooms = evolve(100, rows, cols);
		final int splitX = cols / 2;
		final int splitY = rows / 2;

		final Map<Boolean, List<Guard>> splitOnCols = evolvedBathrooms.guards().stream() //
				.filter(guard -> guard.position().x() != splitX) //
				.collect(Collectors.partitioningBy(guard -> guard.position().x() < splitX));

		return splitOnCols.values().stream() //
				.flatMapToLong(verticalSplit -> {
					final Map<Boolean, Long> splitOnRows = verticalSplit.stream() //
							.filter(guard -> guard.position().y() != splitY) //
							.collect(Collectors.partitioningBy(guard -> guard.position().y() < splitY, Collectors.counting()));
					return splitOnRows.values().stream() //
							.mapToLong(Long::longValue);
				}) //
				.reduce(1L, (a, b) -> a * b);
	}

	public long computeTimeToEasterEgg(final int rows, final int cols)
	{
		var evolvedBathrooms = this;
		for (long t = 1; t < 1_000_000; t++) {
			evolvedBathrooms = evolvedBathrooms.evolve(1, rows, cols);

			// search for the tree pattern
			final List<BitSet> occupiedPositions = new ArrayList<>(rows);
			for (int y = 0; y < rows; y++) {
				occupiedPositions.add(new BitSet());
			}
			for (final Guard guard : evolvedBathrooms.guards()) {
				final var position = guard.position();
				occupiedPositions.get(position.y()).set(position.x());
			}

			if (mayBeATree(occupiedPositions, rows, cols)) {
				return t;
			}
		}
		return -1;
	}

	ElfBathrooms evolve(final long steps, final int rows, final int cols)
	{
		final var newGuards = new ArrayList<Guard>(guards.size());
		for (final Guard guard1 : guards) {
			final int newX = Math.floorMod(guard1.position.x() + (guard1.vx() * steps), cols);
			final int newY = Math.floorMod(guard1.position.y() + (guard1.vy() * steps), rows);
			final Guard apply = guard1.withPosition(new Position8Bits(newX, newY));
			newGuards.add(apply);
		}
		return new ElfBathrooms(List.copyOf(newGuards));
	}

	@SuppressWarnings("SameParameterValue")
	String toVisualizationString(final int rows, final int cols)
	{
		final Map<Position8Bits, Long> positionsToCount = guards.stream() //
				.collect(Collectors.groupingBy(Guard::position, Collectors.counting()));
		return IntStream.range(0, rows) //
				.mapToObj(y -> IntStream.range(0, cols) //
						.mapToObj(x -> new Position8Bits(x, y)) //
						.map(p -> switch (positionsToCount.get(p)) {
							case null -> ".";
							case final long l -> String.valueOf(l);
						}) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	private static boolean mayBeATree(final List<BitSet> occupiedPositions, final int rows, final int cols)
	{
		// keep 10 rows and columns of buffer
		for (int y = 0; y < rows - 10; y++) {
			final BitSet row = occupiedPositions.get(y);
			for (int x = 10; x < cols - 10; x++) {
				if (row.get(x)) {
					// check that the next rows has 3, 5 and 7 occupied positions in a row, respectively
					if (checkAroundPosition(occupiedPositions.get(y + 1), x, 3) && //
							checkAroundPosition(occupiedPositions.get(y + 2), x, 5) && //
							checkAroundPosition(occupiedPositions.get(y + 3), x, 7)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private static boolean checkAroundPosition(final BitSet row, final int position, final int size)
	{
		for (int i = position - size / 2; i < position + size / 2; i++) {
			if (!row.get(i)) {
				return false;
			}
		}
		return true;
	}

	@RecordBuilder
	public record Guard(Position8Bits position, int vx, int vy) implements ElfBathroomsGuardBuilder.With {}

}
