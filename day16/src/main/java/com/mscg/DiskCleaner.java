package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

public record DiskCleaner(String seed) {

    public String cleanDisk(final int size) {
        String data = seed;
        while (data.length() < size) {
            data = dragonCurve(data);
        }
        if (data.length() > size) {
            data = data.substring(0, size);
        }
        return checksum(data);
    }

    public static String checksum(String input) {
        if (input.length() % 2 != 0) {
            throw new IllegalArgumentException("Only strings with even length can be checksummed");
        }

        final StringBuilder checksum = new StringBuilder(input.length() / 2);
        while (input.length() % 2 == 0) {
            for (int i = 0, l = input.length(); i < l; i += 2) {
                checksum.append(switch (input.substring(i, i + 2)) {
                    case "00", "11" -> "1";
                    case "01", "10" -> "0";
                    default -> throw new IllegalArgumentException("Unsupported characters in input " + input + " at indexes " + i + " and " + (i + 1));
                });
            }
            input = checksum.toString();
            checksum.setLength(0);
        }
        return input;
    }

    public static String dragonCurve(final String a) {
        final StringBuilder b = new StringBuilder(2 * a.length() + 1);
        b.append(a);
        b.append("0");
        b.setLength(b.capacity());
        for (int i = 0, l = a.length(); i < l; i++) {
            switch (a.charAt(i)) {
                case '0' -> b.setCharAt(2 * l - i, '1');
                case '1' -> b.setCharAt(2 * l - i, '0');
                default -> throw new IllegalArgumentException("Unsupported character in input " + a + " at index " + i);
            }
        }
        return b.toString();
    }

    public static DiskCleaner parseInput(final BufferedReader in) throws IOException {
        return new DiskCleaner(in.readLine());
    }

}
