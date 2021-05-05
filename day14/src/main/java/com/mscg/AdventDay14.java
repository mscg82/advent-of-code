package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay14 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in);
            final long nthPad = padGenerator.findNthPad(64, false);
            System.out.println("Part 1 - Answer %d".formatted(nthPad));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in);
            final long nthPad = padGenerator.findNthPad(64, true);
            System.out.println("Part 2 - Answer %d".formatted(nthPad));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay14.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}