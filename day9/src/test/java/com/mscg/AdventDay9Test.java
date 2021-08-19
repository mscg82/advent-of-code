package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay9Test {

    @Test
    public void testGame2() {
        {
            final var game = new MarbleGame(9, 25);
            Assertions.assertEquals(32, game.findMaxScore());
        }

        {
            final var game = new MarbleGame(10, 1618);
            Assertions.assertEquals(8317, game.findMaxScore());
        }

        {
            final var game = new MarbleGame(13, 7999);
            Assertions.assertEquals(146373, game.findMaxScore());
        }

        {
            final var game = new MarbleGame(17, 1104);
            Assertions.assertEquals(2764, game.findMaxScore());
        }
    }

}
