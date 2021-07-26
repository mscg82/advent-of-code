package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay24Test {

    @Test
    public void testStrongestBridge() throws Exception {
        try (BufferedReader in = readInput()) {
            final var bridge = Bridge.parseInput(in);
            Assertions.assertEquals(31, bridge.findStrongestBridge());
        }
    }

    @Test
    public void testStrongestAndLongestBridge() throws Exception {
        try (BufferedReader in = readInput()) {
            final var bridge = Bridge.parseInput(in);
            Assertions.assertEquals(19, bridge.findStrongestAndLongestBridge());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
