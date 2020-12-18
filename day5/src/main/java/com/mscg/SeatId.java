package com.mscg;

import java.util.BitSet;
import java.util.Optional;

public record SeatId(int row, int column) {

    public int computeId() {
        return row * 8 + column;
    }

    public static Optional<SeatId> fromString(String s) {
        if (s.length() != 10) {
            return Optional.empty();
        }
        final var bitSet = new BitSet(10);
        for (int i = 0; i < 10; i++) {
            char c = s.charAt(i);
            if (!isValidChar(i, c)) {
                return Optional.empty();
            }
            switch (c) {
                case 'F', 'L' -> bitSet.clear(9 - i);
                case 'B', 'R' -> bitSet.set(9 - i);
            }
        }
        int value = (int) bitSet.toLongArray()[0];
        return Optional.of(new SeatId(value / 8, value % 8));
    }

    private static boolean isValidChar(int i, char c) {
        if (i < 7 && (c == 'F' || c == 'B')) {
            return true;
        }
        return i >= 7 && (c == 'L' || c == 'R');
    }
}
