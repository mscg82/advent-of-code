package com.mscg;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

public record OnePadGenerator(byte[] seed, int offset) {

    public long findNthPad(final int n) {
        long index = 0;
        int padsFound = 0;
        while (padsFound != n) {
            final var md5 = md5(index);
            final char c = has3InARow(md5);
            if (c != '\0') {
                for (int i = 1; i <= 1000; i++) {
                    final var nextMd5 = md5(index + i);
                    if (has5InARow(nextMd5, c)) {
                        padsFound++;
                        break;
                    }
                }
            }
            index++;
        }
        return index - 1;
    }

    private char has3InARow(final String value) {
        for (int i = 0, l = value.length(); i < l - 2; i++) {
            final char c1 = value.charAt(i);
            final char c2 = value.charAt(i + 1);
            final char c3 = value.charAt(i + 2);
            if (c1 == c2 && c2 == c3) {
                return c1;
            }
        }
        return '\0';
    }

    private boolean has5InARow(final String value, final char c) {
        for (int i = 0, l = value.length(); i < l - 4; i++) {
            final char c1 = value.charAt(i);
            final char c2 = value.charAt(i + 1);
            final char c3 = value.charAt(i + 2);
            final char c4 = value.charAt(i + 3);
            final char c5 = value.charAt(i + 4);
            if (c == c1 && c1 == c2 && c2 == c3 && c3 == c4 && c4 == c5) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    private String md5(final long index) {
        final byte[] indexBytes = String.valueOf(index).getBytes();
        System.arraycopy(indexBytes, 0, seed, offset, indexBytes.length);
        return DigestUtils.md5Hex(new ByteArrayInputStream(seed, 0, offset + indexBytes.length));
    }

    public static OnePadGenerator parseInput(final BufferedReader in, final int maxPadding) throws IOException {
        final byte[] seedStr = in.readLine().getBytes();
        final byte[] seed = new byte[seedStr.length + maxPadding];
        System.arraycopy(seedStr, 0, seed, 0, seedStr.length);
        return new OnePadGenerator(seed, seedStr.length);
    }

}
