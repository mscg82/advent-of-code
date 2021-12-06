package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay2 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final Submarine sub = Submarine.parseInput(in);
            final Submarine.Position finalPosition = sub.execute();
            System.out.println("Part 1 - Answer %d".formatted(finalPosition.depth() * finalPosition.horizontal()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final Submarine sub = Submarine.parseInput(in);
            final Submarine.Position finalPosition = sub.execute2();
            System.out.println("Part 2 - Answer %d".formatted(finalPosition.depth() * finalPosition.horizontal()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
