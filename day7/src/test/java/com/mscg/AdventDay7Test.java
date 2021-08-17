package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AdventDay7Test {

    @Test
    public void testParse() throws Exception {
        try (var in = readInput()) {
            final var assembler = SleighAssembler.parseInput(in);
            Assertions.assertEquals(Map.of( //
                    "A", List.of("C"), //
                    "B", List.of("A"), //
                    "D", List.of("A"), //
                    "E", List.of("B", "D", "F"), //
                    "F", List.of("C") //
            ), assembler.stepToRequired());
        }
    }

    @Test
    public void testSequence() throws Exception {
        try (var in = readInput()) {
            final var assembler = SleighAssembler.parseInput(in);
            Assertions.assertEquals("CABDFE", assembler.findSequence());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
