package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public record OnePadGenerator(byte[] seed, int offset) {

    private static final Map<String, byte[]> md5Cache = new ConcurrentHashMap<>();

    public long findNthPad(final int n, final boolean stretched) {
        final long start = System.currentTimeMillis();
        long index = 0;
        int padsFound = 0;
        while (padsFound != n) {
            final var md5 = generatePad(index, stretched);
            final char c = has3InARow(md5);
            if (c != '\0') {
                final var finalIndex = index;
                final boolean isKey = IntStream.rangeClosed(1, 1000) //
                        .parallel() //
                        .mapToObj(i -> generatePad(finalIndex + i, stretched)) //
                        .anyMatch(nextPad -> has5InARow(nextPad, c));
                if (isKey) {
                    padsFound++;
                    System.out.println("Found pad: " + padsFound + " " + index + ", elapsed: " + (System.currentTimeMillis() - start) + "ms");
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
    private String generatePad(final long index, final boolean stretched) {
        var md5 = md5Cache.computeIfAbsent(String.valueOf(index), strIndex -> {
            final byte[] indexBytes = strIndex.getBytes();
            final byte[] seedCopy = Arrays.copyOf(seed, offset + indexBytes.length);
            System.arraycopy(indexBytes, 0, seedCopy, offset, indexBytes.length);
            return DigestUtils.md5(seedCopy);
        });

        if (stretched) {
            final byte[] md5Bytes = new byte[md5.length * 2];
            HexEncoder.encodeHex(md5, 0, md5.length, md5Bytes, 0);
            for (int i = 0; i < 2016; i++) {
                md5 = DigestUtils.md5(md5Bytes);
                HexEncoder.encodeHex(md5, 0, md5.length, md5Bytes, 0);
            }
        }
        return Hex.encodeHexString(md5);
    }

    public static OnePadGenerator parseInput(final BufferedReader in, final int maxPadding) throws IOException {
        final byte[] seedStr = in.readLine().getBytes();
        final byte[] seed = new byte[seedStr.length + maxPadding];
        System.arraycopy(seedStr, 0, seed, 0, seedStr.length);
        return new OnePadGenerator(seed, seedStr.length);
    }

    private static final class HexEncoder {
        private static final byte[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };

        public static void encodeHex(final byte[] data, final int dataOffset, final int dataLen,
                final byte[] out, final int outOffset) {
            // two characters form the hex value.
            for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
                out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
                out[j++] = DIGITS_LOWER[0x0F & data[i]];
            }
        }
    }

}
