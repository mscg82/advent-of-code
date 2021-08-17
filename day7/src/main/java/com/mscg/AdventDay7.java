package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay7 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var assembler = SleighAssembler.parseInput(in);
            System.out.println("Part 1 - Answer %s".formatted(assembler.findSequence()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var assembler = SleighAssembler.parseInput(in);
            System.out.println("Part 2 - Answer %s".formatted(assembler.findTimedSequence(5, 60)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay7.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
