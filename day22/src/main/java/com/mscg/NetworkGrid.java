package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.soabase.recordbuilder.core.RecordBuilder;

public record NetworkGrid(List<List<Status>> nodes) {

    public long countInfections(final int iterations, final int padding) {
        long infections = 0L;

        final int gridSize = nodes.size() + 2 * padding;
        final List<List<Status>> grid = IntStream.range(0, gridSize) //
                .mapToObj(__ -> {
                    final Status[] statuses = new Status[gridSize];
                    Arrays.fill(statuses, Status.CLEAN);
                    return Arrays.asList(statuses);
                }) //
                .toList();
        for (int i = 0, l = nodes.size(); i < l; i++) {
            for (int j = 0; j < l; j++) {
                grid.get(padding + i).set(padding + j, nodes.get(i).get(j));
            }
        }

        Virus virus = new Virus(gridSize / 2, gridSize / 2, Direction.UP);
        for (int i = 0; i < iterations; i++) {
            final var status = grid.get(virus.x()).get(virus.y());
            final var newDirection = switch (status) {
                case CLEAN -> {
                    infections++;
                    grid.get(virus.x()).set(virus.y(), Status.INFECTED);
                    yield switch (virus.direction()) {
                        case UP -> Direction.LEFT;
                        case RIGHT -> Direction.UP;
                        case DOWN -> Direction.RIGHT;
                        case LEFT -> Direction.DOWN;
                    };
                }
                case INFECTED -> {
                    grid.get(virus.x()).set(virus.y(), Status.CLEAN);
                    yield switch (virus.direction()) {
                        case UP -> Direction.RIGHT;
                        case RIGHT -> Direction.DOWN;
                        case DOWN -> Direction.LEFT;
                        case LEFT -> Direction.UP;
                    };
                }
            };

            virus = virus.with(v -> {
                v.direction(newDirection);
                switch (newDirection) {
                    case UP -> v.x(v.x() - 1);
                    case RIGHT -> v.y(v.y() + 1);
                    case DOWN -> v.x(v.x() + 1);
                    case LEFT -> v.y(v.y() - 1);
                }
            });
        }

        return infections;
    }

    @Override
    public String toString() {
        return nodes.stream() //
                .map(row -> row.stream().map(Status::toString).collect(Collectors.joining())) //
                .collect(Collectors.joining("\n"));
    }

    public static NetworkGrid parseInput(final BufferedReader in) throws IOException {
        try {
            final List<List<Status>> nodes = in.lines() //
                    .map(line -> line.chars() //
                            .mapToObj(c -> Status.fromCharacter((char) c)) //
                            .toList()) //
                    .toList();
            return new NetworkGrid(nodes);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public enum Status {
        CLEAN, INFECTED;

        @Override
        public String toString() {
            return switch (this) {
                case CLEAN -> ".";
                case INFECTED -> "#";
            };
        }

        public static Status fromCharacter(final char c) {
            return switch (c) {
                case '.' -> CLEAN;
                case '#' -> INFECTED;
                default -> throw new IllegalArgumentException("Unsupported infection state " + c);
            };
        }
    }

    enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    @RecordBuilder
    record Virus(int x, int y, Direction direction) implements NetworkGridVirusBuilder.With {
    }
}
