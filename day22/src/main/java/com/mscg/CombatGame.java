package com.mscg;

import java.io.BufferedReader;
import java.util.Deque;
import java.util.LinkedList;

import com.codepoetics.protonpack.StreamUtils;

import lombok.Getter;

@Getter
public class CombatGame {

    private final Deque<Integer> player1Hand = new LinkedList<>();
    private final Deque<Integer> player2Hand = new LinkedList<>();

    public GameStatus playRound() {
        if (player1Hand.isEmpty()) {
            return GameStatus.PLAYER_2_WINS;
        }
        if (player2Hand.isEmpty()) {
            return GameStatus.PLAYER_1_WINS;
        }

        int p1 = player1Hand.removeFirst();
        int p2 = player2Hand.removeFirst();

        if (p1 >= p2) {
            player1Hand.addLast(p1);
            player1Hand.addLast(p2);
        } else {
            player2Hand.addLast(p2);
            player2Hand.addLast(p1);

        }

        if (player1Hand.isEmpty()) {
            return GameStatus.PLAYER_2_WINS;
        }
        if (player2Hand.isEmpty()) {
            return GameStatus.PLAYER_1_WINS;
        }
        return GameStatus.RUNNING;
    }

    public long getGameValue() {
        GameStatus status;
        while ((status = playRound()) == GameStatus.RUNNING) {
            // do nothing
        }
        var winningHand = status == GameStatus.PLAYER_1_WINS ? player1Hand : player2Hand;
        return StreamUtils.zipWithIndex(winningHand.stream()) //
                .mapToLong(el -> el.getValue().longValue() * (winningHand.size() - el.getIndex())) //
                .sum();
    }

    public enum GameStatus {
        RUNNING, PLAYER_1_WINS, PLAYER_2_WINS;
    }

    public static CombatGame parseInput(BufferedReader in) throws Exception {
        var game = new CombatGame();

        boolean fillPlayer1 = true;
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                fillPlayer1 = false;
                continue;
            }

            if (line.toLowerCase().startsWith("player")) {
                continue;
            }

            int value = Integer.parseInt(line);
            if (fillPlayer1) {
                game.player1Hand.addLast(value);
            } else {
                game.player2Hand.addLast(value);
            }

        }

        return game;
    }

}