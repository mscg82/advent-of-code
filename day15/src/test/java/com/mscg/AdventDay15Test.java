package com.mscg;

import com.codepoetics.protonpack.StreamUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay15Test {

    @Test
    public void testGeneration() throws Exception {
        try (var in = readInput()) {
            var game = Game.parseInput(in);
            Assertions.assertEquals(0, game.next());
            Assertions.assertEquals(3, game.next());
            Assertions.assertEquals(6, game.next());
            Assertions.assertEquals(0, game.next());
            Assertions.assertEquals(3, game.next());
            Assertions.assertEquals(3, game.next());
            Assertions.assertEquals(1, game.next());
            Assertions.assertEquals(0, game.next());
            Assertions.assertEquals(4, game.next());
            Assertions.assertEquals(0, game.next());
        }
    }

    @Test
    public void testGame() throws Exception {
        try (var in = readInput()) {
            var game = Game.parseInput(in);
            int value = StreamUtils.stream(game)
                    .skip(2019)
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals(436, value);
        }

        {
            var game = new Game(new int[]{ 1, 3, 2 });
            int value = StreamUtils.stream(game)
                    .skip(2019)
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals(1, value);
        }

        {
            var game = new Game(new int[]{ 3, 1, 2 });
            int value = StreamUtils.stream(game)
                    .skip(2019)
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals(1836, value);
        }
    }

    @Test
    public void testGame2() throws Exception {
        try (var in = readInput()) {
            var game = Game.parseInput(in);
            int value = StreamUtils.stream(game)
                    .skip(29999999)
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals(175594, value);
        }

        {
            var game = new Game(new int[]{ 2, 3, 1 });
            int value = StreamUtils.stream(game)
                    .skip(29999999)
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals(6895259, value);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
