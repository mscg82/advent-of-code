package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.LongStream;

public class AdventDay1 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final long totalFuel = in.lines() //
                    .mapToLong(Long::parseLong) //
                    .map(AdventDay1::getFuelForMass) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(totalFuel));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final long totalFuel = in.lines() //
                    .mapToLong(Long::parseLong) //
                    .flatMap(v -> LongStream.iterate(v / 3 - 2, v1 -> v1 >= 0, AdventDay1::getFuelForMass)) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(totalFuel));
        }
    }

    private static long getFuelForMass(final long mass) {
        return mass / 3 - 2;
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
