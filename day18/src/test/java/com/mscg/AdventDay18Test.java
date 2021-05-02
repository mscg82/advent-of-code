package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay18Test {

    @Test
    public void testGenerate() throws Exception {
        try (BufferedReader in = readInput()) {
            final var floor = Floor.parseInput(in, 10);
            floor.computeTiles();
            Assertions.assertEquals("""
                    .^^.^.^^^^
                    ^^^...^..^
                    ^.^^.^.^^.
                    ..^^...^^^
                    .^^^^.^^.^
                    ^^..^.^^..
                    ^^^^..^^^.
                    ^..^^^^.^^
                    .^^^..^.^^
                    ^^.^^^..^^""", floor.toString());

            Assertions.assertEquals(38L, floor.tiles().stream() //
                    .flatMap(List::stream) //
                    .filter(t -> t == Floor.Tile.SAFE) //
                    .count());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
