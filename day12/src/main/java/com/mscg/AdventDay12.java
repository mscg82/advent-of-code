package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.mscg.AssembunnyCPU.Register;

public class AdventDay12 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var cpu = AssembunnyCPU.parseInput(in);
            cpu.run();
            System.out.println("Part 1 - Answer %d".formatted(cpu.register(Register.A)));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var cpu = AssembunnyCPU.parseInput(in);
            cpu.register(Register.C, 1);
            cpu.run();
            System.out.println("Part 2 - Answer %d".formatted(cpu.register(Register.A)));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay12.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
