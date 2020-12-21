package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;

public class AdventDay8 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var interpreter = Interpreter.parseInput(in);
            Interpreter.Result result = interpreter.run();
            System.out.println("Part 1: Result: %s".formatted(result));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var interpreter = Interpreter.parseInput(in);
            OptionalInt result = interpreter.runFixed();
            System.out.println("Part 2: Result: %s".formatted(result));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay8.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
