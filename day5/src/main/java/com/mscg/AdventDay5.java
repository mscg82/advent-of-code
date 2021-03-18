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
            var keycodeDecrypter = KeycodeDecrypter.parseInput(readInput());
            System.out.println("Part 1 - Answer %s".formatted(keycodeDecrypter.findPassword1()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var keycodeDecrypter = KeycodeDecrypter.parseInput(readInput());
            System.out.println("Part 2 - Answer %s".formatted(keycodeDecrypter.findPassword2()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay5.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
