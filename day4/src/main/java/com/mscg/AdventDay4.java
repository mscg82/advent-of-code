package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay4 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            List<Passport> passports = Passport.parseInput(in);
            long validPassports = passports.stream().filter(Passport::isValidPart1).count();
            System.out.println("Part 1: Valid passports: %d".formatted(validPassports));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            List<Passport> passports = Passport.parseInput(in);
            long validPassports = passports.stream().filter(Passport::isValidPart2).count();
            System.out.println("Part 2: Valid passports: %d".formatted(validPassports));
        }
    }

    private static BufferedReader readInput() {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(AdventDay4.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
        return in;
    }
}
