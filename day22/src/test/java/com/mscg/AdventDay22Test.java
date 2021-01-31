package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.CombatGame.GameStatus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay22Test {

    @Test
    public void testParse() throws Exception {
        var game = CombatGame.parseInput(readInput());

        Assertions.assertEquals(List.of(9, 2, 6, 3, 1), game.getPlayer1Hand());
        Assertions.assertEquals(List.of(5, 8, 4, 7, 10), game.getPlayer2Hand());
    }

    @Test
    public void testPlayRound() throws Exception {
        var game = CombatGame.parseInput(readInput());

        var status = game.playRound();
        Assertions.assertEquals(GameStatus.RUNNING, status);
        Assertions.assertEquals(List.of(2, 6, 3, 1, 9, 5), game.getPlayer1Hand());
        Assertions.assertEquals(List.of(8, 4, 7, 10), game.getPlayer2Hand());

        while ((status = game.playRound()) == GameStatus.RUNNING) {
            // do nothing
        }
        Assertions.assertEquals(GameStatus.PLAYER_2_WINS, status);
        Assertions.assertEquals(List.of(), game.getPlayer1Hand());
        Assertions.assertEquals(List.of(3, 2, 10, 6, 8, 5, 9, 4, 7, 1), game.getPlayer2Hand());

    }

    @Test
    public void testGameValue() throws Exception {
        var game = CombatGame.parseInput(readInput());

        Assertions.assertEquals(306L, game.getGameValue());
    }
    
    @Test
    public void testRecursiveGameValue() throws Exception {
        var game = CombatGame.parseInput(readInput());

        GameStatus status = game.playRecursiveGame();

        Assertions.assertEquals(GameStatus.PLAYER_2_WINS, status);
        Assertions.assertEquals(List.of(), game.getPlayer1Hand());
        Assertions.assertEquals(List.of(7, 5, 6, 2, 4, 1, 10, 8, 9, 3), game.getPlayer2Hand());
        Assertions.assertEquals(291L, game.getValidationNumber(status));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
