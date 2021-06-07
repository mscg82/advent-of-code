package com.mscg;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay10Test {

    @Test
    public void testHashSteps() {
        final int[] input = new int[] { 0, 1, 2, 3, 4 };
        final var hasher = new KnotHash(new int[] { 3, 4, 1, 5 });

        final int[] hash = hasher.hash(input);
        Assertions.assertArrayEquals(new int[] { 3, 4, 2, 1, 0 }, hash);
    }

    @Test
    public void testExtHash() throws Exception {
        {
            final var hasher = KnotHash.parseInput(new BufferedReader(new InputStreamReader(new ByteArrayInputStream("".getBytes()))), true);
            Assertions.assertEquals("a2582a3a0e66e6e86e3812dcb672a272", hasher.extHash());
        }
        {
            final var hasher = KnotHash.parseInput(new BufferedReader(new InputStreamReader(new ByteArrayInputStream("AoC 2017".getBytes()))), true);
            Assertions.assertEquals("33efeb34ea91902bb2f59c9920caa6cd", hasher.extHash());
        }
        {
            final var hasher = KnotHash.parseInput(new BufferedReader(new InputStreamReader(new ByteArrayInputStream("1,2,3".getBytes()))), true);
            Assertions.assertEquals("3efbe78a8d82f29979031a4aa0b16a9d", hasher.extHash());
        }
        {
            final var hasher = KnotHash.parseInput(new BufferedReader(new InputStreamReader(new ByteArrayInputStream("1,2,4".getBytes()))), true);
            Assertions.assertEquals("63960835bcdc130f0b66d7ff4f6a5a8e", hasher.extHash());
        }
    }

}
