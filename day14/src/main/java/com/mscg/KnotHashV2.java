package com.mscg;

import java.util.Optional;
import java.util.stream.IntStream;

public record KnotHashV2(int[] lengths) {

    private int[] hash(final int[] input, final int rounds) {
        final int[] hash = new int[input.length];
        System.arraycopy(input, 0, hash, 0, hash.length);
        int pos = 0;
        int skip = 0;

        for (int round = 0; round < rounds; round++) {
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
        }

        return hash;
    }

    public String hash() {
        return hash(IntStream.rangeClosed(0, 255).toArray());
    }

    public String hash(final int[] input) {
        final int[] sparseHash = hash(input, 64);

        final int[] denseHash = new int[sparseHash.length / 16];
        for (int i = 0; i < denseHash.length; i++) {
            int val = 0;
            final int offset = 16 * i;
            for (int j = offset; j < offset + 16 && j < sparseHash.length; j++) {
                val ^= sparseHash[j];
            }
            denseHash[i] = val;
        }

        final StringBuilder result = new StringBuilder(32);
        for (final int val : denseHash) {
            final String hex = Integer.toHexString(val);
            if (hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }

        return result.toString();
    }

    public static KnotHashV2 from(final String input) {
        final int[] lengths;

        final byte[] bytes = Optional.ofNullable(input).map(String::getBytes).orElse(new byte[0]);
        lengths = new int[bytes.length + 5];
        for (int i = 0; i < bytes.length; i++) {
            lengths[i] = bytes[i];
        }
        System.arraycopy(new int[] { 17, 31, 73, 47, 23 }, 0, lengths, bytes.length, 5);

        return new KnotHashV2(lengths);
    }

}
