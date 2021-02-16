package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class AdventDay8 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            List<String> lines = in.lines() //
                    .collect(Collectors.toUnmodifiableList());
            long charSize = lines.stream() //
                    .mapToInt(String::length) //
                    .sum();
            long memSize = lines.stream() //
                    .map(StringParser::cleanString) //
                    .mapToInt(String::length) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(charSize - memSize));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            List<String> lines = in.lines() //
                    .collect(Collectors.toUnmodifiableList());
            long charSize = lines.stream() //
                    .mapToInt(String::length) //
                    .sum();
            long expSize = lines.stream() //
                    .map(StringParser::expandString) //
                    .mapToInt(String::length) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(expSize - charSize));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay8.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
