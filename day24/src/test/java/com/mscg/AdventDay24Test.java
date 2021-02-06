package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.Floor.Direction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay24Test {

    @Test
    public void testParse() {
        {
            var directions = Direction.parseLine("esenee");
            Assertions.assertEquals(List.of(Direction.E, Direction.SE, Direction.NE, Direction.E), directions);
        }
        {
            var directions = Direction.parseLine("esew");
            Assertions.assertEquals(List.of(Direction.E, Direction.SE, Direction.W), directions);
        }
        {
            var directions = Direction.parseLine("nwwswee");
            Assertions.assertEquals(List.of(Direction.NW, Direction.W, Direction.SW, Direction.E, Direction.E), directions);
        }
    }

    @Test
    public void testRun() throws Exception {
        Floor floor = Floor.parseInput(readInput(), 15);
        floor.run();

        Assertions.assertEquals(10L, floor.countBlackTiles());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
