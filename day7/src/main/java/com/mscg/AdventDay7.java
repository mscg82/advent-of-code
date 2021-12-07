package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

public class AdventDay7 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static long computeCost(final long position, final long target) {
        final long distance = Math.abs(position - target);
        return distance * (distance + 1) / 2; // closed formula for sum of all values between 1 and s
    }

    private static long[] getPositions(final BufferedReader in) throws IOException {
        return Arrays.stream(in.readLine().split(",")) //
                .mapToLong(Long::parseLong) //
                .sorted() //
                .toArray();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final long[] positions = getPositions(in);

            int i = 0;
            int j = positions.length - 1;
            long cost = 0L;
            while (i < j) {
                cost += positions[j] - positions[i];
                i++;
                j--;
            }

            System.out.println("Part 1 - Answer %d".formatted(cost));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final long[] positions = getPositions(in);

            final LongUnaryOperator computeCost = target -> Arrays.stream(positions) //
                    .map(position -> computeCost(position, target))  //
                    .sum();

            final LongStream possibleTargets = LongStream.rangeClosed(positions[0], positions[positions.length - 1]);
            final long minCost = possibleTargets //
                    .map(computeCost) //
                    .min() //
                    .orElseThrow();

            System.out.println("Part 2 - Answer %d".formatted(minCost));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay7.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
