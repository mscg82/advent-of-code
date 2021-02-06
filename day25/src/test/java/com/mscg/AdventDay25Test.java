package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay25Test {

    @Test
    public void testParse() throws Exception {
        var breaker = Breaker.parseInput(readInput());

        Assertions.assertEquals(5764801L, breaker.getPublicKey1());
        Assertions.assertEquals(17807724L, breaker.getPublicKey2());
    }

    @Test
    public void testKeyGeneration() {
        Assertions.assertEquals(5764801L, Breaker.generateKey(7L, 8L));
        Assertions.assertEquals(17807724L, Breaker.generateKey(7L, 11L));

        Assertions.assertEquals(14897079L, Breaker.generateKey(17807724L, 8L));
        Assertions.assertEquals(14897079L, Breaker.generateKey(5764801L, 11L));
    }

    @Test
    public void testCrackLoopSizes() throws Exception {
        var breaker = Breaker.parseInput(readInput());

        Assertions.assertEquals(new Breaker.LoopSizes(8L, 11L), breaker.computeLoopSizes());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
