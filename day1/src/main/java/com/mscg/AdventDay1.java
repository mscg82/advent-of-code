package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class AdventDay1 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final long sum = in.lines() //
                    .mapToLong(Long::parseLong) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(sum));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final long[] values = in.lines() //
                    .mapToLong(Long::parseLong) //
                    .toArray();
            final Set<Long> seenValues = new HashSet<>();
            long value = 0L;
            int idx = 0;
            while (!seenValues.contains(value)) {
                seenValues.add(value);
                value += values[idx];
                idx = (idx + 1) % values.length;
            }
            System.out.println("Part 2 - Answer %d".formatted(value));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
