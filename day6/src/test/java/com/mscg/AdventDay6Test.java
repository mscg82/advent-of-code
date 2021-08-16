package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay6Test {

    @Test
    public void testMaxArea() throws Exception {
        try (var in = readInput()) {
            final var map = LocationMap.parseInput(in);
            Assertions.assertEquals(17, map.findBiggestArea());
        }
    }

    @Test
    public void testAreaWithMaxDistance() throws Exception {
        try (var in = readInput()) {
            final var map = LocationMap.parseInput(in);
            Assertions.assertEquals(16, map.findAreaWithDistanceLessThan(32));
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
