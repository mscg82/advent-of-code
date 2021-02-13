package com.mscg;

import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Miner {
    
    private final String seed;

    public int mine() {
        long before = System.nanoTime();
        try {
            return IntStream.range(0, 100_000_000) //
                    // .parallel() //
                    .filter(i -> {
                        String hash = DigestUtils.md5Hex(seed + i);
                        return "00000".equals(hash.substring(0, 5));
                    }) //
                    .findFirst() //
                    .orElseThrow(() -> new IllegalStateException("Unable to mine in 100.000.000 steps"));
        } finally {
            long elapsed = System.nanoTime() - before;
            System.out.println("mine() took %.3f ms".formatted(elapsed / 1_000_000.0));
        }
    }

    public int mine2() {
        long before = System.nanoTime();
        try {
            return IntStream.range(0, 100_000_000) //
                    // .parallel() //
                    .filter(i -> {
                        String hash = DigestUtils.md5Hex(seed + i);
                        return "000000".equals(hash.substring(0, 6));
                    }) //
                    .findFirst() //
                    .orElseThrow(() -> new IllegalStateException("Unable to mine in 100.000.000 steps"));
        } finally {
            long elapsed = System.nanoTime() - before;
            System.out.println("mine2() took %.3f ms".formatted(elapsed / 1_000_000.0));
        }
    }
}
