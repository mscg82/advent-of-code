package com.mscg;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Miner {
    
    private final String seed;

    public int mine() {
        for (int i = 1; i < 100_000_000; i++) {
            String hash = DigestUtils.md5Hex(seed + i);
            if ("00000".equals(hash.substring(0, 5))) {
                return i;
            }
        }

        throw new IllegalStateException("Unable to mine in 100.000.000 steps");
    }

    public int mine2() {
        for (int i = 1; i < 100_000_000; i++) {
            String hash = DigestUtils.md5Hex(seed + i);
            if ("000000".equals(hash.substring(0, 6))) {
                return i;
            }
        }

        throw new IllegalStateException("Unable to mine in 100.000.000 steps");
    }
}
