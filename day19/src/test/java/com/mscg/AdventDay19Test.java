package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay19Test {

    @Test
    public void testGame1() {
        final var game = new ElfGame(5);
        Assertions.assertEquals(3, game.playGame());
    }

    @Test
    public void testGame2() {
        final var game = new ElfGame(6);
        Assertions.assertEquals(5, game.playGame());
    }

    @Test
    public void testVariant() {
        final var game = new ElfGame(5);
        Assertions.assertEquals(2, game.playVariant());
    }

}
