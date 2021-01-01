package com.mscg;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import lombok.Getter;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Game implements Iterable<Integer> {

    private final Map<Integer, Pair> knownNumbers;

    private final int[] initialNumbers;

    private int currentTurn;

    private int lastGenerated;

    public Game(int[] initialNumbers) {
        this.initialNumbers = initialNumbers;
        knownNumbers = StreamUtils.zipWithIndex(Arrays.stream(initialNumbers).boxed())
                .collect(Collectors.toMap(Indexed::getValue, idx -> new Pair(0, (int) idx.getIndex() + 1)));
        currentTurn = 0;
    }

    public int next() {
        int index = currentTurn++;

        if (index < initialNumbers.length) {
            lastGenerated = initialNumbers[index];
            return lastGenerated;
        }

        final Pair pair = knownNumbers.get(lastGenerated);
        if (pair.first() == 0) {
            lastGenerated = 0;
        }
        else {
            lastGenerated = pair.second() - pair.first();
        }
        knownNumbers.compute(lastGenerated, (__, oldPair) -> oldPair == null ? new Pair(0, currentTurn) : oldPair.shift(currentTurn));

        return lastGenerated;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new GameIterator();
    }

    public static Game parseInput(BufferedReader in) throws Exception {
        final int[] numbers = Arrays.stream(in.readLine().split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        return new Game(numbers);
    }

    private static record Pair(int first, int second) {

        public Pair shift(int second) {
            return new Pair(this.second, second);
        }

    }

    private class GameIterator implements Iterator<Integer> {

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            return Game.this.next();
        }

    }

}
