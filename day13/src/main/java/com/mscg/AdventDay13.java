package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay13 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var table = Table.parseInput(in);

            System.out.println("Part 1 - Answer %d".formatted(table.findBestArrangement().happiness()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var table = Table.parseInput(in);
            var newTable = table.withMyself();

            System.out.println("Part 2 - Answer %d".formatted(newTable.findBestArrangement().happiness()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay13.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}