package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class AdventDay4 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final long validPassphrases = in.lines()
                    .filter(line -> {
                        final Map<String, Long> wordsCount = Arrays.stream(line.split(" ")) //
                                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
                        return wordsCount.values().stream().allMatch(l -> l == 1L);
                    }) //
                    .count();
            System.out.println("Part 1 - Answer %d".formatted(validPassphrases));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final long validPassphrases = in.lines()
                    .filter(line -> {
                        final Map<String, Long> wordsCount = Arrays.stream(line.split(" ")) //
                                .map(s -> {
                                    final byte[] bytes = s.getBytes();
                                    Arrays.sort(bytes);
                                    return new String(bytes);
                                }) //
                                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
                        return wordsCount.values().stream().allMatch(l -> l == 1L);
                    }) //
                    .count();
            System.out.println("Part 2 - Answer %d".formatted(validPassphrases));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay4.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
