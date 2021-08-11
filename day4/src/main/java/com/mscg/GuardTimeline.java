package com.mscg;

import java.util.Comparator;

import io.soabase.recordbuilder.core.RecordBuilder;

public class GuardTimeline {

    public record Timestamp(int year, int month, int day, int hour, int min) implements Comparable<Timestamp> {

        private static final Comparator<Timestamp> COMPARATOR = Comparator //
                .comparingInt(Timestamp::year) //
                .thenComparing(Timestamp::month) //
                .thenComparing(Timestamp::day) //
                .thenComparing(Timestamp::hour) //
                .thenComparing(Timestamp::min);

        @Override
        public int compareTo(final Timestamp other) {
            return COMPARATOR.compare(this, other);
        }
    }

    public enum TimelineAction {
        BEGIN_SHIFT, FALLS_ASLEEP, WAKES_UP
    }

    @RecordBuilder
    public record TimelineEntry(Timestamp time, int guard, TimelineAction action) {

    }
}
