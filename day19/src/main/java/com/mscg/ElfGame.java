package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

public record ElfGame(int elfs) {

    public int playGame() {
        final int[] sits = new int[elfs];
        for (int i = 0; i < elfs; i++) {
            sits[i] = (i + 1) % elfs;
        }

        int current = 0;
        while (sits[current] != current) {
            sits[current] = sits[sits[current]];
            current = sits[current];
        }

        return current + 1;
    }

    public static ElfGame parseInput(final BufferedReader in) throws IOException {
        return new ElfGame(Integer.parseInt(in.readLine()));
    }

}
