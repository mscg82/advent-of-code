package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay25Test {

    @Test
    public void testParse() throws Exception {
        var wm = WeatherMachine.parseInput(readInput());
        Assertions.assertEquals(2947, wm.getTargetRow());
        Assertions.assertEquals(3029, wm.getTargetCol());
    }

    @Test
    public void testSequenceForCell() {
        {
            int[] sequences = IntStream.rangeClosed(1, 6) //
                    .map(j -> WeatherMachine.getSequenceForCell(1, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 1, 3, 6, 10, 15, 21 }, sequences);
        }
        {
            int[] sequences = IntStream.rangeClosed(1, 6) //
                    .map(j -> WeatherMachine.getSequenceForCell(2, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 2, 5, 9, 14, 20, 27 }, sequences);
        }
        {
            int[] sequences = IntStream.rangeClosed(1, 6) //
                    .map(j -> WeatherMachine.getSequenceForCell(3, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 4, 8, 13, 19, 26, 34 }, sequences);
        }
    }

    @Test
    public void testValueForCell() {
        {
            long[] values = IntStream.rangeClosed(1, 6) //
                    .mapToLong(j -> WeatherMachine.getValueForCell(1, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new long[] { 20151125, 18749137, 17289845, 30943339, 10071777, 33511524 },
                    values);
        }
        {
            long[] values = IntStream.rangeClosed(1, 6) //
                    .mapToLong(j -> WeatherMachine.getValueForCell(2, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new long[] { 31916031, 21629792, 16929656, 7726640, 15514188, 4041754 },
                    values);
        }
        {
            long[] values = IntStream.rangeClosed(1, 6) //
                    .mapToLong(j -> WeatherMachine.getValueForCell(3, j)) //
                    .toArray();
            Assertions.assertArrayEquals(new long[] { 16080970, 8057251, 1601130, 7981243, 11661866, 16474243 },
                    values);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
