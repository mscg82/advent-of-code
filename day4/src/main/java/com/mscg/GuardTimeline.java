package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.soabase.recordbuilder.core.RecordBuilder;

public record GuardTimeline(List<TimelineEntry> entries) {

    public static GuardTimeline parseInput(final BufferedReader in) throws IOException {
        try {
            final var pattern = Pattern.compile("\\[(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})] (.+)");
            final List<TimelineEntry> timelineEntries = in.lines() //
                    .map(pattern::matcher) //
                    .filter(Matcher::matches) //
                    .map(matcher -> {
                        final var timestamp = new Timestamp( //
                                Integer.parseInt(matcher.group(1)), //
                                Integer.parseInt(matcher.group(2)), //
                                Integer.parseInt(matcher.group(3)), //
                                Integer.parseInt(matcher.group(4)), //
                                Integer.parseInt(matcher.group(5)) //
                        );

                        final String actionStr = matcher.group(6);
                        final int guard;
                        final TimelineAction action;
                        if (actionStr.startsWith("Guard #")) {
                            final int index = actionStr.indexOf('#') + 1;
                            final int endIndex = actionStr.indexOf(' ', index);
                            guard = Integer.parseInt(actionStr, index, endIndex, 10);
                            action = TimelineAction.BEGIN_SHIFT;
                        } else if ("wakes up".equals(actionStr)) {
                            guard = 0;
                            action = TimelineAction.WAKES_UP;
                        } else if ("falls asleep".equals(actionStr)) {
                            guard = 0;
                            action = TimelineAction.FALLS_ASLEEP;
                        } else {
                            throw new IllegalArgumentException("Unable to parse action string " + actionStr);
                        }

                        return new TimelineEntry(timestamp, guard, action);
                    }) //
                    .sorted(Comparator.comparing(TimelineEntry::time)) //
                    .toList();

            final var latestEncounteredGuardId = new AtomicInteger(0);
            final List<TimelineEntry> fixedTimelineEntries = timelineEntries.stream() //
                    .map(entry -> switch (entry.action()) {
                        case BEGIN_SHIFT -> {
                            latestEncounteredGuardId.set(entry.guard());
                            yield entry;
                        }
                        case WAKES_UP, FALLS_ASLEEP -> entry.withGuard(latestEncounteredGuardId.intValue());
                    }) //
                    .toList();

            return new GuardTimeline(fixedTimelineEntries);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Timestamp(int year, int month, int day, int hour, int min) implements Comparable<Timestamp> {

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
    public static record TimelineEntry(Timestamp time, int guard, TimelineAction action) implements GuardTimelineTimelineEntryBuilder.With {

    }
}
