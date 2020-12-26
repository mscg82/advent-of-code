package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay14 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(in);
            computer.run1();
            long memorySum = computer.getMemory().values()
                    .stream().mapToLong(Long::longValue)
                    .sum();
            System.out.println("Part 1: Answer: %d".formatted(memorySum));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(in);
            computer.run2();
            long memorySum = computer.getMemory().values()
                    .stream().mapToLong(Long::longValue)
                    .sum();
            System.out.println("Part 2: Answer: %d".formatted(memorySum));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay14.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
