package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay6 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var grid = Grid.parseInput(in);
            grid.run();
            long lightsOn = grid.getLights().values().stream() //
                    .filter(Boolean::booleanValue) //
                    .count();
            System.out.println("Part 1 - Answer %d".formatted(lightsOn));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var grid = Grid.parseInput(in);
            grid.run2();
            long totalBrightness = grid.getBrightness().values().stream() //
                    .mapToLong(Long::longValue) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(totalBrightness));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay6.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
