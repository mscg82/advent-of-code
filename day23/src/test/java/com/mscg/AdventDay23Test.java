package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.mscg.Computer.Register;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay23Test {

    @Test
    public void testParse() throws Exception {
        List<String> instructionStrs;
        try (var in = readInput()) {
            instructionStrs = in.lines().collect(Collectors.toUnmodifiableList());
        }
        var computer = Computer.parseInput(readInput());
        var instructions = computer.getInstructions();
        Assertions.assertEquals(instructionStrs.size(), instructions.size());
        for (int i = 0, l = instructionStrs.size(); i < l; i++) {
            Assertions.assertEquals(instructionStrs.get(i), instructions.get(i).toString(),
                    "Instruction " + i + " has not been parsed correctly");
        }
    }

    @Test
    public void testRun() throws Exception {
        var program = """
                inc a
                jio a, +2
                tpl a
                inc a
                """;
        var computer = Computer.parseInput(new BufferedReader(new StringReader(program)));
        computer.run();
        Assertions.assertEquals(2, computer.register(Register.a));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
