package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

public record KnotHash(int[] lengths) {

    public int[] hash() {
        return hash(IntStream.rangeClosed(0, 255).toArray());
    }

    public int[] hash(final int[] input) {
        final int[] hash = new int[input.length];
        System.arraycopy(input, 0, hash, 0, hash.length);
        int pos = 0;
        int skip = 0;

        for (final int length : lengths) {
            if (length != 1) {
                int firstIndex = pos;
                int lastIndex = pos + length - 1;
                while (lastIndex > firstIndex) {
                    final int wrappedFirstIndex = firstIndex % hash.length;
                    final int wrappedLastIndex = (lastIndex + hash.length) % hash.length;
                    final int tmp = hash[wrappedFirstIndex];
                    hash[wrappedFirstIndex] = hash[wrappedLastIndex];
                    hash[wrappedLastIndex] = tmp;
                    firstIndex++;
                    lastIndex--;
                }
            }
            pos += (length + skip) % hash.length;
            skip++;
        }

        return hash;
    }

    public static KnotHash parseInput(final BufferedReader in) throws IOException {
        final int[] lengths = Arrays.stream(in.readLine().split(",")) //
                .mapToInt(Integer::parseInt) //
                .toArray();
        return new KnotHash(lengths);
    }

}
