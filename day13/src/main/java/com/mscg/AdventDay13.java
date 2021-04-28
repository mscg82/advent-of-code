package com.mscg;

import static com.mscg.MazeRoomPositionBuilder.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay13 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var maze = MazeRoom.parseInput(in);
            final var path = maze.findPath(Position(1, 1), Position(31, 39), 100);

            System.out.println("Part 1 - Answer %d".formatted(path.positions().size() - 1));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var maze = MazeRoom.parseInput(in);
            final var path = maze.findPath(Position(1, 1), Position(-1, -1), 50);
            System.out.println("Part 2 - Answer %d".formatted(path.seenPositions()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay13.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
