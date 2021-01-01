package com.mscg;

import com.codepoetics.protonpack.StreamUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay15 {
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var game = Game.parseInput(in);
            int value = StreamUtils.stream(game)
                    .skip(2019)
                    .findFirst()
                    .orElseThrow();
            System.out.println("Part 1: Answer: %d".formatted(value));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var game = Game.parseInput(in);
            int value = StreamUtils.stream(game)
                    .skip(29999999)
                    .findFirst()
                    .orElseThrow();
            System.out.println("Part 2: Answer: %d".formatted(value));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay15.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }
}
