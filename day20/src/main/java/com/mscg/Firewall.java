package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import io.soabase.recordbuilder.core.RecordBuilder;

public record Firewall(SortedSet<Range> ranges) {

    public long firstAllowedAddress() {
        return LongStream.rangeClosed(0, 2L * Integer.MAX_VALUE) //
                .filter(address -> ranges.stream().noneMatch(range -> range.contains(address))) //
                .findFirst() //
                .orElseThrow();
    }

    public static Firewall parseInput(final BufferedReader in) throws IOException {
        try {
            final TreeSet<Range> ranges = in.lines() //
                    .map(Range::fromString) //
                    .collect(Collectors.toCollection(TreeSet::new));
            return new Firewall(ranges);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @RecordBuilder
    public static record Range(long start, long end) implements Comparable<Range>, FirewallRangeBuilder.With {

        private static final Comparator<Range> COMPARATOR = Comparator.comparingLong(Range::start).thenComparingLong(Range::end);

        public boolean contains(final long address) {
            return address >= start && address <= end;
        }

        @Override
        public int compareTo(final Range o) {
            return COMPARATOR.compare(this, o);
        }

        public static Range fromString(final String value) {
            final int index = value.indexOf('-');
            return new Range(Long.parseLong(value.substring(0, index)), Long.parseLong(value.substring(index + 1)));
        }
    }

}
