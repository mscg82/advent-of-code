package com.mscg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public record MarbleGame(int players, int lastMarble) {

    public long findMaxScore() {
        final long[] playersScore = new long[players];
        final var marbles = Marble.first(0);

        int currentPlayer = 0;
        var currentMarble = marbles;
        for (int marble = 1; marble <= lastMarble; marble++) {
            if (marble % 23 == 0) {
                for (int i = 0; i < 7; i++) {
                    currentMarble = currentMarble.getPrevious();
                }
                playersScore[currentPlayer] += currentMarble.getValue() + marble;
                currentMarble = currentMarble.removeAndMoveCW();
            } else {
                currentMarble = currentMarble.getNext();
                currentMarble = currentMarble.insertCW(marble);
            }
            currentPlayer = (currentPlayer + 1) % players;
        }

        return Arrays.stream(playersScore) //
                .max() //
                .orElseThrow();
    }

    public static MarbleGame parseInput(final BufferedReader in, final int maxValueMultiplier) throws IOException {
        final var pattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");
        final var matcher = pattern.matcher(in.readLine());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unsupported input format");

        }

        return new MarbleGame(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) * maxValueMultiplier);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class Marble {

        public static Marble first(final int value) {
            final var first = new Marble(value);
            first.next = first;
            first.previous = first;
            return first;
        }

        private final int value;

        private Marble next;

        private Marble previous;

        public Marble removeAndMoveCW() {
            final var next = this.next;
            this.previous.next = this.next;
            next.previous = this.previous;

            this.next = null;
            this.previous = null;

            return next;
        }

        public Marble insertCW(final int value) {
            final var next = this.next;

            final var newMarble = new Marble(value);
            newMarble.next = next;
            newMarble.previous = this;

            this.next = newMarble;
            next.previous = newMarble;

            return newMarble;
        }

    }

}
