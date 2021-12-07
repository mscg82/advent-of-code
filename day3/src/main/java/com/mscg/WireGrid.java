package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Supplier;
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

    private static <C extends Collection<Position>> C getWirePositions(final List<Instruction> wire, final Supplier<C> collectionSupplier,
                                                                       final PositionFilter positionsFilter) {
        final C wire1Pos = collectionSupplier.get();
        Position lastPosition = Position.CENTRAL_PORT;
        for (final ListIterator<Instruction> iterator = wire.listIterator(); iterator.hasNext(); ) {
            final int index = iterator.nextIndex();
            final Instruction instr = iterator.next();
            final List<Position> newPositions = instr.generatePositions(lastPosition);
            wire1Pos.addAll(positionsFilter.accept(index, newPositions));
            lastPosition = newPositions.get(newPositions.size() - 1);
        }
        return wire1Pos;
    }

    public Position findClosestIntersection() {
        final Set<Position> wire1Pos = getWirePositions(wire1, HashSet::new, PositionFilter.ALL);
        wire1Pos.remove(Position.CENTRAL_PORT);

        final Set<Position> wire2Pos = getWirePositions(wire2, HashSet::new, PositionFilter.ALL);
        wire2Pos.remove(Position.CENTRAL_PORT);

        wire1Pos.retainAll(wire2Pos);

        return wire1Pos.stream() //
                .min(Comparator.comparingLong(Position::distance)) //
                .orElseThrow();
    }

    public int findFastestIntersectionLength() {
        final PositionFilter positionFilter = (idx, positions) -> idx == 0 ? positions : positions.subList(1, positions.size());

        final List<Position> wire1Pos = getWirePositions(wire1, ArrayList::new, positionFilter);

        final List<Position> wire2Pos = getWirePositions(wire2, ArrayList::new, positionFilter);

        final Set<Position> intersections = getWirePositions(wire1, HashSet::new, PositionFilter.ALL);
        intersections.retainAll(getWirePositions(wire2, HashSet::new, PositionFilter.ALL));
        intersections.remove(Position.CENTRAL_PORT);

        return intersections.stream() //
                .mapToInt(intersection -> wire1Pos.indexOf(intersection) + wire2Pos.indexOf(intersection)) //
                .min() //
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

    @FunctionalInterface
    private interface PositionFilter {

        PositionFilter ALL = (__, positions) -> positions;

        List<Position> accept(int index, List<Position> positions);

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
