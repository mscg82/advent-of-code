package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay23 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            Cupset cupset = Cupset.parseInput(readInput());
            cupset.run(100);
            System.out.println("Part 1: Answer: %s".formatted(cupset.toStringFrom(1)));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            Cupset cupset = Cupset.parseInput2(readInput());
            cupset.run(10_000_000, true);
            System.out.println("Part 2: Answer: %d".formatted(cupset.getValidationNumber()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay23.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
