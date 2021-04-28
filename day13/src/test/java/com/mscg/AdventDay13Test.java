package com.mscg;

import static com.mscg.MazeRoomPositionBuilder.Position;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay13Test {

    @Test
    public void testPath() throws Exception {
        try (BufferedReader in = readInput()) {
            final var maze = MazeRoom.parseInput(in);
            final var path = maze.findPath(Position(1, 1), Position(7, 4), 100);

            Assertions.assertEquals(11, path.positions().size() - 1);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
