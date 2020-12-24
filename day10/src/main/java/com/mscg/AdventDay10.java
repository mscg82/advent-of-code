package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdventDay10 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            AdaptersList adaptersList = AdaptersList.parseInput(in);
            Map<Long, Long> differences = adaptersList.countDifferences();
            long count1 = differences.get(1L);
            long count3 = differences.get(3L);
            System.out.println("Part 1: Differences: %d * %d = %d".formatted(count1, count3, count1 * count3));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            AdaptersList adaptersList = AdaptersList.parseInput(in);
            System.out.println("Part 2: Arragements: %d".formatted(adaptersList.countArrangments()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay10.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }
}
