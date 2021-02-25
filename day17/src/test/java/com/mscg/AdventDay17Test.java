package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay17Test {

    @Test
    public void testParse() throws Exception {
        var containers = ContainerList.parseInput(readInput());
        Assertions.assertEquals(25, containers.getTarget());
        Assertions.assertArrayEquals(new int[] { 20, 15, 10, 5, 5 }, containers.getContainers());
    }

    @Test
    public void testCountCombinations() throws Exception {
        var containers = ContainerList.parseInput(readInput());
        Assertions.assertEquals(4, containers.computeCombinations());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
