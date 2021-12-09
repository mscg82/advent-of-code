package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record HeightMap(int[][] heights, int rows, int cols) {

    public static HeightMap parseInput(final BufferedReader in) throws IOException {
        try {
            final int[][] heights = in.lines() //
                    .map(line -> line.chars().map(c -> c - '0').toArray()) //
                    .toArray(int[][]::new);
            return new HeightMap(heights, heights.length, heights[0].length);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public long getBasinRisk() {
        final List<Position> valleys = getValleys();

        final long[] basinSizes = valleys.stream() //
                .mapToLong(valley -> (long) getBasin(valley).size()) //
                .sorted() //
                .toArray();

        return basinSizes[basinSizes.length - 1] * basinSizes[basinSizes.length - 2] * basinSizes[basinSizes.length - 3];
    }

    public long sumValleysRisks() {
        final List<Position> valleys = getValleys();
        return valleys.stream() //
                .mapToInt(pos -> heights[pos.i()][pos.j()] + 1) //
                .sum();
    }

    private Set<Position> getBasin(final Position valley) {
        final Set<Position> basin = new HashSet<>();
        basin.add(valley);

        final Deque<Position> queue = new LinkedList<>();
        queue.add(valley);

        while (!queue.isEmpty()) {
            final Position position = queue.pop();
            final int height = heights[position.i()][position.j()];
            final Position[] neighbours = getNeighbours(position.i(), position.j());
            for (final Position pos : neighbours) {
                final int posHeight = heights[pos.i()][pos.j()];
                if (posHeight != 9 && !basin.contains(pos) && posHeight > height) {
                    basin.add(pos);
                    queue.addLast(pos);
                }
            }
        }

        return basin;
    }

    private List<Position> getValleys() {
        return IntStream.range(0, rows) //
                .mapToObj(i -> IntStream.range(0, cols) //
                        .filter(j -> {
                            final int height = heights[i][j];
                            return Arrays.stream(getNeighbours(i, j)) //
                                    .mapToInt(pos -> heights[pos.i()][pos.j()]) //
                                    .allMatch(h -> h > height);
                        }) //
                        .mapToObj(j -> new Position(i, j))) //
                .flatMap(s -> s) //
                .toList();
    }

    private Position[] getNeighbours(final int i, final int j) {
        return Stream.of( //
                        new Position(i - 1, j), //
                        new Position(i, j + 1), //
                        new Position(i + 1, j), //
                        new Position(i, j - 1)) //
                .filter(pos -> pos.i() >= 0 && pos.i() < rows && pos.j() >= 0 && pos.j() < cols) //
                .toArray(Position[]::new);
    }

    private record Position(int i, int j) {

    }

}
