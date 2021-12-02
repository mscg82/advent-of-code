package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record Submarine(List<Instruction> instructions) {

    public static Submarine parseInput(BufferedReader in) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .map(Instruction::parse) //
                    .toList();
            return new Submarine(instructions);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public Position execute() {
        return Seq.seq(instructions.stream()) //
                .foldLeft(Position.ORIGIN, Position::move);
    }

    public Position execute2() {
        return Seq.seq(instructions.stream()) //
                .foldLeft(Position.ORIGIN, Position::move2);
    }

    public enum Direction {
        FORWARD, DOWN, UP;

        public static Direction fromString(String value) {
            return switch (value) {
                case "up" -> UP;
                case "down" -> DOWN;
                case "forward" -> FORWARD;
                default -> throw new IllegalArgumentException("Unsupported direction " + value);
            };
        }
    }

    public record Instruction(Direction direction, long amount) {

        public static Instruction parse(String value) {
            final String[] parts = value.split(" ");
            return new Instruction(Direction.fromString(parts[0]), Long.parseLong(parts[1]));
        }

    }

    @RecordBuilder
    public record Position(long horizontal, long depth, long aim) implements SubmarinePositionBuilder.With {

        public static final Position ORIGIN = new Position(0, 0, 0);

        public Position move(Instruction instruction) {
            return this.with(pos -> {
                switch (instruction.direction()) {
                    case UP -> pos.depth(pos.depth() - instruction.amount());
                    case DOWN -> pos.depth(pos.depth() + instruction.amount());
                    case FORWARD -> pos.horizontal(pos.horizontal() + instruction.amount());
                }
            });
        }

        public Position move2(Instruction instruction) {
            return this.with(pos -> {
                switch (instruction.direction()) {
                    case UP -> pos.aim(pos.aim() - instruction.amount());
                    case DOWN -> pos.aim(pos.aim() + instruction.amount());
                    case FORWARD -> {
                        pos.horizontal(pos.horizontal() + instruction.amount());
                        pos.depth(pos.depth() + pos.aim() * instruction.amount());
                    }
                }
            });
        }

    }

}
