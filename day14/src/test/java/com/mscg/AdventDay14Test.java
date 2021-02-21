package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.Race.Horse;
import com.mscg.Race.Position;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay14Test {

    @Test
    public void testParse() throws Exception {
        var race = Race.parseInput(readInput());
        Assertions.assertEquals(List.of( //
                new Horse("Comet", 14, 10, 127), //
                new Horse("Dancer", 16, 11, 162) //
        ), race.getHorses());
    }

    @Test
    public void testRun() throws Exception {
        var race = Race.parseInput(readInput());

        Assertions.assertEquals(List.of( //
                new Position("Comet", 14), //
                new Position("Dancer", 16) //
        ), race.run(1));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 140), //
                new Position("Dancer", 160) //
        ), race.run(10));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 140), //
                new Position("Dancer", 176) //
        ), race.run(11));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 140), //
                new Position("Dancer", 176) //
        ), race.run(12));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 154), //
                new Position("Dancer", 176) //
        ), race.run(138));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 1120), //
                new Position("Dancer", 1056) //
        ), race.run(1000));
    }

    @Test
    public void testRunWithPoints() throws Exception {
        var race = Race.parseInput(readInput());

        Assertions.assertEquals(List.of( //
                new Position("Comet", 0), //
                new Position("Dancer", 1) //
        ), race.runWithPoints(1));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 1), //
                new Position("Dancer", 139) //
        ), race.runWithPoints(140));

        Assertions.assertEquals(List.of( //
                new Position("Comet", 312), //
                new Position("Dancer", 689) //
        ), race.runWithPoints(1000));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
