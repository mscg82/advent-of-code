package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay17 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var cubeSat = CubeSat.parseInput(in, 0, 6, 6, 6);
            var booted = cubeSat.boot();
            final long activeCells = booted.stream().filter(s -> s == CubeSat.CubeState.ACTIVE).count();
            System.out.println("Part 1: Answer: %d".formatted(activeCells));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var cubeSat = CubeSat.parseInput(in, 6, 6, 6, 6);
            var booted = cubeSat.boot();
            final long activeCells = booted.stream().filter(s -> s == CubeSat.CubeState.ACTIVE).count();
            System.out.println("Part 2: Answer: %d".formatted(activeCells));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay17.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
