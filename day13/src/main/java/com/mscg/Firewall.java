package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class Firewall {

    private final Map<Integer, Integer> levelToDepth;

    public int computeDelay() {
        return IntStream.iterate(0, d -> d + 1) //
                .filter(d -> computeCatches(d, true).size() == 0) //
                .findFirst() //
                .orElseThrow();
    }

    public long computeSeverity(final int delay) {
        return computeCatches(delay, false).stream() //
                .mapToInt(pos -> pos * levelToDepth.get(pos)) //
                .sum();
    }

    private List<Integer> computeCatches(final int delay, final boolean stopAtFirstCatch) {
        final List<Integer> catches = new ArrayList<>(levelToDepth.size());
        for (final var entry : levelToDepth.entrySet()) {
            final int level = entry.getKey();
            final int depth = entry.getValue();
            if ((delay + level) % (2 * (depth - 1)) == 0) {
                catches.add(level);
                if (stopAtFirstCatch) {
                    break;
                }
            }
        }
        return List.copyOf(catches);
    }

    public static Firewall parseInput(final BufferedReader in) throws IOException {
        try {
            final Map<Integer, Integer> levelToDepth = in.lines() //
                    .map(line -> line.split(":")) //
                    .collect(Collectors.toUnmodifiableMap(parts -> Integer.parseInt(parts[0].trim()), parts -> Integer.parseInt(parts[1].trim())));
            return new Firewall(levelToDepth);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

}
