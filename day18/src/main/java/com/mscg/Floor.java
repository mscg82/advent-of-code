package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Floor(List<List<Tile>> tiles) {

    public void computeTiles() {
        for (int i = 1, l = tiles.size(); i < l; i++) {
            final List<Tile> prevRow = tiles.get(i - 1);
            final List<Tile> row = tiles.get(i);
            for (int j = 0, c = row.size(); j < c; j++) {
                final Tile left = j != 0 ? prevRow.get(j - 1) : Tile.SAFE;
                final Tile center = prevRow.get(j);
                final Tile right = j != c - 1 ? prevRow.get(j + 1) : Tile.SAFE;
                if (left == Tile.TRAP && center == Tile.TRAP && right == Tile.SAFE) {
                    row.set(j, Tile.TRAP);
                } else if (left == Tile.SAFE && center == Tile.TRAP && right == Tile.TRAP) {
                    row.set(j, Tile.TRAP);
                } else if (left == Tile.TRAP && center == Tile.SAFE && right == Tile.SAFE) {
                    row.set(j, Tile.TRAP);
                } else if (left == Tile.SAFE && center == Tile.SAFE && right == Tile.TRAP) {
                    row.set(j, Tile.TRAP);
                } else {
                    row.set(j, Tile.SAFE);
                }
            }
        }
    }

    @Override
    public String toString() {
        return tiles.stream() //
                .map(row -> row.stream().map(t -> t == null ? " " : t.toString()).collect(Collectors.joining())) //
                .collect(Collectors.joining("\n"));
    }

    public static Floor parseInput(final BufferedReader in, final int rows) throws IOException {
        final List<Tile> firstRow = in.readLine().chars() //
                .mapToObj(c -> Tile.fromChar((char) c)) //
                .toList();
        final int columns = firstRow.size();

        final List<List<Tile>> tiles = new ArrayList<>(rows);
        tiles.add(firstRow);
        for (int i = 1; i < rows; i++) {
            final List<Tile> row = Arrays.asList(new Tile[columns]);
            tiles.add(row);
        }
        return new Floor(List.copyOf(tiles));
    }

    public enum Tile {
        SAFE, TRAP;

        @Override
        public String toString() {
            return switch (this) {
                case SAFE -> ".";
                case TRAP -> "^";
            };
        }

        public static Tile fromChar(final char value) {
            return switch (value) {
                case '.' -> SAFE;
                case '^' -> TRAP;
                default -> throw new IllegalArgumentException("Invalid value " + value);
            };
        }
    }

}
