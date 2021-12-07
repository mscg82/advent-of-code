package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record WireGrid(List<Instruction> wire1, List<Instruction> wire2) {

    public static WireGrid parseInput(final BufferedReader in) throws IOException {
        final List<Instruction> wire1 = Arrays.stream(in.readLine().split(",")) //
                .map(Instruction::parse) //
                .toList();
        final List<Instruction> wire2 = Arrays.stream(in.readLine().split(",")) //
                .map(Instruction::parse) //
                .toList();
        return new WireGrid(wire1, wire2);
    }

    public Position findClosestIntersection() {
        final Set<Position> wire1Pos = new HashSet<>();
        Position lastPosition = Position.CENTRAL_PORT;
        for (final Instruction instr : wire1) {
            final List<Position> newPositions = instr.generatePositions(lastPosition);
            wire1Pos.addAll(newPositions);
            lastPosition = newPositions.get(newPositions.size() - 1);
        }
        wire1Pos.remove(Position.CENTRAL_PORT);

        final Set<Position> wire2Pos = new HashSet<>();
        lastPosition = Position.CENTRAL_PORT;
        for (final Instruction instr : wire2) {
            final List<Position> newPositions = instr.generatePositions(lastPosition);
            wire2Pos.addAll(newPositions);
            lastPosition = newPositions.get(newPositions.size() - 1);
        }
        wire2Pos.remove(Position.CENTRAL_PORT);

        wire1Pos.retainAll(wire2Pos);

        return wire1Pos.stream() //
                .min(Comparator.comparingLong(Position::distance)) //
                .orElseThrow();
    }

    public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public static Direction from(final char c) {
            return switch (c) {
                case 'U' -> UP;
                case 'R' -> RIGHT;
                case 'D' -> DOWN;
                case 'L' -> LEFT;
                default -> throw new IllegalArgumentException("Unsupported direction " + c);
            };
        }
    }

    public record Instruction(Direction direction, long amount) {

        public static Instruction parse(final String s) {
            return new Instruction(Direction.from(s.charAt(0)), Long.parseLong(s.substring(1)));
        }

        public List<Position> generatePositions(final Position start) {
            final Stream<Position> positions = switch (direction) {
                case UP -> LongStream.rangeClosed(start.y(), start.y() + amount) //
                        .mapToObj(y -> new Position(start.x(), y));
                case RIGHT -> LongStream.rangeClosed(start.x(), start.x() + amount) //
                        .mapToObj(x -> new Position(x, start.y()));
                case DOWN -> LongStream.rangeClosed(-start.y(), -start.y() + amount) //
                        .mapToObj(y -> new Position(start.x(), -y));
                case LEFT -> LongStream.rangeClosed(-start.x(), -start.x() + amount) //
                        .mapToObj(x -> new Position(-x, start.y()));
            };
            return positions.toList();
        }

    }

    public record Position(long x, long y) {

        public static final Position CENTRAL_PORT = new Position(0, 0);

        public long distance() {
            return Math.abs(x) + Math.abs(y);
        }

    }

}
