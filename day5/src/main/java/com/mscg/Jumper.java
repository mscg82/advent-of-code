package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public record Jumper(List<Integer> jumps) {

    public int stepsToEscape() {
        final List<Integer> jumps = new ArrayList<>(this.jumps);

        int steps = 0;
        int ic = 0;
        while (ic < jumps.size()) {
            final int delta = jumps.get(ic);
            jumps.set(ic, delta + 1);
            ic += delta;
            steps++;
        }

        return steps;
    }

    public int stepsToEscape2() {
        final List<Integer> jumps = new ArrayList<>(this.jumps);

        int steps = 0;
        int ic = 0;
        while (ic < jumps.size()) {
            final int delta = jumps.get(ic);
            jumps.set(ic, delta >= 3 ? delta - 1 : delta + 1);
            ic += delta;
            steps++;
        }

        return steps;
    }

    public static Jumper parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Integer> jumps = in.lines() //
                    .map(Integer::parseInt) //
                    .toList();
            return new Jumper(jumps);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

}
