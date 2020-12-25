package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay13 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var schedule = DepartureSchedule.parseInput(in);
            var earliestDeparture = schedule.findEarliestDeparture();
            System.out.println("Part 1: Answer: %d".formatted((earliestDeparture.minDepartureTime() - schedule.minDepartureTime()) * earliestDeparture.lineId()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var schedule = DepartureSchedule.parseInput(in);
            long solution = schedule.solveContest();
            System.out.println("Part 2: Answer: %d".formatted(solution));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay13.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
