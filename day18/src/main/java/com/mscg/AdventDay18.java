package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay18 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var floor = Floor.parseInput(in, 40);
            floor.computeTiles();
            final long safeTiles = floor.tiles().stream() //
                    .flatMap(List::stream) //
                    .filter(t -> t == Floor.Tile.SAFE) //
                    .count();
            System.out.println("Part 1 - Answer %d".formatted(safeTiles));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var floor = Floor.parseInput(in, 400000);
            floor.computeTiles();
            final long safeTiles = floor.tiles().stream() //
                    .flatMap(List::stream) //
                    .filter(t -> t == Floor.Tile.SAFE) //
                    .count();
            System.out.println("Part 2 - Answer %d".formatted(safeTiles));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay18.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
