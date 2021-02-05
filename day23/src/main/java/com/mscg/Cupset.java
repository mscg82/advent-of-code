package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class Cupset {

    private final Cup[] cups;
    private final Map<Integer, Cup> valToCup;
    private final int minCup;
    private final int maxCup;

    public Cupset(final int[] cups) {
        this.cups = Arrays.stream(cups) //
                .mapToObj(Cup::new) //
                .toArray(Cup[]::new);
        this.cups[0].setPrev(this.cups[this.cups.length - 1]);
        this.cups[this.cups.length - 1].setNext(this.cups[0]);
        for (int i = 1; i < this.cups.length; i++) {
            this.cups[i].setPrev(this.cups[i - 1]);
            this.cups[i - 1].setNext(this.cups[i]);
        }

        valToCup = Arrays.stream(this.cups) //
                .collect(Collectors.toMap(Cup::getValue, c -> c));

        var stats = Arrays.stream(cups) //
                .summaryStatistics();

        this.minCup = stats.getMin();
        this.maxCup = stats.getMax();
    }

    public void run(int steps) {
        Cup currentCup = cups[0];
        for (int i = 0; i < steps; i++) {
            Cup destinationCup = getDestinationCup(currentCup);
            Cup afterDestination = destinationCup.getNext();
            Cup next1 = currentCup.getNext();
            Cup next2 = next1.getNext();
            Cup next3 = next2.getNext();
            Cup next4 = next3.getNext();

            destinationCup.setNext(next1);
            next1.setPrev(destinationCup);

            next3.setNext(afterDestination);
            afterDestination.setPrev(next3);

            currentCup.setNext(next4);
            next4.setPrev(currentCup);

            currentCup = currentCup.getNext();
        }
    }

    public long getValidationNumber() {
        Cup cup1 = findCup(1);
        Cup next1 = cup1.getNext();
        Cup next2 = next1.getNext();
        return next1.getValue() * (long) next2.getValue();
    }

    @Override
    public String toString() {
        return Stream.concat(Stream.of(cups[0]), Stream.iterate(cups[0].getNext(), cup -> cup != cups[0], Cup::getNext)) //
                .mapToInt(Cup::getValue) //
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) //
                .toString();
    }

    public String toStringFrom(int firstCup) {
        Cup startCup = findCup(firstCup);
        return Stream.iterate(startCup.getNext(), cup -> cup != startCup, Cup::getNext) //
                .mapToInt(Cup::getValue) //
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) //
                .toString();
    }

    private Cup findCup(int value) {
        Cup cup = valToCup.get(value);
        if (cup != null) {
            return cup;
        }
        throw new IllegalArgumentException("Unable to find cup with value " + value);
    }

    private Cup getDestinationCup(final Cup currentCup) {
        final Cup next1 = currentCup.getNext();
        final Cup next2 = next1.getNext();
        final Cup next3 = next2.getNext();

        int destinationCup = currentCup.getValue() - 1;
        if (destinationCup < minCup) {
            destinationCup = maxCup;
        }
        while (destinationCup == next1.getValue() || destinationCup == next2.getValue()
                || destinationCup == next3.getValue()) {
            if (destinationCup == currentCup.getValue()) {
                throw new IllegalStateException("Unable to find destination cup");
            }
            destinationCup = destinationCup - 1;
            if (destinationCup < minCup) {
                destinationCup = maxCup;
            }
        }

        return findCup(destinationCup);
    }

    public static Cupset parseInput(BufferedReader in) throws IOException {
        String line = in.readLine();
        int length = line.length();
        final int[] cups = new int[length];
        for (int i = 0; i < length; i++) {
            cups[i] = Integer.parseInt(line.substring(i, i + 1));
        }
        return new Cupset(cups);
    }

    public static Cupset parseInput2(BufferedReader in) throws IOException {
        String line = in.readLine();
        int length = line.length();
        final int[] cups = new int[1_000_000];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            int val = Integer.parseInt(line.substring(i, i + 1));
            if (val > max) {
                max = val;
            }
            cups[i] = val;
        }
        for (int i = length; i < cups.length; i++) {
            cups[i] = max + (i - length) + 1;
        }
        return new Cupset(cups);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class Cup {
        private final int value;
        private Cup next;
        private Cup prev;

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}