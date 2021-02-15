package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.mscg.LogicBoard.Constant;
import com.mscg.LogicBoard.Instruction;

public class AdventDay7 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var board = LogicBoard.parseInput(readInput());
            Map<String, Constant> portToValues = board.execute();
            System.out.println("Part 1 - Answer %d".formatted(portToValues.get("a").value()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var board = LogicBoard.parseInput(readInput());
            Map<String, Constant> portToValues = board.execute();

            board = board.patch(new Instruction(new Constant(portToValues.get("a").value()), "b"));
            portToValues = board.execute();
            
            System.out.println("Part 2 - Answer %d".formatted(portToValues.get("a").value()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay7.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}