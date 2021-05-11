package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AdventDay2 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final int result = in.lines() //
                    .map(line -> line.split("\\s")) //
                    .map(values -> Arrays.stream(values) //
                            .mapToInt(Integer::parseInt) //
                            .summaryStatistics()) //
                    .mapToInt(stats -> stats.getMax() - stats.getMin()) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(result));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final int result = in.lines() //
                    .map(line -> line.split("\\s")) //
                    .map(values -> Arrays.stream(values) //
                            .mapToInt(Integer::parseInt) //
                            .sorted() //
                            .toArray()) //
                    .mapToInt(sortedValues -> {
                        for (int i = 0; i < sortedValues.length - 1; i++) {
                            for (int j = i + 1; j < sortedValues.length; j++) {
                                if (sortedValues[j] % sortedValues[i] == 0) {
                                    return sortedValues[j] / sortedValues[i];
                                }
                            }
                        }
                        throw new IllegalArgumentException("Invalid set of values " + Arrays.toString(sortedValues));
                    }) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(result));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
