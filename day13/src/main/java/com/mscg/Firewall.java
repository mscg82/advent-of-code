package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class Firewall {

    private final Map<Integer, Integer> levelToDepth;

    public int computeDelay() {
        return IntStream.iterate(0, d -> d + 1) //
                .filter(d -> computeCatches(d).size() == 0) //
                .findFirst() //
                .orElseThrow();
    }

    public long computeSeverity(final int delay) {
        return computeCatches(delay).stream() //
                .mapToInt(pos -> pos * levelToDepth.get(pos)) //
                .sum();
    }

    private List<Integer> computeCatches(final int delay) {
        enum Direction {
            UP, DOWN
        }
        record Scan(int position, Direction direction) {
        }

        final List<Integer> catches = new ArrayList<>(levelToDepth.size());

        int packetPosition = -1;
        final int maxLevel = levelToDepth.keySet().stream().mapToInt(Integer::intValue).max().orElseThrow();
        final Map<Integer, Scan> levelToPosition = levelToDepth.keySet().stream() //
                .collect(Collectors.toMap(k -> k, k -> {
                    final int depth = levelToDepth.get(k);
                    final int fullLength = (depth - 1) * 2;
                    int position = delay % fullLength;
                    final Direction direction;
                    if (position >= depth) {
                        position = (depth - 1) * 2 - position;
                        direction = Direction.UP;
                    } else {
                        direction = Direction.DOWN;
                    }
                    return new Scan(position, direction);
                }));
        while (true) {
            packetPosition++;

            if (packetPosition > maxLevel) {
                break;
            }

            if (Optional.ofNullable(levelToPosition.get(packetPosition)).map(Scan::position).orElse(-1) == 0) {
                catches.add(packetPosition);
            }

            levelToPosition.replaceAll((level, scan) -> switch (scan.direction()) {
                case DOWN -> {
                    if (scan.position() == levelToDepth.get(level) - 1) {
                        yield new Scan(scan.position() - 1, Direction.UP);
                    } else {
                        yield new Scan(scan.position() + 1, Direction.DOWN);
                    }
                }
                case UP -> {
                    if (scan.position() == 0) {
                        yield new Scan(1, Direction.DOWN);
                    } else {
                        yield new Scan(scan.position() - 1, Direction.UP);
                    }
                }
            });
        }

        return catches;
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
