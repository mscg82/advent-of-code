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
            final var computer = Intcode.parseInput(in);
            System.out.println("Part 1 - Answer %d".formatted(computer.execute(12, 2)));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var computer = Intcode.parseInput(in);
            for (int noun = 0; noun <= 99; noun++) {
                for (int verb = 0; verb <= 99; verb++) {
                    final int result = computer.execute(noun, verb);
                    if (result == 19690720) {
                        System.out.println("Part 2 - Answer %d".formatted(100 * noun + verb));
                        return;
                    }
                }
            }
            System.out.println("Part 2 - Unable to find answer");
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
