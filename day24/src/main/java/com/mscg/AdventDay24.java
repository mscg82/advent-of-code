package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class AdventDay24 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            Floor floor = Floor.parseInput(readInput(), 37);
            floor.run();
            System.out.println("Part 1: Answer: %d".formatted(floor.countBlackTiles()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            Floor floor = Floor.parseInput(readInput(), 137);
            floor.run();

            long[] blackTiles = Stream.iterate(floor, Floor::evolve) //
                    .skip(1) //
                    .limit(100) //
                    .mapToLong(Floor::countBlackTiles) //
                    .toArray();

            System.out.println("Part 2: Answer: %d".formatted(blackTiles[blackTiles.length - 1]));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay24.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
