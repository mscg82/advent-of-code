package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record DiskGrid(String seed) {

    public Stream<BitSet> initialStatus() {
        return IntStream.range(0, 128) //
                .mapToObj(i -> seed + "-" + i) //
                .map(KnotHashV2::from) //
                .map(KnotHashV2::hash) //
                .map(DiskGrid::toBitSet);
    }

    private static BitSet toBitSet(final String value) {
        long hi = 0L;
        for (int i = 0; i < 16; i++) {
            final long v = Long.parseLong(value, i, i + 1, 16);
            hi = (hi << 4) + v;
        }

        long lo = 0L;
        for (int i = 16; i < 32; i++) {
            final long v = Long.parseLong(value, i, i + 1, 16);
            lo = (lo << 4) + v;
        }

        return BitSet.valueOf(new long[] { hi, lo });
    }

    public static DiskGrid parseInput(final BufferedReader in) throws IOException {
        return new DiskGrid(in.readLine());
    }

}
