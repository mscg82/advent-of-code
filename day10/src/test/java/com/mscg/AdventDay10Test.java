package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdventDay10Test {

    @Test
    public void testDifferences() throws Exception {
        try (BufferedReader in = readInput()) {
            AdaptersList adaptersList = AdaptersList.parseInput(in);
            Map<Long, Long> differences = adaptersList.countDifferences();
            long count1 = differences.get(1L);
            long count3 = differences.get(3L);
            Assertions.assertEquals(22L, count1);
            Assertions.assertEquals(10L, count3);
        }
    }

    @Test
    public void testCountArrangements() throws Exception {
        try (BufferedReader in = readInput()) {
            AdaptersList adaptersList = AdaptersList.parseInput(in);
            Assertions.assertEquals(19208L, adaptersList.countArrangments());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
