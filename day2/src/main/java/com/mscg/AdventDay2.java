package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LongSummaryStatistics;

public class AdventDay2 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var boxSet = BoxSet.parseInput(in);
            long answer = boxSet.getBoxes().stream() //
                    .map(box -> box.facesAreas().summaryStatistics()) //
                    .mapToLong(summary -> summary.getSum() * 2 + summary.getMin()) //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(answer));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var boxSet = BoxSet.parseInput(in);
            long ribbonLength = boxSet.getBoxes().stream() //
                    .map(box -> box.facesPerimeters().summaryStatistics()) //
                    .mapToLong(LongSummaryStatistics::getMin) //
                    .sum();
            long boxLength = boxSet.getBoxes().stream() //
                    .mapToLong(box -> box.width() * box.height() * box.depth()) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(ribbonLength + boxLength));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
