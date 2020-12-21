package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.OptionalInt;

public class AdventDay8Test {

    @Test
    public void testParser() throws Exception {
        try (BufferedReader in = readInput()) {
            var interpreter = Interpreter.parseInput(in);
            List<Interpreter.Instruction> instructions = interpreter.getInstructions();

            Assertions.assertEquals(9, instructions.size());
            Assertions.assertEquals(new Interpreter.Instruction(Interpreter.OpCodes.NOP, 0), instructions.get(0));
            Assertions.assertEquals(new Interpreter.Instruction(Interpreter.OpCodes.ACC, 1), instructions.get(1));
            Assertions.assertEquals(new Interpreter.Instruction(Interpreter.OpCodes.JMP, 4), instructions.get(2));
            Assertions.assertEquals(new Interpreter.Instruction(Interpreter.OpCodes.JMP, -3), instructions.get(4));
        }
    }

    @Test
    public void testRun() throws Exception {
        try (BufferedReader in = readInput()) {
            var interpreter = Interpreter.parseInput(in);
            Interpreter.Result result = interpreter.run();
            Assertions.assertEquals(new Interpreter.Result(false, 5), result);
        }
    }

    @Test
    public void testRunFixed() throws Exception {
        try (BufferedReader in = readInput()) {
            var interpreter = Interpreter.parseInput(in);
            OptionalInt result = interpreter.runFixed();
            Assertions.assertEquals(OptionalInt.of(8), result);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
