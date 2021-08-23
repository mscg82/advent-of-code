package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay12 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var potGen = PotGeneration.parseInput(in);
            final List<PotGeneration.IndexedPot> pots = potGen.evolvePlants(20L);
            System.out.println("Part 1 - Answer %d".formatted(sumIndexes(pots)));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var potGen = PotGeneration.parseInput(in);
            final List<PotGeneration.IndexedPot> pots = potGen.evolvePlants(50_000_000_000L);
            System.out.println("Part 2 - Answer %d".formatted(sumIndexes(pots)));
        }
    }

    private static long sumIndexes(final List<PotGeneration.IndexedPot> pots) {
        return pots.stream() //
                .filter(p -> p.pot() == PotGeneration.Pot.PLANTED) //
                .mapToLong(PotGeneration.IndexedPot::index)
                .sum();
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
