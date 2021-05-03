package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

public record Firewall(SortedSet<Range> ranges) {

    public long firstAllowedAddress() {
        final SortedSet<Range> mergedRanges = mergeRanges();
        return mergedRanges.first().end() + 1;
    }

    public long allowedAddresses() {
        final SortedSet<Range> mergedRanges = mergeRanges();

        final long internalCount = StreamUtils.windowed(mergedRanges.stream(), 2) //
                .mapToLong(window -> measureHole(window.get(0), window.get(1))) //
                .sum();
        final long firstGap = measureHole(new Range(0, 0), mergedRanges.first());
        final long lastGap = measureHole(mergedRanges.last(), new Range(0xFFFFFFFFL, 0xFFFFFFFFL));

        return firstGap + internalCount + lastGap;
    }

    private long measureHole(final Range left, final Range right) {
        return Math.max(0L, right.start() - left.end() - 1);
    }

    private SortedSet<Range> mergeRanges() {
        final SortedSet<Range> mergedRanges = new TreeSet<>();
        Range rangeHead = null;
        for (final Range range : ranges) {
            if (rangeHead == null || range.start() > rangeHead.end()) {
                if (rangeHead != null) {
                    mergedRanges.add(rangeHead);
                }
                rangeHead = range;
                continue;
            }
            if (range.end() > rangeHead.end()) {
                rangeHead = rangeHead.withEnd(range.end());
            }
        }
        mergedRanges.add(rangeHead);
        return mergedRanges;
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
