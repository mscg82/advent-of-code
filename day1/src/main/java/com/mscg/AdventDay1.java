package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class AdventDay1 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final Seq<Integer> readings = Seq.seq(in.lines()) //
                    .map(Integer::parseInt);
            final long incrementsCount = getIncrementsCount(readings);

            System.out.println("Part 1 - Answer %d".formatted(incrementsCount));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final Seq<Integer> windows3 = Seq.seq(in.lines()) //
                    .map(Integer::parseInt) //
                    .sliding(3) //
                    .map(AdventDay1::sumValuesInWindow);

            final long windowsIncrementsCount = getIncrementsCount(windows3);

            System.out.println("Part 2 - Answer %d".formatted(windowsIncrementsCount));
        }
    }

    private static long getIncrementsCount(Seq<Integer> inputSequence) {
        return inputSequence //
                .sliding(2) //
                .map(Stream::toList) //
                .filter(w -> w.get(1) > w.get(0)) //
                .count();
    }

    private static Integer sumValuesInWindow(Seq<Integer> s) {
        return s.foldLeft(0, Integer::sum);
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
