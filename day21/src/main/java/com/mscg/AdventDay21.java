package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay21 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var generator = ArtGenerator.parseInput(in);
            final ArtGenerator.Pattern image = generator.generateImage(5);
            System.out.println("Part 1 - Answer %d".formatted(image.pixels().stream() //
                    .flatMap(List::stream)
                    .filter(p -> p == ArtGenerator.PixelStatus.ON)
                    .count()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var generator = ArtGenerator.parseInput(in);
            final ArtGenerator.Pattern image = generator.generateImage(18);
            System.out.println("Part 2 - Answer %d".formatted(image.pixels().stream() //
                    .flatMap(List::stream)
                    .filter(p -> p == ArtGenerator.PixelStatus.ON)
                    .count()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay21.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
