package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class AdventDay10 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            String input = in.readLine();
            String result = Stream.iterate(input, LookAndSay::transform) //
                    .skip(40) //
                    .findFirst() //
                    .orElseThrow();
            System.out.println("Part 1 - Answer %d".formatted(result.length()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            String input = in.readLine();
            String result = Stream.iterate(input, LookAndSay::transform) //
                    .skip(50) //
                    .findFirst() //
                    .orElseThrow();
            System.out.println("Part 2 - Answer %d".formatted(result.length()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay10.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
