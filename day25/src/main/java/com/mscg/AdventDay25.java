package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay25 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var breaker = Breaker.parseInput(readInput());
            var loopSizes = breaker.computeLoopSizes();
            long encryptionKey1 = Breaker.generateKey(breaker.getPublicKey2(), loopSizes.loopSize1());
            long encryptionKey2 = Breaker.generateKey(breaker.getPublicKey1(), loopSizes.loopSize2());
            System.out.println("Part 1: Answer: %d - %d".formatted(encryptionKey1, encryptionKey2));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // var game = CombatGame.parseInput(readInput());

            // GameStatus status = game.playRecursiveGame();

            // System.out.println("Part 2: Answer: %d".formatted(game.getValidationNumber(status)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay25.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
