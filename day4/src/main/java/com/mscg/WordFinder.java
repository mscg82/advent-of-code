package com.mscg;

import com.mscg.utils.Position8Bits;

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
		for (int y = 0; y < rows; y++) {
			final String row = lines.get(y);
			for (int x = 0; x < cols; x++) {
				final char ch = row.charAt(x);
				if (ch == 'X') {
					occurrences += searchXMASFromPosition(new Position8Bits(x, y), rows, cols);
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
					occurrences += searchCrossedMASFromPosition(new Position8Bits(c, r), rows, cols);
				}
			}
		}
		return occurrences;
	}

	private long searchXMASFromPosition(final Position8Bits posX, final int rows, final int cols)
	{
		return Arrays.stream(Direction.values()) //
				.filter(direction -> {
					final var posM = direction.move(posX);
					if (!posM.isValid(rows, cols) || 'M' != lines.get(posM.y()).charAt(posM.x())) {
						return false;
					}

					final var posA = direction.move(posM);
					if (!posA.isValid(rows, cols) || 'A' != lines.get(posA.y()).charAt(posA.x())) {
						return false;
					}

					final var posS = direction.move(posA);
					return posS.isValid(rows, cols) && 'S' == lines.get(posS.y()).charAt(posS.x());
				}) //
				.count();
	}

	private long searchCrossedMASFromPosition(final Position8Bits posA, final int rows, final int cols)
	{
		boolean found = false;
		final var upLeft = Direction.UP_LEFT.move(posA);
		final var downRight = Direction.DOWN_RIGHT.move(posA);
		if (upLeft.isValid(rows, cols) && downRight.isValid(rows, cols)) {
			final char c1 = lines.get(upLeft.y()).charAt(upLeft.x());
			final char c2 = lines.get(downRight.y()).charAt(downRight.x());
			if ((c1 == 'M' && c2 == 'S') || (c1 == 'S' && c2 == 'M')) {
				found = true;
			}
		}

		if (!found) {
			return 0;
		}

		final var upRight = Direction.UP_RIGHT.move(posA);
		final var downLeft = Direction.DOWN_LEFT.move(posA);
		if (upRight.isValid(rows, cols) && downLeft.isValid(rows, cols)) {
			final char c1 = lines.get(upRight.y()).charAt(upRight.x());
			final var c2 = lines.get(downLeft.y()).charAt(downLeft.x());
			if ((c1 == 'M' && c2 == 'S') || (c1 == 'S' && c2 == 'M')) {
				return 1;
			}
		}

		return 0;
	}

	private enum Direction
	{
		UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT;

		public Position8Bits move(final Position8Bits position)
		{
			return switch (this) {
				case UP -> new Position8Bits(position.x(), position.y() - 1);
				case UP_RIGHT -> new Position8Bits(position.x() + 1, position.y() - 1);
				case RIGHT -> new Position8Bits(position.x() + 1, position.y());
				case DOWN_RIGHT -> new Position8Bits(position.x() + 1, position.y() + 1);
				case DOWN -> new Position8Bits(position.x(), position.y() + 1);
				case DOWN_LEFT -> new Position8Bits(position.x() - 1, position.y() + 1);
				case LEFT -> new Position8Bits(position.x() - 1, position.y());
				case UP_LEFT -> new Position8Bits(position.x() - 1, position.y() - 1);
			};
		}
	}
}
