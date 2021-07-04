package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record PacketRouter(List<List<Character>> routes) {

    public String run() {
        var pos = findStartPosition().orElseThrow();
        var dir = Direction.DOWN;

        final StringBuilder path = new StringBuilder();
        do {
            final char current = routes.get(pos.x()).get(pos.y());
            if (current != '-' && current != '|' && current != '+') {
                path.append(current);
            }
            if (current == '+') {
                final var curPos = pos;
                record DirAndPos(Direction dir, Position pos) {
                }
                dir = dir.turn().stream() //
                        .map(d -> new DirAndPos(d, d.next(curPos))) //
                        .filter(dp -> dp.pos().isValid(routes)) //
                        .filter(dp -> {
                            final char next = routes.get(dp.pos().x()).get(dp.pos().y());
                            return switch (dp.dir()) {
                                case UP, DOWN -> next != '-';
                                case LEFT, RIGHT -> next != '|';
                            };
                        }) //
                        .findFirst() //
                        .map(DirAndPos::dir)
                        .orElseThrow();

            }

            pos = dir.next(pos);
        }
        while (pos.isValid(routes));

        return path.toString();
    }

    private Optional<Position> findStartPosition() {
        Position pos = null;
        final List<Character> firstRow = routes.get(0);
        for (int i = 0, cols = firstRow.size(); i < cols; i++) {
            if (firstRow.get(i) != ' ') {
                pos = new Position(0, i);
            }
        }
        return Optional.ofNullable(pos);
    }

    private enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public List<Direction> turn() {
            return switch (this) {
                case UP, DOWN -> List.of(RIGHT, LEFT);
                case RIGHT, LEFT -> List.of(UP, DOWN);
            };
        }

        public Position next(final Position pos) {
            return switch (this) {
                case UP -> new Position(pos.x() - 1, pos.y());
                case RIGHT -> new Position(pos.x(), pos.y() + 1);
                case DOWN -> new Position(pos.x() + 1, pos.y());
                case LEFT -> new Position(pos.x(), pos.y() - 1);
            };
        }
    }

    private static record Position(int x, int y) {

        public boolean isValid(final List<List<Character>> routes) {
            final int rows = routes.size();
            final int cols = routes.get(0).size();
            return x >= 0 && x < rows && y >= 0 && y < cols && routes.get(x).get(y) != ' ';
        }

    }

    public static PacketRouter parseInput(final BufferedReader in) throws IOException {
        try {
            final List<String> lines = in.lines().toList();
            final int cols = lines.stream() //
                    .mapToInt(String::length) //
                    .max() //
                    .orElseThrow();
            final List<List<Character>> routes = lines.stream() //
                    .map(line -> line.chars().mapToObj(i -> (char) i).collect(Collectors.toCollection(ArrayList::new))) //
                    .map(row -> {
                        while (row.size() < cols) {
                            row.add(' ');
                        }
                        return List.copyOf(row);
                    }) //
                    .toList();
            return new PacketRouter(routes);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

}
