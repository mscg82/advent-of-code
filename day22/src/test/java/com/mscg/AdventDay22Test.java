package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay22Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var grid = NetworkGrid.parseInput(in);
            Assertions.assertEquals("""
                    ..#
                    #..
                    ...""", grid.toString());
        }
    }

    @Test
    public void testCountInfections() throws Exception {
        try (BufferedReader in = readInput()) {
            final var grid = NetworkGrid.parseInput(in);
            Assertions.assertEquals(5, grid.countInfections(7, 50));
            Assertions.assertEquals(41, grid.countInfections(70, 50));
            Assertions.assertEquals(5587, grid.countInfections(10_000, 500));
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
