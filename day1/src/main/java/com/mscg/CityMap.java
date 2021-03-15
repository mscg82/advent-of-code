package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CityMap {

    private final List<Instruction> instructions;

    public Position run() {
        var position = new Position(Intersection.origin(), Facing.NORTH);
        for (var instruction : instructions) {
            position = position.advance(instruction);
        }
        return position;
    }

    public Optional<Intersection> findHQ() {
        Set<Intersection> visitedIntersections = new HashSet<>();
        var position = new Position(Intersection.origin(), Facing.NORTH);
        visitedIntersections.add(position.intersection());

        for (var instruction : instructions) {
            List<Position> newPositions = position.advanceInSteps(instruction);
            for (var newPosition : newPositions) {
                if (!visitedIntersections.add(newPosition.intersection())) {
                    return Optional.of(newPosition.intersection());
                }
                position = newPosition;
            }
        }

        return Optional.empty();
    }

    public static CityMap parseInput(BufferedReader in) throws IOException {
        List<Instruction> instructions = in.lines() //
                .flatMap(line -> Arrays.stream(line.split(","))) //
                .map(String::trim) //
                .map(Instruction::parse) //
                .collect(Collectors.toUnmodifiableList());

        return new CityMap(instructions);
    }

    public static record Position(Intersection intersection, Facing facing) {

        public Position advance(Instruction instruction) {
            var newFacing = facing.rotate(instruction.direction());
            var newIntersection = switch (newFacing) {
            case NORTH -> new Intersection(intersection.x() + instruction.amount(), intersection.y());
            case EAST -> new Intersection(intersection.x(), intersection.y() + instruction.amount());
            case SOUTH -> new Intersection(intersection.x() - instruction.amount(), intersection.y());
            case WEST -> new Intersection(intersection.x(), intersection.y() - instruction.amount());
            };

            return new Position(newIntersection, newFacing);
        }

        public List<Position> advanceInSteps(Instruction instruction) {
            var newFacing = facing.rotate(instruction.direction());
            return IntStream.rangeClosed(1, instruction.amount()) //
                    .mapToObj(amount -> switch (newFacing) {
                    case NORTH -> new Intersection(intersection.x() + amount, intersection.y());
                    case EAST -> new Intersection(intersection.x(), intersection.y() + amount);
                    case SOUTH -> new Intersection(intersection.x() - amount, intersection.y());
                    case WEST -> new Intersection(intersection.x(), intersection.y() - amount);
                    }) //
                    .map(intersection -> new Position(intersection, newFacing)) //
                    .collect(Collectors.toUnmodifiableList());
        }

    }

    public static record Intersection(int x, int y) {

        public int distance() {
            return Math.abs(x) + Math.abs(y);
        }

        public static Intersection origin() {
            return new Intersection(0, 0);
        }

    }

    public static record Instruction(Direction direction, int amount) {

        public static Instruction parse(String value) {
            return new Instruction(Direction.fromChar(value.charAt(0)), Integer.parseInt(value.substring(1)));
        }

    }

    public enum Facing {
        NORTH, EAST, SOUTH, WEST;

        public Facing rotate(Direction direction) {
            return switch (direction) {
            case RIGHT -> switch (this) {
                case NORTH -> Facing.EAST;
                case EAST -> Facing.SOUTH;
                case SOUTH -> Facing.WEST;
                case WEST -> Facing.NORTH;
                };

            case LEFT -> switch (this) {
                case NORTH -> Facing.WEST;
                case EAST -> Facing.NORTH;
                case SOUTH -> Facing.EAST;
                case WEST -> Facing.SOUTH;
                };
            };
        }
    }

    public enum Direction {
        LEFT, RIGHT;

        @Override
        public String toString() {
            return switch (this) {
            case LEFT -> "L";
            case RIGHT -> "R";
            };
        }

        public static Direction fromChar(char c) {
            return switch (c) {
            case 'L' -> Direction.LEFT;
            case 'R' -> Direction.RIGHT;
            default -> throw new IllegalArgumentException("Invalid direction " + c);
            };
        }
    }

}
