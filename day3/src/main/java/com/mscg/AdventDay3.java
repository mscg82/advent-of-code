package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdventDay3 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        TerrainMap map = readInput();
        int trees = map.getTrees(3, 1);
        System.out.println("Part 1: Trees: %d".formatted(trees));
    }

    private static void part2() throws Exception {
        final long trees1;
        final long trees2;
        final long trees3;
        final long trees4;
        final long trees5;
        {
            TerrainMap map = readInput();
            trees1 = map.getTrees(1, 1);
        }
        {
            TerrainMap map = readInput();
            trees2 = map.getTrees(3, 1);
        }
        {
            TerrainMap map = readInput();
            trees3 = map.getTrees(5, 1);
        }
        {
            TerrainMap map = readInput();
            trees4 = map.getTrees(7, 1);
        }
        {
            TerrainMap map = readInput();
            trees5 = map.getTrees(1, 2);
        }
        long product = trees1 * trees2 * trees3 * trees4 * trees5;
        System.out.println("Part 2: Trees: (%d * %d * %d * %d * %d) = %d".formatted(trees1, trees2, trees3, trees4, trees5, product));
    }

    private static TerrainMap readInput() throws Exception {
        try (var in = new BufferedReader(new InputStreamReader(AdventDay3.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8))) {
            List<String> rows = in.lines()
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
            return TerrainMap.fromStrings(rows);
        }

    }
}

enum Cell {
    OPEN, TREE
}

class Tile {
    private final List<List<Cell>> cells = new ArrayList<>();

    public void addCellsRow(List<Cell> row) {
        cells.add(row);
    }

    public Optional<Cell> getCell(int x, int y) {
        if (y < 0 || y >= cells.size()) {
            return Optional.empty();
        }

        List<Cell> row = cells.get(y);
        return Optional.of(row.get(x % row.size()));
    }
}

class Sled {

    private int x;
    private int y;

    public Sled(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

}

class TerrainMap {

    private final Tile tile;
    private final Sled sled;

    public TerrainMap(Tile tile, Sled sled) {
        this.tile = tile;
        this.sled = sled;
    }

    public int getTrees(int dx, int dy) {
        int trees = 0;
        getSled().move(dx, dy);
        Optional<Cell> cell;
        while ((cell = getTile().getCell(getSled().x(), getSled().y())).isPresent()) {
            if (cell.get() == Cell.TREE) {
                trees++;
            }
            getSled().move(dx, dy);
        }
        return trees;
    }

    public Tile getTile() {
        return tile;
    }

    public Sled getSled() {
        return sled;
    }

    public static TerrainMap fromStrings(List<String> rows) {
        final var sled = new Sled(0, 0);
        final var tile = new Tile();
        for (var row : rows) {
            List<Cell> cellsRow = Arrays.stream(row.split(""))
                    .map(c -> c.equals(".") ? Cell.OPEN : Cell.TREE)
                    .collect(Collectors.toList());
            tile.addCellsRow(cellsRow);
        }
        return new TerrainMap(tile, sled);
    }

}
