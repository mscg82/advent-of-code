package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay10 {
    
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var chipFactory = ChipFactory.parseInput(in);
            int index = chipFactory.execute(bot -> !(bot.high() == 61 && bot.low() == 17));
            System.out.println("Part 1 - Answer %d".formatted(index));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var chipFactory = ChipFactory.parseInput(in);
            chipFactory.execute(__ -> true);
            int chip0 = chipFactory.getOutputs().get(0);
            int chip1 = chipFactory.getOutputs().get(1);
            int chip2 = chipFactory.getOutputs().get(2);
            System.out.println("Part 2 - Answer %d".formatted(chip0 * chip1 * chip2));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay10.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
