package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

import lombok.AllArgsConstructor;

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

    public int playVariant() {
        @AllArgsConstructor
        class Elf {
            int prev;
            int next;

            @Override
            public String toString() {
                return "(" + prev + ", " + next + ")";
            }
        }

        final Elf[] sits = new Elf[elfs];
        for (int i = 0; i < elfs; i++) {
            sits[i] = new Elf((i - 1 + elfs) % elfs, (i + 1) % elfs);
        }

        int current = 0;
        int toDelete = elfs / 2;
        int size = elfs;
        while (sits[current].next != current) {
            final Elf deleted = sits[toDelete];
            sits[deleted.prev].next = deleted.next;
            sits[deleted.next].prev = deleted.prev;
            toDelete = deleted.next;
            if (size % 2 != 0) {
                toDelete = sits[toDelete].next;
            }
            size--;
            current = sits[current].next;
        }

        return current + 1;
    }

    public static ElfGame parseInput(final BufferedReader in) throws IOException {
        return new ElfGame(Integer.parseInt(in.readLine()));
    }

}
