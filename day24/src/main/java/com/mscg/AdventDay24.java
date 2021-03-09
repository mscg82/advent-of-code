package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay24 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var giftsBag = GiftsBag.parseInput(readInput());

            System.out.println("Part 1 - Answer %d".formatted(giftsBag.findQuantumEntanglement(3)));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // var containers = ContainerList.parseInput(in);

            // System.out.println("Part 2 - Answer %d".formatted(containers.computeShorterCombinations()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay24.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
