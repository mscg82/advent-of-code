package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.LongStream;

public record PasswordAnalyzer(long start, long end) {

    public static PasswordAnalyzer parseInput(final BufferedReader in) throws IOException {
        final String[] parts = in.readLine().split("-");
        return new PasswordAnalyzer(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
    }

    private static boolean hasAtLeastOneExactDouble(final int[] digits) {
        for (int i = 0; i < digits.length - 1; i++) {
            final int curDigit = digits[i];
            int j = i + 1;
            while (j < digits.length && digits[j] == curDigit) {
                j++;
            }
            if (j - i == 2) {
                return true;
            }
            i = j - 1;
        }
        return false;
    }

    private static boolean hasDoubleDigits(final int[] digits) {
        for (int i = 0; i < digits.length - 1; i++) {
            if (digits[i] == digits[i + 1]) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNonDecreasingDigits(final int[] digits) {
        for (int i = 0; i < digits.length - 1; i++) {
            if (digits[i] > digits[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public long countMatchingPasswords() {
        return LongStream.rangeClosed(start, end) //
                .mapToObj(v -> String.valueOf(v).chars().map(c -> c - '0').toArray()) //
                .filter(PasswordAnalyzer::hasDoubleDigits) //
                .filter(PasswordAnalyzer::hasNonDecreasingDigits) //
                .count();
    }

    public long countMatchingPasswords2() {
        return LongStream.rangeClosed(start, end) //
                .mapToObj(v -> String.valueOf(v).chars().map(c -> c - '0').toArray()) //
                .filter(PasswordAnalyzer::hasAtLeastOneExactDouble) //
                .filter(PasswordAnalyzer::hasNonDecreasingDigits) //
                .count();
    }

}
