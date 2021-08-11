package com.mscg;

import com.mscg.GuardTimeline.TimelineAction;
import com.mscg.GuardTimeline.TimelineEntry;
import com.mscg.GuardTimeline.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay4Test {

    @Test
    public void testParse() throws Exception {
        try (var in = readInput()) {
            final var timeline = GuardTimeline.parseInput(in);
            Assertions.assertEquals(List.of( //
                    new TimelineEntry(new Timestamp(1518, 11, 1, 0, 0), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 1, 0, 5), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 1, 0, 25), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 1, 0, 30), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 1, 0, 55), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 1, 23, 58), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 2, 0, 40), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 2, 0, 50), 99, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 3, 0, 5), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 3, 0, 24), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 3, 0, 29), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 4, 0, 2), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 4, 0, 36), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 4, 0, 46), 99, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 5, 0, 3), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 5, 0, 45), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 5, 0, 55), 99, TimelineAction.WAKES_UP)
            ), timeline.entries());
        }
    }

    @Test
    public void testStrategy1() throws Exception {
        try (var in = readInput()) {
            final var timeline = GuardTimeline.parseInput(in);
            Assertions.assertEquals(240, timeline.computeStrategy1());
        }
    }

    @Test
    public void testStrategy2() throws Exception {
        try (var in = readInput()) {
            final var timeline = GuardTimeline.parseInput(in);
            Assertions.assertEquals(4455, timeline.computeStrategy2());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
