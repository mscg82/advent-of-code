package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MemoryBank(List<Integer> blocks) {

    public IndexSize redistributeUntilLoop() {
        final var blocks = new ArrayList<>(this.blocks);
        final int size = blocks.size();
        final Map<List<Integer>, Integer> states = new HashMap<>();
        while (!states.containsKey(blocks)) {
            states.put(List.copyOf(blocks), states.size());
            int index = findMaxIndex(blocks);
            int value = blocks.get(index);
            blocks.set(index, 0);
            do {
                index = (index + 1) % size;
                blocks.set(index, blocks.get(index) + 1);
                value--;
            }
            while (value != 0);
        }
        return new IndexSize(states.size(), states.size() - states.get(blocks));
    }

    private int findMaxIndex(final List<Integer> blocks) {
        int max = blocks.get(0);
        int index = 0;
        for (int i = 0, l = blocks.size(); i < l; i++) {
            if (blocks.get(i) > max) {
                max = blocks.get(i);
                index = i;
            }
        }
        return index;
    }

    public static MemoryBank parseInput(final BufferedReader in) throws IOException {
        final String line = in.readLine();
        final List<Integer> blocks = Arrays.stream(line.split("\\s+")) //
                .map(Integer::parseInt) //
                .toList();
        return new MemoryBank(blocks);
    }

    public static record IndexSize(int index, int size) {
    }

}
