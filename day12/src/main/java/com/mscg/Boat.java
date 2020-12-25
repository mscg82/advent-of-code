package com.mscg;

import lombok.Getter;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Boat {

    private Position position;

    private Position orientation;

    private Position waypoint;

    private final List<Instruction> instructions;

    public Boat(List<Instruction> instructions) {
        this.position = new Position(0, 0);
        this.orientation = new Position(0, 1);
        this.waypoint = new Position(1, 10);
        this.instructions = instructions;
    }

    public void execute1() {
        for (var instruction : instructions) {
            switch (instruction.direction) {
                case NORTH -> position = new Position(position.north + instruction.amount, position.east);
                case EAST -> position = new Position(position.north, position.east + instruction.amount);
                case SOUTH -> position = new Position(position.north - instruction.amount, position.east);
                case WEST -> position = new Position(position.north, position.east - instruction.amount);
                case LEFT, RIGTH -> orientation = rotate(this.orientation, instruction);
                case FORWARD -> position = new Position(position.north + (orientation.north * instruction.amount),
                        position.east + (orientation.east * instruction.amount));
            }
        }
    }

    public void execute2() {
        for (var instruction : instructions) {
            switch (instruction.direction) {
                case NORTH -> waypoint = new Position(waypoint.north + instruction.amount, waypoint.east);
                case EAST -> waypoint = new Position(waypoint.north, waypoint.east + instruction.amount);
                case SOUTH -> waypoint = new Position(waypoint.north - instruction.amount, waypoint.east);
                case WEST -> waypoint = new Position(waypoint.north, waypoint.east - instruction.amount);
                case LEFT, RIGTH -> waypoint = rotate(this.waypoint, instruction);
                case FORWARD -> position = new Position(position.north + (waypoint.north * instruction.amount),
                        position.east + (waypoint.east * instruction.amount));
            }
        }
    }

    private static Position rotate(Position initialVector, Instruction instruction) {
        var newVector = initialVector;
        int ticks = instruction.amount() / 90;
        for (int i = 0; i < ticks; i++) {
            newVector = switch (instruction.direction()) {
                case RIGTH -> new Position(-newVector.east(), newVector.north());
                case LEFT -> new Position(newVector.east(), -newVector.north());
                default -> newVector;
            };
        }
        return newVector;
    }

    public static Boat parseInput(BufferedReader in) {
        var instructions = in.lines()
                .map(Instruction::fromString)
                .collect(Collectors.toList());

        return new Boat(instructions);
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST,
        LEFT, RIGTH,
        FORWARD
    }

    public record Instruction(Direction direction, int amount) {

        public static Instruction fromString(String s) {
            Direction direction = switch (s.charAt(0)) {
                case 'N' -> Direction.NORTH;
                case 'E' -> Direction.EAST;
                case 'S' -> Direction.SOUTH;
                case 'W' -> Direction.WEST;
                case 'L' -> Direction.LEFT;
                case 'R' -> Direction.RIGTH;
                case 'F' -> Direction.FORWARD;
                default -> throw new IllegalArgumentException("Illegal instruction " + s);
            };
            int amount = Integer.parseInt(s.substring(1));
            return new Instruction(direction, amount);
        }

    }

    public record Position(int north, int east) {

    }

}
