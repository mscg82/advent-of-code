package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Keypad {

    private static final Map<Key, Map<Direction, Key>> adjacencyList1;
    private static final Map<Key, Map<Direction, Key>> adjacencyList2;

    static {
        adjacencyList1 = Collections.unmodifiableMap(adjacencyList1());
        adjacencyList2 = Collections.unmodifiableMap(adjacencyList2());
    }

    private static Map<Key, Map<Direction, Key>> adjacencyList1() {
        Map<Key, Map<Direction, Key>> tmp = new EnumMap<>(Key.class);
        tmp.put(Key.K1, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.RIGHT, Key.K2, //
                Direction.DOWN, Key.K4))));
        tmp.put(Key.K2, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K1, //
                Direction.RIGHT, Key.K3, //
                Direction.DOWN, Key.K5))));
        tmp.put(Key.K3, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K2, //
                Direction.DOWN, Key.K6))));
        tmp.put(Key.K4, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.UP, Key.K1, //
                Direction.RIGHT, Key.K5, //
                Direction.DOWN, Key.K7))));
        tmp.put(Key.K5, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.UP, Key.K2, //
                Direction.LEFT, Key.K4, //
                Direction.RIGHT, Key.K6, //
                Direction.DOWN, Key.K8))));
        tmp.put(Key.K6, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.UP, Key.K3, //
                Direction.LEFT, Key.K5, //
                Direction.DOWN, Key.K9))));
        tmp.put(Key.K7, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.RIGHT, Key.K8, //
                Direction.UP, Key.K4))));
        tmp.put(Key.K8, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K7, //
                Direction.RIGHT, Key.K9, //
                Direction.UP, Key.K5))));
        tmp.put(Key.K9, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K8, //
                Direction.UP, Key.K6))));
        return tmp;
    }

    private static Map<Key, Map<Direction, Key>> adjacencyList2() {
        Map<Key, Map<Direction, Key>> tmp = new EnumMap<>(Key.class);
        tmp.put(Key.K1, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.DOWN, Key.K3))));
        tmp.put(Key.K2, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.RIGHT, Key.K3, //
                Direction.DOWN, Key.K6))));
        tmp.put(Key.K3, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K2, //
                Direction.RIGHT, Key.K4, //
                Direction.UP, Key.K1, //
                Direction.DOWN, Key.K7))));
        tmp.put(Key.K4, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K3, //
                Direction.DOWN, Key.K8))));
        tmp.put(Key.K5, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.RIGHT, Key.K6))));
        tmp.put(Key.K6, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K5, //
                Direction.RIGHT, Key.K7, //
                Direction.UP, Key.K2, //
                Direction.DOWN, Key.A))));
        tmp.put(Key.K7, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K6, //
                Direction.RIGHT, Key.K8, //
                Direction.UP, Key.K3, //
                Direction.DOWN, Key.B))));
        tmp.put(Key.K8, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K7, //
                Direction.RIGHT, Key.K9, //
                Direction.UP, Key.K4, //
                Direction.DOWN, Key.C))));
        tmp.put(Key.K9, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.K8))));
        tmp.put(Key.A, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.RIGHT, Key.B, //
                Direction.UP, Key.K6))));
        tmp.put(Key.B, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.A, //
                Direction.RIGHT, Key.C, //
                Direction.UP, Key.K7, //
                Direction.DOWN, Key.D))));
        tmp.put(Key.C, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.LEFT, Key.B, //
                Direction.UP, Key.K8))));
        tmp.put(Key.D, Collections.unmodifiableMap(new EnumMap<>(Map.of(//
                Direction.UP, Key.B))));
        return tmp;
    }

    public final List<List<Direction>> instructions;

    public String computeCode1() {
        return computeCode(adjacencyList1);
    }

    public String computeCode2() {
        return computeCode(adjacencyList2);
    }

    private String computeCode(Map<Key, Map<Direction, Key>> adjacencyList) {
        StringBuilder code = new StringBuilder(instructions.size());
        Key currentKey = Key.K5;
        for (List<Direction> directions : instructions) {
            for (Direction dir : directions) {
                currentKey = adjacencyList.get(currentKey).getOrDefault(dir, currentKey);
            }
            code.append(currentKey);
        }
        return code.toString();
    }

    public static Keypad parseInput(BufferedReader in) throws IOException {
        List<List<Direction>> instructions = in.lines() //
                .map(line -> line.chars() //
                        .mapToObj(c -> Direction.fromChar((char) c)) //
                        .toList()) //
                .toList();

        return new Keypad(instructions);
    }

    public enum Direction {
        UP, LEFT, DOWN, RIGHT;

        @Override
        public String toString() {
            return switch (this) {
            case UP -> "U";
            case LEFT -> "L";
            case DOWN -> "D";
            case RIGHT -> "R";
            };
        }

        public static Direction fromChar(char c) {
            return switch (c) {
            case 'U' -> Direction.UP;
            case 'L' -> Direction.LEFT;
            case 'D' -> Direction.DOWN;
            case 'R' -> Direction.RIGHT;
            default -> throw new IllegalArgumentException("Unsupported char " + c);
            };
        }
    }

    public enum Key {
        K1, K2, K3, K4, K5, K6, K7, K8, K9, A, B, C, D;

        @Override
        public String toString() {
            return switch (this) {
            case K1 -> "1";
            case K2 -> "2";
            case K3 -> "3";
            case K4 -> "4";
            case K5 -> "5";
            case K6 -> "6";
            case K7 -> "7";
            case K8 -> "8";
            case K9 -> "9";
            case A -> "A";
            case B -> "B";
            case C -> "C";
            case D -> "D";
            };
        }
    }

}
