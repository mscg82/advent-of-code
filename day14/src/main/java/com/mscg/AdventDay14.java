package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import com.mscg.Race.Position;

public class AdventDay14 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var race = Race.parseInput(in);
            List<Position> result = race.run(2503);
            var winner = result.stream() //
                    .max(Comparator.comparingInt(Position::km)) //
                    .orElseThrow();

            System.out.println("Part 1 - Answer %d".formatted(winner.km()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var race = Race.parseInput(in);
            List<Position> result = race.runWithPoints(2503);
            var winner = result.stream() //
                    .max(Comparator.comparingInt(Position::km)) //
                    .orElseThrow();

            System.out.println("Part 2 - Answer %d".formatted(winner.km()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay14.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
