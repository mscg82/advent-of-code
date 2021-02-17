package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.mscg.LocationMap.Path;

public class AdventDay9 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var map = LocationMap.parseInput(readInput());
            Path shortestPath = map.findShortestPath().orElseThrow();

            System.out.println("Part 1 - Answer %s".formatted(shortestPath));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var map = LocationMap.parseInput(readInput());
            Path longestPath = map.findLongestPath().orElseThrow();

            System.out.println("Part 2 - Answer %s".formatted(longestPath));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay9.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
