package com.mscg;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

        final int p1 = player1Hand.removeFirst();
        final int p2 = player2Hand.removeFirst();

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

    public GameStatus playRecursiveGame() {
        final var recursiveGame = new RecursiveCombatGame(player1Hand, player2Hand);
        final GameStatus result = recursiveGame.playGame();

        player1Hand.clear();
        player1Hand.addAll(recursiveGame.player1Hand);
        player2Hand.clear();
        player2Hand.addAll(recursiveGame.player2Hand);

        return result;
    }

    public long getValidationNumber(final GameStatus status) {
        final var winningHand = status == GameStatus.PLAYER_1_WINS ? player1Hand : player2Hand;

        return StreamUtils.zipWithIndex(winningHand.stream()) //
                .mapToLong(el -> el.getValue().longValue() * (winningHand.size() - el.getIndex())) //
                .sum();
    }

    public long getGameValue() {
        GameStatus status;
        while ((status = playRound()) == GameStatus.RUNNING) {
            // do nothing
        }

        return getValidationNumber(status);
    }

    private static class RecursiveCombatGame {
        private final LinkedList<Integer> player1Hand;
        private final LinkedList<Integer> player2Hand;

        private final Set<List<Integer>> player1KnownHands;
        private final Set<List<Integer>> player2KnownHands;

        public RecursiveCombatGame(final Collection<Integer> player1Hand, final Collection<Integer> player2Hand) {
            this.player1Hand = new LinkedList<>(player1Hand);
            this.player2Hand = new LinkedList<>(player2Hand);

            this.player1KnownHands = new HashSet<>();
            this.player2KnownHands = new HashSet<>();
        }

        public GameStatus playGame() {
            GameStatus status;
            while ((status = playRound()) == GameStatus.RUNNING) {
                // do nothing
            }

            return status;
        }

        private GameStatus playRound() {
            // anti infinite-recursion rule
            if (player1KnownHands.contains(player1Hand) && player2KnownHands.contains(player2Hand)) {
                return GameStatus.PLAYER_1_WINS;
            }
            player1KnownHands.add(player1Hand);
            player2KnownHands.add(player2Hand);

            if (player1Hand.isEmpty()) {
                return GameStatus.PLAYER_2_WINS;
            }
            if (player2Hand.isEmpty()) {
                return GameStatus.PLAYER_1_WINS;
            }

            final int c1 = player1Hand.removeFirst();
            final int c2 = player2Hand.removeFirst();

            final GameStatus roundStatus;
            if (c1 <= player1Hand.size() && c2 <= player2Hand.size()) {
                final var subGame = new RecursiveCombatGame(player1Hand.subList(0, c1), player2Hand.subList(0, c2));
                roundStatus = subGame.playGame();
            } else if (c1 >= c2) {
                roundStatus = GameStatus.PLAYER_1_WINS;
            } else {
                roundStatus = GameStatus.PLAYER_2_WINS;
            }

            switch (roundStatus) {
                case PLAYER_1_WINS -> player1Hand.addAll(List.of(c1, c2));
                case PLAYER_2_WINS -> player2Hand.addAll(List.of(c2, c1));
                case RUNNING -> throw new IllegalStateException("Invalid status for round found");
            }

            if (player1Hand.isEmpty()) {
                return GameStatus.PLAYER_2_WINS;
            }
            if (player2Hand.isEmpty()) {
                return GameStatus.PLAYER_1_WINS;
            }
            return GameStatus.RUNNING;
        }
    }

    public enum GameStatus {
        RUNNING, PLAYER_1_WINS, PLAYER_2_WINS;
    }

    public static CombatGame parseInput(final BufferedReader in) throws Exception {
        final var game = new CombatGame();

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

            final int value = Integer.parseInt(line);
            if (fillPlayer1) {
                game.player1Hand.addLast(value);
            } else {
                game.player2Hand.addLast(value);
            }

        }

        return game;
    }

}