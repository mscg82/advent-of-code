package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Grid {

    private final List<Direction> directions;

    public Map<Position, Long> visit() {
        Map<Position, Long> counts = new HashMap<>();
        
        Position position = new Position(0, 0);
        counts.merge(position, 1L, Long::sum);
        for (var direction : directions) {
            position = position.move(direction);
            counts.merge(position, 1L, Long::sum);
        }

        return counts;
    }

    public Map<Position, Long> visit2() {
        Map<Position, Long> counts = new HashMap<>();
        
        Position position1 = new Position(0, 0);
        Position position2 = new Position(0, 0);
        counts.merge(position1, 1L, Long::sum);
        counts.merge(position2, 1L, Long::sum);
        
        int turn = 0;
        for (var direction : directions) {
            switch (turn) {
                case 0 -> {
                    position1 = position1.move(direction);
                    counts.merge(position1, 1L, Long::sum);
                }
                case 1 -> {
                    position2 = position2.move(direction);
                    counts.merge(position2, 1L, Long::sum);
                }
                default -> throw new IllegalArgumentException("Impossible case");
            }
            turn = (turn + 1) % 2;
        }

        return counts;
    }

    public static Grid parseInput(BufferedReader in) throws IOException {
        List<Direction> directions = in.readLine() //
                .chars() //
                .mapToObj(c -> Direction.fromChar((char) c)) //
                .collect(Collectors.toUnmodifiableList());

        return new Grid(directions);
    }

    public static record Position(int north, int east) {

        public Position move(Direction direction) {
            return switch (direction) {
                case N -> new Position(north + 1, east);
                case E -> new Position(north, east + 1);
                case S -> new Position(north - 1, east);
                case W -> new Position(north, east - 1);
            };
        }

    }

    private enum Direction {
        N, E, S, W;

        @Override
        public String toString() {
            return switch (this) {
                case N -> "^";
                case E -> ">";
                case S -> "v";
                case W -> "<";
            };
        }

        public static Direction fromChar(char c) {
            return switch (c) {
                case '^' -> Direction.N;
                case '>' -> Direction.E;
                case 'v' -> Direction.S;
                case '<' -> Direction.W;
                default -> throw new IllegalArgumentException("Illegal direction character " + c);
            };
        }
    }

}