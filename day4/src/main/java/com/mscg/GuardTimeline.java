package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GuardTimeline(List<TimelineEntry> entries) {

    public int computeStrategy1() {
        final Map<Integer, List<TimelineEntry>> guardToEntries = entries.stream() //
                .collect(Collectors.groupingBy(TimelineEntry::guard, LinkedHashMap::new, Collectors.toList()));

        final Map<Integer, Map<Day, List<TimelineEntry>>> guardToDayToEntries = guardToEntries.entrySet().stream() //
                .map(entry -> {
                    final Map<Day, List<TimelineEntry>> dayToEntries = entry.getValue().stream() //
                            .collect(Collectors.groupingBy(entry2 -> entry2.time().day(), LinkedHashMap::new, Collectors.toList()));
                    return Map.entry(entry.getKey(), dayToEntries);
                }) //
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Map<Integer, Integer> guardToMinutesAsleep = new HashMap<>();
        final Map<Integer, Map<Day, boolean[]>> guardToDayToMinuteToAsleep = new HashMap<>();
        guardToDayToEntries.forEach((guard, dayToEntries) -> {
            final Map<Day, boolean[]> dayToMinuteToAsleep = guardToDayToMinuteToAsleep.computeIfAbsent(guard, __ -> new HashMap<>());
            dayToEntries.forEach((day, entries) -> {
                final boolean[] minuteToAsleep = dayToMinuteToAsleep.computeIfAbsent(day, __ -> new boolean[60]);
                Seq.seq(entries.stream()).sliding(2) //
                        .map(Stream::toList) //
                        .filter(window -> window.get(0).action() == TimelineAction.FALLS_ASLEEP) //
                        .forEach(window -> {
                            final var asleepEntry = window.get(0);
                            final var awakeEntry = window.get(1);
                            for (int m = asleepEntry.time().min(), max = awakeEntry.time().min(); m < max; m++) {
                                minuteToAsleep[m] = true;
                                guardToMinutesAsleep.merge(guard, 1, Integer::sum);
                            }
                        });
            });
        });

        final int maxMinutesAsleep = guardToMinutesAsleep.values().stream() //
                .mapToInt(Integer::intValue) //
                .max() //
                .orElseThrow();

        final int[] guardsWithMaxMinutesAsleep = guardToMinutesAsleep.entrySet().stream() //
                .filter(entry -> entry.getValue() == maxMinutesAsleep) //
                .mapToInt(Map.Entry::getKey) //
                .toArray();

        return Arrays.stream(guardsWithMaxMinutesAsleep) //
                .map(guardWithMaxMinutesAsleep -> {
                    final Map<Day, boolean[]> dayToMinuteToAsleep = guardToDayToMinuteToAsleep.get(guardWithMaxMinutesAsleep);
                    final Map<Integer, Integer> minuteToHowMuchAsleep = new HashMap<>();
                    dayToMinuteToAsleep.values() //
                            .forEach(minuteToAsleep -> {
                                for (int i = 0; i < 60; i++) {
                                    if (minuteToAsleep[i]) {
                                        minuteToHowMuchAsleep.merge(i, 1, Integer::sum);
                                    }
                                }
                            });

                    final int minuteWithMaxAsleep = minuteToHowMuchAsleep.entrySet().stream() //
                            .max(Map.Entry.comparingByValue()) //
                            .map(Map.Entry::getKey) //
                            .orElseThrow();

                    return guardWithMaxMinutesAsleep * minuteWithMaxAsleep;
                }) //
                .max() //
                .orElseThrow();
    }

    public int computeStrategy2() {
        final Map<Integer, List<TimelineEntry>> guardToEntries = entries.stream() //
                .collect(Collectors.groupingBy(TimelineEntry::guard, LinkedHashMap::new, Collectors.toList()));

        final Map<Integer, Map<Day, List<TimelineEntry>>> guardToDayToEntries = guardToEntries.entrySet().stream() //
                .map(entry -> {
                    final Map<Day, List<TimelineEntry>> dayToEntries = entry.getValue().stream() //
                            .collect(Collectors.groupingBy(entry2 -> entry2.time().day(), LinkedHashMap::new, Collectors.toList()));
                    return Map.entry(entry.getKey(), dayToEntries);
                }) //
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Map<Integer, Map<Day, boolean[]>> guardToDayToMinuteToAsleep = new HashMap<>();
        guardToDayToEntries.forEach((guard, dayToEntries) -> {
            final Map<Day, boolean[]> dayToMinuteToAsleep = guardToDayToMinuteToAsleep.computeIfAbsent(guard, __ -> new HashMap<>());
            dayToEntries.forEach((day, entries) -> {
                final boolean[] minuteToAsleep = dayToMinuteToAsleep.computeIfAbsent(day, __ -> new boolean[60]);
                Seq.seq(entries.stream()).sliding(2) //
                        .map(Stream::toList) //
                        .filter(window -> window.get(0).action() == TimelineAction.FALLS_ASLEEP) //
                        .forEach(window -> {
                            final var asleepEntry = window.get(0);
                            final var awakeEntry = window.get(1);
                            for (int m = asleepEntry.time().min(), max = awakeEntry.time().min(); m < max; m++) {
                                minuteToAsleep[m] = true;
                            }
                        });
            });
        });

        final Map<Integer, Map<Integer, Integer>> guardToMinuteToFrequency = new HashMap<>();
        guardToDayToMinuteToAsleep.forEach((guard, dayToMinutesAsleep) -> {
            final Map<Integer, Integer> minuteToFrequency = guardToMinuteToFrequency.computeIfAbsent(guard, __ -> new HashMap<>());
            dayToMinutesAsleep.forEach((day, minutesAsleep) -> {
                for (int m = 0; m < 60; m++) {
                    if (minutesAsleep[m]) {
                        minuteToFrequency.merge(m, 1, Integer::sum);
                    }
                }
            });
        });

        record Triple(int guard, int minute, int frequency) {

        }

        final Triple maxTriple = guardToMinuteToFrequency.entrySet().stream() //
                .flatMap(entry -> entry.getValue().entrySet().stream() //
                        .map(entry2 -> new Triple(entry.getKey(), entry2.getKey(), entry2.getValue()))) //
                .max(Comparator.comparingInt(Triple::frequency)) //
                .orElseThrow();

        return maxTriple.guard() * maxTriple.minute();
    }

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
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Day(int year, int month, int day) implements Comparable<Day> {

        private static final Comparator<Day> COMPARATOR = Comparator //
                .comparingInt(Day::year) //
                .thenComparing(Day::month) //
                .thenComparing(Day::day);

        @Override
        public int compareTo(@NonNull final Day other) {
            return COMPARATOR.compare(this, other);
        }

    }

    public static record Timestamp(Day day, int hour, int min) implements Comparable<Timestamp> {

        public Timestamp(final int year, final int month, final int day, final int hour, final int min) {
            this(new Day(year, month, day), hour, min);
        }

        private static final Comparator<Timestamp> COMPARATOR = Comparator //
                .comparing(Timestamp::day) //
                .thenComparing(Timestamp::hour) //
                .thenComparing(Timestamp::min);

        @Override
        public int compareTo(@NonNull final Timestamp other) {
            return COMPARATOR.compare(this, other);
        }

    }

    public enum TimelineAction {
        BEGIN_SHIFT, FALLS_ASLEEP, WAKES_UP
    }

    @RecordBuilder
    public static record TimelineEntry(Timestamp time, int guard,
                                       TimelineAction action) implements GuardTimelineTimelineEntryBuilder.With {

    }

}
