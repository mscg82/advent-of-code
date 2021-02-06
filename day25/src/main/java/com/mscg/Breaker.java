package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Breaker {

    private final long publicKey1;
    private final long publicKey2;

    public LoopSizes computeLoopSizes() {
        return new LoopSizes(bruteForceLoopSize(7L, publicKey1), bruteForceLoopSize(7L, publicKey2));
    }

    private long bruteForceLoopSize(long initialSubject, long key) {
        long loopSize = 1L;
        long generatedKey = 1L;
        while (loopSize < 100_000_000L) {
            generatedKey = generateKey(generatedKey * initialSubject, 1L);
            if (generatedKey == key) {
                return loopSize;
            }
            loopSize++;
        }
        throw new IllegalStateException("Can't brute force key " + key + " with 100.000.000 iterations");
    }

    public static long generateKey(long subject, long loopSize) {
        long key = 1L;
        for (long i = 0; i < loopSize; i++) {
            key = (key * subject) % 20201227;
        }
        return key;
    }

    public static Breaker parseInput(BufferedReader in) throws IOException {
        return new Breaker(Long.parseLong(in.readLine()), Long.parseLong(in.readLine()));
    }

    public static record LoopSizes(long loopSize1, long loopSize2) {
    }
}