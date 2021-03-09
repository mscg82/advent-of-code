package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay24Test {

    @Test
    public void testParse() throws Exception {
        var giftsBag = GiftsBag.parseInput(readInput());

        Assertions.assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 7, 8, 9, 10, 11 }, giftsBag.getWeights());
    }

    @Test
    public void testBlockWeights() throws Exception {
        var giftsBag = GiftsBag.parseInput(readInput());

        Assertions.assertEquals(20, giftsBag.getBlockWeight(3));
        Assertions.assertEquals(15, giftsBag.getBlockWeight(4));
    }

    @Test
    public void testQuantumEntanglement() throws Exception {
        var giftsBag = GiftsBag.parseInput(readInput());

        Assertions.assertEquals(99L, giftsBag.findQuantumEntanglement(3));
        Assertions.assertEquals(44L, giftsBag.findQuantumEntanglement(4));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
