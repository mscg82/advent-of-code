package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record SpiralMemory(int targetValue) {

    public int findDistance() {
        final int level = IntStream.iterate(0, i -> i < targetValue, i -> i + 1) //
                .dropWhile(i -> (2 * i + 1) * (2 * i + 1) <= targetValue) //
                .findFirst() //
                .orElseThrow();
        var position = new Position(level, Math.min(0, 1 - level));
        int value = (2 * (level - 1) + 1) * (2 * (level - 1) + 1) + 1;

        var direction = Direction.UP;

        while (value < targetValue) {
            value++;
            direction = direction.next(position, level);
            position = position.next(direction);
        }

        return position.x() + position.y();
    }

    public int findValue() {
        final Map<Position, Integer> storedValues = new HashMap<>();
        var position = new Position(0, 0);
        int lastValue = 1;
        int level = 0;
        var direction = Direction.UP;
        while (lastValue < targetValue) {
            lastValue = Stream.of( //
                    position.with(p -> p.x(p.x() + 1)), //
                    position.with(p -> {
                        p.x(p.x() + 1);
                        p.y(p.y() + 1);
                    }), //
                    position.with(p -> p.y(p.y() + 1)), //
                    position.with(p -> {
                        p.x(p.x() - 1);
                        p.y(p.y() + 1);
                    }), //
                    position.with(p -> p.x(p.x() - 1)), //
                    position.with(p -> {
                        p.x(p.x() - 1);
                        p.y(p.y() - 1);
                    }), //
                    position.with(p -> p.y(p.y() - 1)), //
                    position.with(p -> {
                        p.x(p.x() + 1);
                        p.y(p.y() - 1);
                    })) //
                    .mapToInt(p -> storedValues.getOrDefault(p, 0))
                    .sum();
            storedValues.put(position, Math.max(1, lastValue));
            if (position.x() == level && position.y() == -level) {
                // switch level
                level++;
                direction = Direction.UP;
                position = position.with(p -> p.x(p.x() + 1));
            } else {
                direction = direction.next(position, level);
                position = position.next(direction);
            }
        }
        return lastValue;
    }

    public static SpiralMemory parseInput(final BufferedReader in) throws IOException {
        return new SpiralMemory(Integer.parseInt(in.readLine()));
    }

    enum Direction {
        UP, LEFT, DOWN, RIGHT;

        public Direction next(final Position position, final int level) {
            return switch (this) {
                case UP -> position.y() == level ? Direction.LEFT : Direction.UP;
                case LEFT -> position.x() == -level ? Direction.DOWN : Direction.LEFT;
                case DOWN -> position.y() == -level ? Direction.RIGHT : Direction.DOWN;
                case RIGHT -> position.x() == level ? Direction.UP : Direction.RIGHT;
            };
        }
    }

    @RecordBuilder
    static record Position(int x, int y) implements SpiralMemoryPositionBuilder.With {

        public Position next(final Direction direction) {
            return switch (direction) {
                case UP -> this.with(p -> p.y(p.y() + 1));
                case LEFT -> this.with(p -> p.x(p.x() - 1));
                case DOWN -> this.with(p -> p.y(p.y() - 1));
                case RIGHT -> this.with(p -> p.x(p.x() + 1));
            };
        }

    }

}
