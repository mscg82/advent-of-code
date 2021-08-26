package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay13Test {

    @Test
    public void testFirstClash() throws Exception {
        try (var in = readInput()) {
            final var tracks = Tracks.parseInput(in);
            Assertions.assertEquals("7,3", tracks.findFirstClash().toString());
        }
    }

    @Test
    public void testLastCartPosition() throws Exception {
        try (var in = readInput2()) {
            final var tracks = Tracks.parseInput(in);
            Assertions.assertEquals("6,4", tracks.findLastCartPosition().toString());
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
