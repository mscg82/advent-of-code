package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.CPU.Condition;
import com.mscg.CPU.Instruction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay8Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var cpu = CPU.parseInput(in);
            Assertions.assertEquals(List.of( //
                    new Instruction("b", CPU.Operation.INC, 5, new Condition("a", CPU.Comparison.GT, 1)), //
                    new Instruction("a", CPU.Operation.INC, 1, new Condition("b", CPU.Comparison.LT, 5)), //
                    new Instruction("c", CPU.Operation.DEC, -10, new Condition("a", CPU.Comparison.GTE, 1)), //
                    new Instruction("c", CPU.Operation.INC, -20, new Condition("c", CPU.Comparison.EQ, 10)) //
            ), cpu.getInstructions());
        }
    }

    @Test
    public void testLargestValue() throws Exception {
        try (BufferedReader in = readInput()) {
            final var cpu = CPU.parseInput(in);
            cpu.run();
            Assertions.assertEquals(1L, cpu.getLargestValue());
            Assertions.assertEquals(1L, cpu.getRegisters().getOrDefault("a", 0L));
            Assertions.assertEquals(0L, cpu.getRegisters().getOrDefault("b", 0L));
            Assertions.assertEquals(-10L, cpu.getRegisters().getOrDefault("c", 0L));
        }
    }

    @Test
    public void testHighestValue() throws Exception {
        try (BufferedReader in = readInput()) {
            final var cpu = CPU.parseInput(in);
            final long highest = cpu.run();
            Assertions.assertEquals(10L, highest);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
