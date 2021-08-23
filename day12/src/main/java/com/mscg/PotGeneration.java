package com.mscg;

import com.codepoetics.protonpack.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record PotGeneration(List<Pot> pots, Map<EvolutionKey, Pot> evolutionRules) {

    public List<IndexedPot> evolvePlants(final long generations) {
        List<Pot> currentGeneration = pots;
        long offset = 0;

        final Map<List<Pot>, Long> generatedSequences = new LinkedHashMap<>();

        for (long i = 1; i <= generations; i++) {
            final var curPots = currentGeneration;
            if (!generatedSequences.containsKey(currentGeneration)) {
                generatedSequences.put(currentGeneration, offset);
                currentGeneration = IntStream.range(-2, currentGeneration.size() + 2) //
                        .mapToObj(idx -> {
                            final var key = new EvolutionKey(get(curPots, idx - 2), get(curPots, idx - 1),
                                    get(curPots, idx), get(curPots, idx + 1), get(curPots, idx + 2));
                            return evolutionRules.getOrDefault(key, Pot.EMPTY);
                        }) //
                        .toList();
                // trim empty initial and final pots
                final int l = currentGeneration.size();
                int firstIndex = 0;
                while (firstIndex < l && currentGeneration.get(firstIndex) == Pot.EMPTY) {
                    firstIndex++;
                }
                int lastIndex = l - 1;
                while (lastIndex >= 0 && currentGeneration.get(lastIndex) == Pot.EMPTY) {
                    lastIndex--;
                }
                currentGeneration = currentGeneration.subList(firstIndex, lastIndex + 1);
                offset = offset - 2 + firstIndex;
            } else {
                final long offsetDifference = offset - generatedSequences.get(currentGeneration);
                offset += offsetDifference * (generations - i) + 1;
                break;
            }
        }

        final long finalOffset = offset;
        return StreamUtils.zipWithIndex(currentGeneration.stream()) //
                .map(idx -> new IndexedPot(idx.getValue(), idx.getIndex() + finalOffset)) //
                .toList();
    }

    private Pot get(final List<Pot> pots, final int index) {
        if (index < 0 || index >= pots.size()) {
            return Pot.EMPTY;
        } else {
            return pots.get(index);
        }
    }

    public static PotGeneration parseInput(final BufferedReader in) throws IOException {
        final String statusLine = in.readLine();
        final int index = statusLine.indexOf(':');
        final List<Pot> pots = statusLine.substring(index + 2).chars() //
                .mapToObj(c -> Pot.from((char) c)) //
                .toList();
        in.readLine();
        final Map<EvolutionKey, Pot> evolutionRules = in.lines() //
                .map(line -> Map.entry(new EvolutionKey(//
                                Pot.from(line.charAt(0)), Pot.from(line.charAt(1)), // LL
                                Pot.from(line.charAt(2)), // C
                                Pot.from(line.charAt(3)), Pot.from(line.charAt(4))), // RR
                        Pot.from(line.charAt(line.length() - 1)))) // NEXT
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new PotGeneration(pots, evolutionRules);
    }

    public enum Pot {
        PLANTED, EMPTY;

        @Override
        public String toString() {
            return switch (this) {
                case PLANTED -> "#";
                case EMPTY -> ".";
            };
        }

        public static Pot from(final char c) {
            return switch (c) {
                case '.' -> EMPTY;
                case '#' -> PLANTED;
                default -> throw new IllegalArgumentException("Unsupported value " + c);
            };
        }
    }

    public static record IndexedPot(Pot pot, long index) {

    }

    public static record EvolutionKey(Pot l2, Pot l1, Pot c, Pot r1, Pot r2) {

    }

}
