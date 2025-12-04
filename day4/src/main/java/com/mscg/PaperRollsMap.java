package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.BitSet;
import java.util.List;

public record PaperRollsMap(BitSet rolls, int rows, int cols)
{

	public static PaperRollsMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.toList();
			final int rows = allLines.size();
			final int cols = allLines.getFirst().length();
			final BitSet rolls = new BitSet(rows * cols);
			for (int x = 0; x < rows; x++) {
				final String line = allLines.get(x);
				for (int y = 0; y < cols; y++) {
					if (line.charAt(y) == '@') {
						rolls.set(positionToIndex(x, y, cols));
					}
				}
			}

			return new PaperRollsMap(rolls, rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countMovableRolls()
	{
		return rolls.stream() //
				.filter(pos -> countExistingAdjacents(pos, rolls, rows, cols) < 4) //
				.count();
	}

	public long countAllMovableRolls()
	{
		var currentRolls = rolls;
		while (true) {
			final var nextRolls = new BitSet(rows * cols);
			nextRolls.or(currentRolls);

			for (int i = currentRolls.nextSetBit(0); i >= 0; i = currentRolls.nextSetBit(i + 1)) {
				final long existingAdjacents = countExistingAdjacents(i, currentRolls, rows, cols);
				if (existingAdjacents < 4) {
					nextRolls.set(i, false);
				}
			}

			final int removedItems = currentRolls.cardinality() - nextRolls.cardinality();
			if (removedItems == 0) {
				break;
			}

			currentRolls = nextRolls;
		}
		return (long) rolls.cardinality() - currentRolls.cardinality();
	}

	private static long countExistingAdjacents(final int pos, final BitSet currentRolls, final int rows, final int cols)
	{
		final int[] adjacents = buildAdjacents(pos, rows, cols);
		long existingAdjacents = 0;
		for (int i = 0; i < adjacents.length && adjacents[i] >= 0; i++) {
			final int adj = adjacents[i];
			if (currentRolls.get(adj)) {
				existingAdjacents++;
			}
		}
		return existingAdjacents;
	}

	private static int positionToIndex(final int x, final int y, final int cols)
	{
		return x * cols + y;
	}

	private static int[] buildAdjacents(final int index, final int rows, final int cols)
	{
		final int x = index / cols;
		final int y = index % cols;
		final var adjacents = new int[] { -1, -1, -1, -1, -1, -1, -1, -1 };
		int idx = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx != 0 || dy != 0) {
					final int newX = x + dx;
					final int newY = y + dy;
					if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
						adjacents[idx++] = positionToIndex(newX, newY, cols);
					}
				}
			}
		}
		return adjacents;
	}

}
