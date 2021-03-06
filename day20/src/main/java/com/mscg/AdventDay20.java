package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay20 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var giftDelivery = GiftDelivery.parseInput(readInput(), 1_000_000, 1_000_000, false);

            System.out.println("Part 1 - Answer %s".formatted(giftDelivery.findHouseNumber()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var giftDelivery = GiftDelivery.parseInput(readInput(), 1_000_000, 1_000_000, true);

            System.out.println("Part 2 - Answer %s".formatted(giftDelivery.findHouseNumber()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay20.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
