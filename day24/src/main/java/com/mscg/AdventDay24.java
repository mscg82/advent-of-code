package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay24 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            Floor floor = Floor.parseInput(readInput(), 37);
            floor.run();
            System.out.println("Part 1: Answer: %d".formatted(floor.countBlackTiles()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // var game = CombatGame.parseInput(readInput());

            // GameStatus status = game.playRecursiveGame();

            // System.out.println("Part 2: Answer:
            // %d".formatted(game.getValidationNumber(status)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay24.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
