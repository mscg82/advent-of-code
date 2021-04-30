package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;

public record DiscMachine(List<Disc> discs) {

    public int findFirstSolution() {
        record Sieve(int n, int a) {
        }

        final var sieves = StreamUtils.zipWithIndex(this.discs.stream()) //
                .map(idx -> {
                    final Disc disc = idx.getValue();
                    final int index = (int) idx.getIndex();
                    return new Sieve(disc.positions(), (2 * disc.positions() - (disc.offset() + index + 1)) % disc.positions());
                }) //
                .sorted(Comparator.comparingInt(Sieve::n).reversed()) //
                .collect(Collectors.toCollection(ArrayList::new));

        var sieve = sieves.remove(0);
        int t = sieve.a();
        int delta = sieve.n();
        while (!sieves.isEmpty()) {
            sieve = sieves.remove(0);
            while (t % sieve.n() != sieve.a()) {
                t += delta;
            }
            delta *= sieve.n();
        }

        return t;
    }

    public static DiscMachine parseInput(final BufferedReader in) throws IOException {
        try {
            final var patter = Pattern.compile(".* (\\d+) positions;.+position (\\d+)\\.");

            final List<Disc> discs = in.lines() //
                    .map(patter::matcher) //
                    .filter(Matcher::find) //
                    .map(matcher -> new Disc(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))) //
                    .toList();

            return new DiscMachine(discs);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Disc(int positions, int offset) {
    }

}
