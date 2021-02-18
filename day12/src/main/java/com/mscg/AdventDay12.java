package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdventDay12 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            String input = in.lines() //
                    .collect(Collectors.joining());
            var pattern = Pattern.compile("(-?\\d+)");
            var matcher = pattern.matcher(input);

            int sum = 0;
            while (matcher.find()) {
                int val = Integer.parseInt(matcher.group(1));
                sum += val;
            }

            System.out.println("Part 1 - Answer %d".formatted(sum));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            // long niceStrings = in.lines() //
            // .filter(StringFilter::isNice2) //
            // .count();
            // System.out.println("Part 2 - Answer %d".formatted(niceStrings));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
