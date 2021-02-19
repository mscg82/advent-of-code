package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay11 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            System.out.println("Part 1 - Answer %s".formatted(PasswordManager.nextValid("hxbxwxba")));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            System.out.println("Part 2 - Answer %s".formatted(PasswordManager.nextValid("hxbxwxba", 1)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay11.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
