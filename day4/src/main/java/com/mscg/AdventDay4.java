package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay4 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            // var line = in.readLine();
            // Map<Character, Long> counts = line.chars() //
            //         .collect(() -> new HashMap<Character, Long>(), //
            //                 (acc, v) -> {
            //                     acc.merge((char) v, 1L, Long::sum);
            //                 }, //
            //                 (m1, m2) -> {
            //                     m2.forEach((k, v) -> m1.merge(k, v, Long::sum));
            //                 });
            // long floor = counts.get('(') - counts.get(')');
            // System.out.println("Part 1 - Answer %d".formatted(floor));
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
                new InputStreamReader(AdventDay4.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
