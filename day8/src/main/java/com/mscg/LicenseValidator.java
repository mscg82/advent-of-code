package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record LicenseValidator(int[] rawData) {

    public long computeChecksum() {
        final ParseResult entries = parseRecord(null, 0);

        return entries.entries().stream() //
                .flatMapToInt(entry -> Arrays.stream(entry.payloads())) //
                .sum();
    }

    private ParseResult parseRecord(final Entry parent, final int startIndex) {
        final List<Entry> entries = new ArrayList<>();

        final int childrenNum = rawData[startIndex];
        final int payloadsNum = rawData[startIndex + 1];

        final var entry = new Entry(parent, new int[payloadsNum]);
        entries.add(entry);

        int nextIndex = startIndex + 2;
        for (int i = 0; i < childrenNum; i++) {
            final ParseResult subParse = parseRecord(entry, nextIndex);
            nextIndex = subParse.lastIndex();
            entries.addAll(subParse.entries());
        }

        for (int i = 0; i < payloadsNum; i++) {
            entry.payloads()[i] = rawData[nextIndex++];
        }

        return new ParseResult(entries, nextIndex);
    }

    public static LicenseValidator parseInput(final BufferedReader in) throws IOException {
        try {
            final int[] rawData = in.lines() //
                    .flatMap(line -> Arrays.stream(line.split(" "))) //
                    .mapToInt(Integer::parseInt) //
                    .toArray();
            return new LicenseValidator(rawData);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static record ParseResult(List<Entry> entries, int lastIndex) {

    }

    public static record Entry(Entry parent, int[] payloads) {

    }

}
