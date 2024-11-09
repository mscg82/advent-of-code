package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public record RockDish(List<List<CellType>> cells)
{

	public static RockDish parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<CellType>> cells = in.lines() //
					.map(line -> line.chars() //
							.mapToObj(c -> CellType.from((char) c)) //
							.toList()) //
					.toList();
			return new RockDish(cells);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public RockDish tiltNorth()
	{
		final var newCells = cells.stream() //
				.map(ArrayList::new) //
				.collect(Collectors.toCollection(ArrayList::new));

		tiltNorth(newCells);

		return new RockDish(newCells.stream() //
				.map(List::copyOf) //
				.toList());
	}

	public RockDish tiltOneCycle()
	{
		final var newCells = cells.stream() //
				.map(ArrayList::new) //
				.collect(Collectors.toCollection(ArrayList::new));

		tiltNorth(newCells);
		tiltWest(newCells);
		tiltSouth(newCells);
		tiltEast(newCells);

		return new RockDish(newCells.stream() //
				.map(List::copyOf) //
				.toList());
	}

	public RockDish tiltContinuously()
	{
		final var dishToIteration = HashMap.<RockDish, Integer>newHashMap(10_000);
		var currentDish = this;
		while (!dishToIteration.containsKey(currentDish)) {
			dishToIteration.put(currentDish, dishToIteration.size());
			currentDish = currentDish.tiltOneCycle();
		}
		final int loopStartIndex = dishToIteration.get(currentDish);
		final int leadingIterations = loopStartIndex - 1;
		final int loopLength = dishToIteration.size() - loopStartIndex;
		final int remainingIterations = (1_000_000_000 - leadingIterations) % loopLength - 1;

		for (int i = 0; i < remainingIterations; i++) {
			currentDish = currentDish.tiltOneCycle();
		}

		return currentDish;
	}

	public long weight()
	{
		final int rows = cells.size();
		return Seq.seq(cells.stream()).zipWithIndex() //
				.mapToLong(rowWithIndex -> {
					final long rowWeight = rows - rowWithIndex.v2();
					return rowWithIndex.v1().stream() //
							.mapToLong(cell -> cell == CellType.ROUND ? rowWeight : 0) //
							.sum();
				}) //
				.sum();
	}

	@Override
	public String toString()
	{
		return cells.stream() //
				.map(row -> row.stream() //
						.map(CellType::toString) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	private static void tiltNorth(final List<? extends List<CellType>> newCells)
	{
		for (int i = 1, l = newCells.size(); i < l; i++) {
			final var row = newCells.get(i);
			for (int j = 0, l2 = row.size(); j < l2; j++) {
				final var cell = row.get(j);
				if (cell == CellType.ROUND) {
					// move the rock as north as possible
					int k = i - 1;
					while (k >= 0) {
						final var tmpCell = newCells.get(k).get(j);
						if (tmpCell != CellType.EMPTY) {
							break;
						}
						k--;
					}
					row.set(j, CellType.EMPTY);
					newCells.get(k + 1).set(j, CellType.ROUND);
				}
			}
		}
	}

	private static void tiltSouth(final List<? extends List<CellType>> newCells)
	{
		for (int l = newCells.size(), i = l - 2; i >= 0; i--) {
			final var row = newCells.get(i);
			for (int j = 0, l2 = row.size(); j < l2; j++) {
				final var cell = row.get(j);
				if (cell == CellType.ROUND) {
					// move the rock as south as possible
					int k = i + 1;
					while (k < l) {
						final var tmpCell = newCells.get(k).get(j);
						if (tmpCell != CellType.EMPTY) {
							break;
						}
						k++;
					}
					row.set(j, CellType.EMPTY);
					newCells.get(k - 1).set(j, CellType.ROUND);
				}
			}
		}
	}

	private static void tiltWest(final List<? extends List<CellType>> newCells)
	{
		final int rows = newCells.size();
		final int columns = newCells.getFirst().size();
		for (int j = 1; j < columns; j++) {
			for (int i = 0; i < rows; i++) {
				final var cell = newCells.get(i).get(j);
				if (cell == CellType.ROUND) {
					// move the rock as west as possible
					int k = j - 1;
					while (k >= 0) {
						final var tmpCell = newCells.get(i).get(k);
						if (tmpCell != CellType.EMPTY) {
							break;
						}
						k--;
					}
					newCells.get(i).set(j, CellType.EMPTY);
					newCells.get(i).set(k + 1, CellType.ROUND);
				}
			}
		}
	}

	private static void tiltEast(final List<? extends List<CellType>> newCells)
	{
		final int rows = newCells.size();
		final int columns = newCells.getFirst().size();
		for (int j = columns - 2; j >= 0; j--) {
			for (int i = 0; i < rows; i++) {
				final var cell = newCells.get(i).get(j);
				if (cell == CellType.ROUND) {
					// move the rock as east as possible
					int k = j + 1;
					while (k < columns) {
						final var tmpCell = newCells.get(i).get(k);
						if (tmpCell != CellType.EMPTY) {
							break;
						}
						k++;
					}
					newCells.get(i).set(j, CellType.EMPTY);
					newCells.get(i).set(k - 1, CellType.ROUND);
				}
			}
		}
	}

	public enum CellType
	{
		ROUND, SQUARE, EMPTY;

		public static CellType from(final char c)
		{
			return switch (c) {
				case 'O' -> ROUND;
				case '#' -> SQUARE;
				case '.' -> EMPTY;
				default -> throw new IllegalArgumentException("Unsupported cell type " + c);
			};
		}

		@Override
		public String toString()
		{
			return switch (this) {
				case ROUND -> "O";
				case SQUARE -> "#";
				case EMPTY -> ".";
			};
		}
	}

}
