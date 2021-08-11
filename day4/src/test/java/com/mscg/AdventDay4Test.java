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
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 00), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 05), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 25), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 30), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 55), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 23, 58), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 02, 00, 40), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 02, 00, 50), 99, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 03, 00, 05), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 03, 00, 24), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 03, 00, 29), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 04, 00, 02), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 04, 00, 36), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 04, 00, 46), 99, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 05, 00, 03), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 05, 00, 45), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 05, 00, 55), 99, TimelineAction.WAKES_UP)
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

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
