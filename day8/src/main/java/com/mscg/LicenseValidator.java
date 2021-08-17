package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public record LicenseValidator(int[] rawData) {

    public long computeChecksum() {
        final ParseResult parseResult = parseRecord(null, 0);

        return parseResult.entries().stream() //
                .flatMapToInt(entry -> Arrays.stream(entry.payloads())) //
                .sum();
    }

    public long computeAdvancedChecksum() {
        final ParseResult parseResult = parseRecord(null, 0);

        final List<Entry> entries = parseResult.entries();
        final Map<Entry, List<Entry>> nodeToChildren = new IdentityHashMap<>();
        for (int i = 1, l = entries.size(); i < l; i++) {
            final var entry = entries.get(i);
            nodeToChildren.computeIfAbsent(entry.parent(), __ -> new ArrayList<>()).add(entry);
        }

        final Map<Entry, Long> nodeToValue = new IdentityHashMap<>();
        computeNodeValue(entries.get(0), nodeToChildren, nodeToValue);

        return nodeToValue.get(entries.get(0));
    }

    private void computeNodeValue(final Entry node, final Map<Entry, List<Entry>> nodeToChildren, final Map<Entry, Long> nodeToValue) {
        final List<Entry> children = nodeToChildren.getOrDefault(node, List.of());
        final long value;
        if (children.isEmpty()) {
            value = Arrays.stream(node.payloads()).sum();

        } else {
            children.forEach(child -> computeNodeValue(child, nodeToChildren, nodeToValue));
            value = Arrays.stream(node.payloads()) //
                    .filter(payload -> payload <= children.size()) //
                    .mapToLong(payload -> nodeToValue.getOrDefault(children.get(payload - 1), 0L)) //
                    .sum();
        }
        nodeToValue.put(node, value);
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

        @Override
        public String toString() {
            return "Entry[" +
                    "parent=" + parent +
                    ", payloads=" + Arrays.toString(payloads) +
                    ']';
        }

    }

}
