package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.IntStream;

public record SpiralMemory(int targetValue) {

    public int findDistance() {
        final int level = IntStream.iterate(0, i -> i < targetValue, i -> i + 1) //
                .dropWhile(i -> (2 * i + 1) * (2 * i + 1) <= targetValue) //
                .findFirst() //
                .orElseThrow();
        var position = new Position(level, Math.min(0, 1 - level));
        int value = (2 * (level - 1) + 1) * (2 * (level - 1) + 1) + 1;

        enum Direction {
            UP, LEFT, DOWN, RIGHT;
        }

        var direction = Direction.UP;

        while (value < targetValue) {
            value++;
            direction = switch (direction) {
                case UP -> position.y() == level ? Direction.LEFT : Direction.UP;
                case LEFT -> position.x() == -level ? Direction.DOWN : Direction.LEFT;
                case DOWN -> position.y() == -level ? Direction.RIGHT : Direction.DOWN;
                case RIGHT -> position.x() == level ? Direction.UP : Direction.RIGHT;
            };
            position = switch (direction) {
                case UP -> position.with(p -> p.y(p.y() + 1));
                case LEFT -> position.with(p -> p.x(p.x() - 1));
                case DOWN -> position.with(p -> p.y(p.y() - 1));
                case RIGHT -> position.with(p -> p.x(p.x() + 1));
            };
        }

        return position.x() + position.y();
    }

    public static SpiralMemory parseInput(final BufferedReader in) throws IOException {
        return new SpiralMemory(Integer.parseInt(in.readLine()));
    }

    @RecordBuilder
    static record Position(int x, int y) implements SpiralMemoryPositionBuilder.With {

    }

}
