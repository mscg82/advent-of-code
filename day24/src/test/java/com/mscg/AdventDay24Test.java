package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
            Assertions.assertEquals(List.of(Direction.NW, Direction.W, Direction.SW, Direction.E, Direction.E),
                    directions);
        }
    }

    @Test
    public void testRun() throws Exception {
        Floor floor = Floor.parseInput(readInput(), 11);
        floor.run();

        Assertions.assertEquals(10L, floor.countBlackTiles());
    }

    @Test
    public void testClone() throws Exception {
        {
            Floor floor = Floor.parseInput(readInput(), 11);
            floor.run();

            Floor clonedFloor = floor.clone();
            Assertions.assertEquals(10L, clonedFloor.countBlackTiles());
        }
        {
            Floor floor = Floor.parseInput(readInput(), 11);

            Floor clonedFloor = floor.clone();
            clonedFloor.run();

            Assertions.assertEquals(10L, clonedFloor.countBlackTiles());
        }
    }

    @Test
    public void testEvolve() throws Exception {
        Floor floor = Floor.parseInput(readInput(), 111);
        floor.run();

        long[] blackTiles = Stream.iterate(floor, Floor::evolve) //
                .skip(1) //
                .limit(100) //
                .mapToLong(Floor::countBlackTiles) //
                .toArray();

        Assertions.assertArrayEquals(new long[] { 15L, 12L, 25L, 14L, 23L, 28L, 41L, 37L, 49L, 37L },
                Arrays.stream(blackTiles).limit(10).toArray());

        Assertions.assertEquals(2208L, blackTiles[blackTiles.length - 1]);
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
