package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay8Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var screen = Screen.parseInput(3, 7, in);
            Assertions.assertEquals(List.of( //
                    new Screen.Rect(3, 2), //
                    new Screen.RotateCol(1, 1), //
                    new Screen.RotateRow(0, 4), //
                    new Screen.RotateCol(1, 1)), //
                    screen.getInstructions());
        }
    }

    @Test
    public void testScreen() throws Exception {
        try (BufferedReader in = readInput()) {
            var screen = Screen.parseInput(3, 7, in);
            screen.getInstructions().get(0).executeOnScreen(screen.getScreen());
            Assertions.assertEquals("""
                    ###....
                    ###....
                    .......""", screen.toString());

            screen.getInstructions().get(1).executeOnScreen(screen.getScreen());
            Assertions.assertEquals("""
                    #.#....
                    ###....
                    .#.....""", screen.toString());

            screen.getInstructions().get(2).executeOnScreen(screen.getScreen());
            Assertions.assertEquals("""
                    ....#.#
                    ###....
                    .#.....""", screen.toString());

            screen.getInstructions().get(3).executeOnScreen(screen.getScreen());
            Assertions.assertEquals("""
                    .#..#.#
                    #.#....
                    .#.....""", screen.toString());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
