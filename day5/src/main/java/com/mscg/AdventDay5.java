package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay5 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            long niceStrings = in.lines() //
                    .filter(StringFilter::isNice) //
                    .count();
            System.out.println("Part 1 - Answer %d".formatted(niceStrings));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            // var line = in.readLine();
            // long floor = 0;
            // int answer = 0;
            // for (int i = 0, l = line.length(); i < l; i++) {
            //     switch (line.charAt(i)) {
            //         case '(' -> floor++;
            //         case ')' -> floor--;
            //         default -> throw new IllegalArgumentException("Invalid char in input");
            //     }
            //     if (floor == -1) {
            //         answer = i + 1;
            //         break;
            //     }
            // }
            // System.out.println("Part 2 - Answer %d".formatted(answer));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay5.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
