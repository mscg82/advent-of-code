package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

public record WordFinder(List<String> lines)
{
	public static WordFinder parseInput(final BufferedReader in) throws IOException
	{
		try {
			return new WordFinder(in.lines().toList());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countXMASOccurrences()
	{
		final int rows = lines.size();
		final int cols = lines.getFirst().length();

		long occurrences = 0;
		for (int r = 0; r < rows; r++) {
			final String row = lines.get(r);
			for (int c = 0; c < cols; c++) {
				final char ch = row.charAt(c);
				if (ch == 'X') {
					occurrences += searchXMASFromPosition(new Position(r, c), rows, cols);
				}
			}
		}

		return occurrences;
	}

	public long countCrossedMASOccurrences()
	{
		final int rows = lines.size();
		final int cols = lines.getFirst().length();
		long occurrences = 0;
		for (int r = 0; r < rows; r++) {
			final String row = lines.get(r);
			for (int c = 0; c < cols; c++) {
				final char ch = row.charAt(c);
				if (ch == 'A') {
					occurrences += searchCrossedMASFromPosition(new Position(r, c), rows, cols);
				}
			}
		}
		return occurrences;
	}

	private long searchXMASFromPosition(final Position posX, final int rows, final int cols)
	{
		return Arrays.stream(Direction.values()) //
				.filter(direction -> {
					final var posM = posX.move(direction);
					if (!posM.isValid(rows, cols) || 'M' != lines.get(posM.row()).charAt(posM.col())) {
						return false;
					}

					final var posA = posM.move(direction);
					if (!posA.isValid(rows, cols) || 'A' != lines.get(posA.row()).charAt(posA.col())) {
						return false;
					}

					final var posS = posA.move(direction);
					return posS.isValid(rows, cols) && 'S' == lines.get(posS.row()).charAt(posS.col());
				}) //
				.count();
	}

	private long searchCrossedMASFromPosition(final Position posA, final int rows, final int cols)
	{
		boolean found = false;
		final var upLeft = posA.move(Direction.UP_LEFT);
		final var downRight = posA.move(Direction.DOWN_RIGHT);
		if (upLeft.isValid(rows, cols) && downRight.isValid(rows, cols)) {
			final char c1 = lines.get(upLeft.row()).charAt(upLeft.col());
			final char c2 = lines.get(downRight.row()).charAt(downRight.col());
			if ((c1 == 'M' && c2 == 'S') || (c1 == 'S' && c2 == 'M')) {
				found = true;
			}
		}

		if (!found) {
			return 0;
		}

		final var upRight = posA.move(Direction.UP_RIGHT);
		final var downLeft = posA.move(Direction.DOWN_LEFT);
		if (upRight.isValid(rows, cols) && downLeft.isValid(rows, cols)) {
			final char c1 = lines.get(upRight.row()).charAt(upRight.col());
			final var c2 = lines.get(downLeft.row()).charAt(downLeft.col());
			if ((c1 == 'M' && c2 == 'S') || (c1 == 'S' && c2 == 'M')) {
				return 1;
			}
		}

		return 0;
	}

	private record Position(int row, int col)
	{
		public Position move(final Direction direction)
		{
			return switch (direction) {
				case UP -> new Position(row - 1, col);
				case UP_RIGHT -> new Position(row - 1, col + 1);
				case RIGHT -> new Position(row, col + 1);
				case DOWN_RIGHT -> new Position(row + 1, col + 1);
				case DOWN -> new Position(row + 1, col);
				case DOWN_LEFT -> new Position(row + 1, col - 1);
				case LEFT -> new Position(row, col - 1);
				case UP_LEFT -> new Position(row - 1, col - 1);
			};
		}

		public boolean isValid(final int rows, final int cols)
		{
			return row >= 0 && row < rows && col >= 0 && col < cols;
		}
	}

	private enum Direction
	{
		UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT
	}
}
