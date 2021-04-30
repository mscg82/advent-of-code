package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay15Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var machine = DiscMachine.parseInput(in);

            Assertions.assertEquals(List.of( //
                    new DiscMachine.Disc(5, 4), //
                    new DiscMachine.Disc(2, 1)
            ), machine.discs());
        }
    }

    @Test
    public void testSolution2Discs() {
        final var machine = new DiscMachine(List.of( //
                new DiscMachine.Disc(5, 4), //
                new DiscMachine.Disc(2, 1)
        ));

        Assertions.assertEquals(5, machine.findFirstSolution());
    }

    @Test
    public void testSolution3Discs() {
        final var machine = new DiscMachine(List.of( //
                new DiscMachine.Disc(5, 4), //
                new DiscMachine.Disc(2, 1), //
                new DiscMachine.Disc(3, 2)
        ));

        Assertions.assertEquals(25, machine.findFirstSolution());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
