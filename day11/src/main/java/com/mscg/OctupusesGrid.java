package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record OctupusesGrid(int[][] grid, int rows, int cols)
{

	public static OctupusesGrid parseInput(final BufferedReader in) throws IOException
	{
		try {
			final int[][] grid = in.lines() //
					.map(line -> line.chars().map(c -> c - '0').toArray()) //
					.toArray(int[][]::new);
			return new OctupusesGrid(grid, grid.length, grid[0].length);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static int[][] cloneGrid(final int[][] grid)
	{
		final int[][] newGrid = grid.clone();
		for (int i = 0; i < newGrid.length; i++) {
			newGrid[i] = newGrid[i].clone();
		}
		return newGrid;
	}

	public long countFlashes(final int iterations)
	{
		long totalFlashes = 0L;

		int[][] previousGrid = grid;
		for (int it = 0; it < iterations; it++) {
			final var evolution = evolveGrid(previousGrid);
			previousGrid = evolution.newGrid();
			totalFlashes += evolution.flashes();
		}
		return totalFlashes;
	}

	public long findFirstSynchFlash()
	{
		return Stream.iterate(new Evolution(grid, 0), //
				ev -> ev.flashes() != (long) rows * cols, //
				ev -> evolveGrid(ev.newGrid())) //
				.count();
	}

	@Override
	public String toString()
	{
		return Arrays.stream(grid) //
				.map(row -> Arrays.stream(row) //
						.mapToObj(String::valueOf) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	private Evolution evolveGrid(final int[][] previousGrid)
	{
		final int[][] newGrid = cloneGrid(previousGrid);
		final Set<Position> flashes = new HashSet<>();
		// Step 1: increase each cell by 1 and collect flashing ones
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				newGrid[i][j]++;
				if (newGrid[i][j] > 9) {
					flashes.add(new Position(i, j));
				}
			}
		}

		// Step 2: increase flashing cells neighbours
		final Deque<Position> queue = new LinkedList<>(flashes);
		while (!queue.isEmpty()) {
			final var flash = queue.pop();
			flash.getNeighbours(rows, cols) //
					.filter(n -> !flashes.contains(n)) //
					.forEach(n -> {
						newGrid[n.i()][n.j()]++;
						if (newGrid[n.i()][n.j()] > 9) {
							flashes.add(n);
							queue.add(n);
						}
					});
		}

		// Step 3: reset flashed cells
		flashes.forEach(flash -> newGrid[flash.i()][flash.j()] = 0);

		return new Evolution(newGrid, flashes.size());
	}

	private record Evolution(int[][] newGrid, long flashes)
	{

	}

	private record Position(int i, int j)
	{

		public Stream<Position> getNeighbours(final int rows, final int cols)
		{
			return IntStream.rangeClosed(i - 1, i + 1) //
					.filter(i -> i >= 0 && i < rows) //
					.mapToObj(i -> IntStream.rangeClosed(j - 1, j + 1) //
							.filter(j -> j >= 0 && j < cols && (i != this.i || j != this.j)) //
							.mapToObj(j -> new Position(i, j))) //
					.flatMap(s -> s);
		}

	}
}
