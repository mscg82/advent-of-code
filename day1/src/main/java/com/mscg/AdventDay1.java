package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;

public class AdventDay1 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final String line = in.readLine();
            final int result = StreamUtils.windowed(Stream.concat(line.chars().boxed(), Stream.of((int) line.charAt(0))), 2) //
                    .filter(window -> window.get(0).equals(window.get(1))) //
                    .mapToInt(window -> window.get(0) - '0') //
                    .sum();
            System.out.println("Part 1 - Answer %d".formatted(result));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final String line = in.readLine();
            final String rotatedLine = line.substring(line.length() / 2) + line.substring(0, line.length() / 2);
            record Pair(int digit1, int digit2) {
            }
            final int result = StreamUtils.zip(line.chars().boxed(), rotatedLine.chars().boxed(), (c1, c2) -> new Pair(c1 - '0', c2 - '0')) //
                    .filter(p -> p.digit1() == p.digit2()) //
                    .mapToInt(Pair::digit1) //
                    .sum();
            System.out.println("Part 2 - Answer %d".formatted(result));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
