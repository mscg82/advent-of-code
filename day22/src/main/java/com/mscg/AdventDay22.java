package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.mscg.CombatGame.GameStatus;

public class AdventDay22 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var game = CombatGame.parseInput(readInput());
            System.out.println("Part 1: Answer: %d".formatted(game.getGameValue()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var game = CombatGame.parseInput(readInput());

            GameStatus status = game.playRecursiveGame();

            System.out.println("Part 2: Answer: %d".formatted(game.getValidationNumber(status)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay22.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
