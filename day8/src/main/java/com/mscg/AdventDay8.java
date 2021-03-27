package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay8 {
    
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var screen = Screen.parseInput(6, 50, in);
            screen.run();
            System.out.println("Part 1 - Answer %d".formatted(screen.countActivePixels()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var screen = Screen.parseInput(6, 50, in);
            screen.run();
            System.out.println("Part 2 - Answer %n%s".formatted(screen.toString().replace(".", " ")));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay8.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
