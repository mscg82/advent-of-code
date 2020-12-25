package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay12 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var boat = Boat.parseInput(in);
            boat.execute1();
            int distance = Math.abs(boat.getPosition().north()) + Math.abs(boat.getPosition().east());
            System.out.println("Part 1: Distance: %d".formatted(distance));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var boat = Boat.parseInput(in);
            boat.execute2();
            int distance = Math.abs(boat.getPosition().north()) + Math.abs(boat.getPosition().east());
            System.out.println("Part 2: Distance: %d".formatted(distance));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
