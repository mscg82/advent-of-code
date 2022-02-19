package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.eclipse.collections.impl.factory.primitive.LongSets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

@RecordBuilder
public record BugArea(BitSet bugs, int rows, int cols) implements BugAreaBuilder.With
{
	public static BugArea parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int[] chars = allLines.stream() //
					.flatMapToInt(String::chars) //
					.toArray();
			final var bugs = new BitSet();
			for (int i = 0; i < chars.length; i++) {
				if ((char) chars[i] == '#') {
					bugs.set(i);
				}
			}

			return new BugArea(bugs, allLines.size(), allLines.get(0).length());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long getBiodiversity()
	{
		return bugs.toLongArray()[0];
	}

	public BugArea evolveUntilFirstRepetition()
	{
		final var seenPatterns = LongSets.mutable.of(getBiodiversity());
		BugArea current = this;
		while (true) {
			final var newBugs = new BitSet();
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					final var currentArea = current;
					final long bugNeighbours = Position.neighbours(x, y) //
							.filter(pos -> currentArea.getBug(pos.x(), pos.y())) //
							.count();

					final boolean currentBugPresent = current.getBug(x, y);
					if (bugNeighbours == 1 || (!currentBugPresent && bugNeighbours == 2)) {
						newBugs.set(getIndex(x, y));
					}
				}
			}
			current = current.withBugs(newBugs);
			final long pattern = current.getBiodiversity();
			if (seenPatterns.contains(pattern)) {
				return current;
			}
			seenPatterns.add(pattern);
		}
	}

	private boolean getBug(final int x, final int y)
	{
		if (x < 0 || x >= cols || y < 0 || y >= rows) {
			return false;
		}
		return bugs.get(getIndex(x, y));
	}

	private int getIndex(final int x, final int y)
	{
		return y * cols + x;
	}

	private record Position(int x, int y)
	{

		public static Stream<Position> neighbours(final int x, final int y)
		{
			return Stream.of( //
					new Position(x - 1, y), //
					new Position(x, y - 1), //
					new Position(x + 1, y), //
					new Position(x, y + 1));
		}

		public Stream<Position> neighbours()
		{
			return neighbours(x, y);
		}

	}
}
