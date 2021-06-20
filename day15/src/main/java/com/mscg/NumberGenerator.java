package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

public record NumberGenerator(int startA, int startB) {

    public long countSimilarValues() {
        long count = 0L;
        int a = startA;
        int b = startB;
        for (int i = 0; i < 40_000_000; i++) {
            a = next(a, 16807);
            b = next(b, 48271);
            if ((short) a == (short) b) {
                count++;
            }
        }

        return count;
    }

    private int next(final int start, final int factor) {
        return (int) (((long) start * (long) factor) % 2147483647L);
    }

    public static NumberGenerator parseInput(final BufferedReader in) throws IOException {
        return new NumberGenerator(Integer.parseInt(in.readLine().substring(24)), Integer.parseInt(in.readLine().substring(24)));
    }

}
