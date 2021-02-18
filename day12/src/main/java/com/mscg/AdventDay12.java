package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay12 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            // long niceStrings = in.lines() //
            //         .filter(StringFilter::isNice) //
            //         .count();
            // System.out.println("Part 1 - Answer %d".formatted(niceStrings));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            // long niceStrings = in.lines() //
            //         .filter(StringFilter::isNice2) //
            //         .count();
            // System.out.println("Part 2 - Answer %d".formatted(niceStrings));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
