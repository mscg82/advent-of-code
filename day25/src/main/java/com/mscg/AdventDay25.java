package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay25 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            // String input = in.lines() //
            //         .collect(Collectors.joining());

            // System.out.println("Part 1 - Answer %d".formatted(JsonCleaner.sumValues(input)));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // String input = in.lines() //
            //         .collect(Collectors.joining());

            // System.out.println("Part 2 - Answer %d".formatted(JsonCleaner.sumValues(JsonCleaner.cleanJson(input))));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay25.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
