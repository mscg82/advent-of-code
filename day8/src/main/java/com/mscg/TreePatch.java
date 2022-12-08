package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

public record TreePatch(int[][] heights, int rows, int cols)
{
	public static TreePatch parseInput(final BufferedReader in) throws IOException
	{
		try {
			final int[][] heights = in.lines() //
					.map(line -> line.chars() //
							.map(c -> c - '0') //
							.toArray()) //
					.toArray(int[][]::new);
			return new TreePatch(heights, heights.length, heights[0].length);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int countVisibleTrees()
	{
		record Coord(int i, int j) {}

		final Set<Coord> visibleCoords = new HashSet<>();

		// left-to-right
		for (int i = 0; i < rows; i++) {
			int max = -1;
			for (int j = 0; j < cols; j++) {
				if (heights[i][j] > max) {
					max = heights[i][j];
					visibleCoords.add(new Coord(i, j));
				}
			}
		}

		// right-to-left
		for (int i = 0; i < rows; i++) {
			int max = -1;
			for (int j = cols - 1; j >= 0; j--) {
				if (heights[i][j] > max) {
					max = heights[i][j];
					visibleCoords.add(new Coord(i, j));
				}
			}
		}

		// top-to-bottom
		for (int j = 0; j < cols; j++) {
			int max = -1;
			for (int i = 0; i < rows; i++) {
				if (heights[i][j] > max) {
					max = heights[i][j];
					visibleCoords.add(new Coord(i, j));
				}
			}
		}

		// bottom-to-top
		for (int j = 0; j < cols; j++) {
			int max = -1;
			for (int i = rows - 1; i >= 0; i--) {
				if (heights[i][j] > max) {
					max = heights[i][j];
					visibleCoords.add(new Coord(i, j));
				}
			}
		}

		return visibleCoords.size();
	}

	public long computeHighestScenicScore()
	{
		long highest = -1L;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				final int currentHeight = heights[i][j];

				long treesUp = 0;
				for (int k = i - 1; k >= 0; k--) {
					treesUp++;
					if (heights[k][j] >= currentHeight) {
						break;
					}
				}

				long treesDown = 0;
				for (int k = i + 1; k < rows; k++) {
					treesDown++;
					if (heights[k][j] >= currentHeight) {
						break;
					}
				}

				long treesLeft = 0;
				for (int k = j - 1; k >= 0; k--) {
					treesLeft++;
					if (heights[i][k] >= currentHeight) {
						break;
					}
				}

				long treesRight = 0;
				for (int k = j + 1; k < cols; k++) {
					treesRight++;
					if (heights[i][k] >= currentHeight) {
						break;
					}
				}

				final long scenicScore = treesUp * treesDown * treesLeft * treesRight;
				if (scenicScore > highest) {
					highest = scenicScore;
				}
			}
		}

		return highest;
	}

}
