package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay5Test {

    @Test
    public void testReduce() throws Exception {
        try (var in = readInput()) {
            final var analyzer = MoleculeAnalyzer.parseInput(in);
            Assertions.assertEquals("dabCBAcaDA", analyzer.reduce());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
