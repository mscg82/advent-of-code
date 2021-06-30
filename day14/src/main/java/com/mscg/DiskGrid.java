package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;
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

    public int countRegions() {
        final boolean[][] grid = new boolean[128][128];

        {
            int i = 0;
            for (final var it = initialStatus().iterator(); it.hasNext(); ) {
                final var bitset = it.next();
                for (int j = 0, l = bitset.length(); j < l; j++) {
                    grid[i][j] = bitset.get(j);
                }
                i++;
            }
        }

        int regions = 0;
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                if (grid[i][j]) {
                    regions++;
                    unmarkRegion(grid, i, j);
                }
            }
        }

        return regions;
    }

    private static void unmarkRegion(final boolean[][] grid, final int i, final int j) {
        record Position(int i, int j) {
        }

        final Queue<Position> queue = new LinkedList<>();
        queue.add(new Position(i, j));

        while (!queue.isEmpty()) {
            final var pos = queue.remove();
            grid[pos.i][pos.j] = false;
            final Stream<Position> adjacentCells = Stream.of( //
                    new Position(pos.i - 1, pos.j), //
                    new Position(pos.i + 1, pos.j), //
                    new Position(pos.i, pos.j - 1), //
                    new Position(pos.i, pos.j + 1));
            adjacentCells //
                    .filter(p -> p.i >= 0 && p.i < 128 && p.j >= 0 && p.j < 128) //
                    .filter(p -> grid[p.i][p.j])
                    .forEach(queue::add);
        }
    }

    private static BitSet toBitSet(final String value) {
        final byte[] bytes = new byte[16];

        for (int i = 0, l = value.length(); i < l; i += 2) {
            final byte bh = charToByte(value.charAt(i));
            final byte bl = charToByte(value.charAt(i + 1));
            bytes[15 - (i / 2)] = (byte) ((bh << 4) + bl);
        }

        return BitSet.valueOf(bytes);
    }

    private static byte charToByte(final char c) {
        return (byte) switch (c) {
            case '0' -> 0;
            case '1' -> 1;
            case '2' -> 2;
            case '3' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '7' -> 7;
            case '8' -> 8;
            case '9' -> 9;
            case 'a' -> 10;
            case 'b' -> 11;
            case 'c' -> 12;
            case 'd' -> 13;
            case 'e' -> 14;
            case 'f' -> 15;
            default -> throw new IllegalStateException("Unsupported value " + c);
        };
    }

    public static DiskGrid parseInput(final BufferedReader in) throws IOException {
        return new DiskGrid(in.readLine());
    }

}