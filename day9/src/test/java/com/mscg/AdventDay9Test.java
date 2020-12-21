package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.OptionalLong;

public class AdventDay9Test {

    @Test
    public void testValidity() throws Exception {
        try (BufferedReader in = readInput()) {
            XMASReader xmasReader = XMASReader.parseInput(in, 5);
            OptionalLong invalidValue = xmasReader.getFirstInvalidValue();
            Assertions.assertEquals(OptionalLong.of(127L), invalidValue);
        }
    }

    @Test
    public void testBreakingSequence() throws Exception {
        try (BufferedReader in = readInput()) {
            XMASReader xmasReader = XMASReader.parseInput(in, 5);
            Optional<long[]> breakingSequence = xmasReader.getBreakingSequence();
            Assertions.assertTrue(breakingSequence.isPresent());
            Assertions.assertArrayEquals(new long[] { 15, 25, 47, 40 }, breakingSequence.get());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
