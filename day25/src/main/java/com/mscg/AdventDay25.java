package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay25 {

    public static void main(final String[] args) throws Exception {
        part1();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var cpu = AssembunnyCPU3.parseInput(in);
            final long before = System.currentTimeMillis();
            System.out.println("Part 1 - Answer %d".formatted(cpu.findInitValue()));
            System.out.println("Elapsed " + (System.currentTimeMillis() - before) + "ms");
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay25.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
