package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay11 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var seatBoard = SeatBoard.parseInput(in);
            var evolved = seatBoard.evolveUntilHalt1();
            System.out.println("Part 1: Occupied seats: %d".formatted(evolved.countOccupied()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var seatBoard = SeatBoard.parseInput(in);
            var evolved = seatBoard.evolveUntilHalt2();
            System.out.println("Part 2: Occupied seats: %d".formatted(evolved.countOccupied()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay11.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
