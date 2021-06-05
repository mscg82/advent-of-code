package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay10Test {

    @Test
    public void testHashSteps() {
        final int[] input = new int[]{ 0, 1, 2, 3, 4 };
        final var hasher = new KnotHash(new int[]{ 3, 4, 1, 5 });

        final int[] hash = hasher.hash(input);
        Assertions.assertArrayEquals(new int[]{ 3, 4, 2, 1, 0 }, hash);
    }

}
