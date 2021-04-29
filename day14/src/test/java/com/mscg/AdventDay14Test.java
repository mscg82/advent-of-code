package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay14Test {

    @Test
    public void testFirstIndex() throws Exception {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in, 30);
            final long index = padGenerator.findNthPad(1, false);
            Assertions.assertEquals(39, index);
        }
    }

    @Test
    public void testSecondIndex() throws Exception {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in, 30);
            final long index = padGenerator.findNthPad(2, false);
            Assertions.assertEquals(92, index);
        }
    }

    // @Test
    public void test64thIndex() throws Exception {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in, 30);
            final long index = padGenerator.findNthPad(64, false);
            Assertions.assertEquals(22728, index);
        }
    }

    @Test
    public void testFirstIndexStretched() throws Exception {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in, 30);
            final long index = padGenerator.findNthPad(1, true);
            Assertions.assertEquals(10, index);
        }
    }

    @Test
    public void test64thIndexStretched() throws Exception {
        try (BufferedReader in = readInput()) {
            final var padGenerator = OnePadGenerator.parseInput(in, 30);
            final long index = padGenerator.findNthPad(64, true);
            Assertions.assertEquals(22551, index);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
