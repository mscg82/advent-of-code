package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdventDay5 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        List<String> seatIds = readInput();
        int maxSeatId = seatIds.stream()
                .map(SeatId::fromString)
                .map(Optional::get)
                .mapToInt(SeatId::computeId)
                .max()
                .orElseGet(() -> -1);
        System.out.println("Part 1: Max seat id: %d".formatted(maxSeatId));
    }

    private static void part2() throws Exception {
        List<String> seatIds = readInput();
        int[] sortedIds = seatIds.stream()
                .map(SeatId::fromString)
                .map(Optional::get)
                .mapToInt(SeatId::computeId)
                .sorted()
                .toArray();
        for (int i = 1; i < sortedIds.length; i++) {
            if (sortedIds[i] - sortedIds[i - 1] != 1) {
                System.out.println("Part 2: My seat: %d".formatted(sortedIds[i] - 1));
            }
        }
    }

    private static List<String> readInput() throws Exception {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(AdventDay5.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8))) {
            return in.lines().collect(Collectors.toList());
        }
    }

}
