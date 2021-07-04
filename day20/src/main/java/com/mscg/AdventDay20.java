package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay20 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var gpu = GPU.parseInput(in);
            final var p0 = gpu.particles().get(0);
            final var p1 = p0.simulate(1);
            final var p2 = p1.simulate(1);
            final var p22 = p0.simulate(2);
            System.out.println(p1);
            System.out.println(p2 + " -> " + p22);
            System.out.println("Part 1 - Answer %d".formatted(gpu.getClosestParticle()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var gpu = GPU.parseInput(in);
            System.out.println("Part 2 - Answer %d".formatted(gpu.simulateCollisions()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay20.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
