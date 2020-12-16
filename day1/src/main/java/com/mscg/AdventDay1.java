package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay1 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        final int[] values = readInput();

        for (int i = 0; i < values.length - 1; i++) {
            for (int j = i + 1; j < values.length; j++) {
                if (values[i] + values[j] == 2020) {
                    int product = values[i] * values[j];
                    System.out.println("Part 1: %d * %d = %d".formatted(values[i], values[j], product));
                }
            }
        }
    }

    private static void part2() throws IOException {
        final int[] values = readInput();

        for (int i = 0; i < values.length - 2; i++) {
            for (int j = i + 1; j < values.length - 1; j++) {
                for (int k = j + 1; k < values.length; k++) {
                    if (values[i] + values[j] + values[k] == 2020) {
                        int product = values[i] * values[j] * values[k];
                        System.out.println("Part 2: %d * %d * %d = %d".formatted(values[i], values[j], values[k], product));
                    }
                }
            }
        }
    }

    private static int[] readInput() throws IOException {
        final int[] values;
        try (var in = new BufferedReader(new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8))) {
            values = in.lines()
                    .filter(s -> !s.isBlank())
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
        return values;
    }

}
