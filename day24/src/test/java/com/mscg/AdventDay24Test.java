package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("SpellCheckingInspection")
public class AdventDay24Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var airduct = AirductMap.parseInput(in);
            Assertions.assertEquals("""
                    ###########
                    #0.1.....2#
                    #.#######.#
                    #4.......3#
                    ###########""", airduct.toString());
        }
    }

    @Test
    public void testShortestPath() throws Exception {
        try (BufferedReader in = readInput()) {
            final var airduct = AirductMap.parseInput(in);
            Assertions.assertEquals(new AirductMap.Path(List.of('0', '4', '1', '2', '3'), 14), airduct.findShortestPath());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
