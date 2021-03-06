package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.mscg.Computer.Register;

public class AdventDay23 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(readInput());
            computer.run();

            System.out.println("Part 1 - Answer %d".formatted(computer.register(Register.b)));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(readInput());
            computer.register(Register.a, 1L);
            computer.run();

            System.out.println("Part 2 - Answer %d".formatted(computer.register(Register.b)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay23.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
