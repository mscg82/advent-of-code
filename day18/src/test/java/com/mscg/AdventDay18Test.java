package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay18Test {

    @Test
    public void testGetFrequency() throws Exception {
        try (BufferedReader in = readInput()) {
            final var cpu = DuettoCPU.parseInput(in, false);
            Assertions.assertEquals(4, cpu.retrieveSound());
        }
    }

    @Test
    public void testDuetto() throws Exception {
        try (BufferedReader in = readInput2()) {
            final var cpu = DuettoCPU.parseInput(in, true);
            Assertions.assertEquals(3, cpu.runDuetto());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

    private BufferedReader readInput2() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input2.txt"), StandardCharsets.UTF_8));
    }

}
