package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay23 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var cpu = CoProcessor.parseInput(in);
            cpu.run(false);
            System.out.println("Part 1 - Answer %d".formatted(cpu.register(CoProcessor.Register.MUL_STAT)));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var cpu = CoProcessor.parseInput(in);
            cpu.register(CoProcessor.Register.A, 1);
            cpu.run(true);
            System.out.println("Part 2 - Answer %d".formatted(cpu.register(CoProcessor.Register.H)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay23.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
