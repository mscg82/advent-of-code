package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay19 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            // final long sum = Calculator.parseInput1(in).stream()
            //         .map(Calculator::compute)
            //         .mapToLong(OptionalLong::orElseThrow)
            //         .sum();
            // System.out.println("Part 1: Answer: %d".formatted(sum));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // final long sum = Calculator.parseInput2(in).stream()
            //         .map(Calculator::compute)
            //         .mapToLong(OptionalLong::orElseThrow)
            //         .sum();
            // System.out.println("Part 2: Answer: %d".formatted(sum));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay19.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
