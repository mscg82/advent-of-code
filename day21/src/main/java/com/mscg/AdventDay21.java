package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay21 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var foodList = FoodList.parseInput(readInput());
            System.out.println("Part 1: Answer: %d".formatted(foodList.computePart1Answer()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var foodList = FoodList.parseInput(readInput());
            System.out.println("Part 2: Answer: %s".formatted(foodList.computePart2Answer()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay21.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
