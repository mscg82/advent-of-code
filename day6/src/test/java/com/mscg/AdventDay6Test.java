package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay6Test {

    @Test
    public void testDescramble1() throws Exception {
        try (BufferedReader in = readInput()) {
            var descrambler = Descrambler.parseInput(in);
            Assertions.assertEquals("easter", descrambler.clean1());
        }
    }

    @Test
    public void testDescramble2() throws Exception {
        try (BufferedReader in = readInput()) {
            var descrambler = Descrambler.parseInput(in);
            Assertions.assertEquals("advent", descrambler.clean2());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
