package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class AdventDay14 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var diskGrid = DiskGrid.parseInput(in);
            final int activeCells = diskGrid.initialStatus() //
                    .mapToInt(BitSet::cardinality) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(activeCells));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            System.out.println("Part 2 - Answer %d".formatted(0));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay14.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
