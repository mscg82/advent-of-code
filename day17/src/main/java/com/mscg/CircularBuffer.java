package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public record CircularBuffer(int steps) {

    public static CircularBuffer parseInput(final BufferedReader in) throws IOException {
        return new CircularBuffer(Integer.parseInt(in.readLine()));
    }

    public int findShortcircuit() {
        return findShortcircuit(2017);
    }

    public int findShortcircuit(final int repetitions) {
        final ArrayList<Integer> buffer = new ArrayList<>(repetitions);
        buffer.add(0);

        int position = 0;
        for (int i = 1; i <= repetitions; i++) {
            final int newPosition = (position + steps) % buffer.size();
            buffer.add((newPosition + 1) % buffer.size(), i);
            position = (newPosition + 1) % buffer.size();
        }

        return buffer.get((position + 1) % buffer.size());
    }

    public int findShortcircuit2(final int repetitions) {
        int value = -1;
        int position = 0;
        for (int i = 1; i < repetitions; i++) {
            position = (position + steps) % i + 1;
            if (position == 1) {
                value = i;
            }
        }
        return value;
    }

}
