package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Optional;
import java.util.OptionalLong;

public class AdventDay9 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            XMASReader xmasReader = XMASReader.parseInput(in, 25);
            OptionalLong invalidValue = xmasReader.getFirstInvalidValue();
            System.out.println("Part 1: Invalid value: %s".formatted(invalidValue));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            XMASReader xmasReader = XMASReader.parseInput(in, 25);
            Optional<long[]> breakingSequence = xmasReader.getBreakingSequence();
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            long[] sequence = breakingSequence.get();
            LongSummaryStatistics statistics = Arrays.stream(sequence).summaryStatistics();
            long sum = statistics.getMin() + statistics.getMax();
            System.out.println("Part 2: Sequence: %d + %d = %d".formatted(statistics.getMin(), statistics.getMax(), sum));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay9.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
