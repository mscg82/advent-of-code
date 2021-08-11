package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.GuardTimeline.TimelineAction;
import com.mscg.GuardTimeline.TimelineEntry;
import com.mscg.GuardTimeline.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay4Test {

    @Test
    public void testParse() throws Exception {
        try (var in = readInput()) {
            var timeline = GuardTimeline.parseInput(in);
            Assertions.assertEquals(List.of( //
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 00), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 05), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 25), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 30), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 00, 55), 10, TimelineAction.WAKES_UP),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 23, 58), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 01, 23, 58), 99, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 02, 00, 40), 99, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 02, 00, 50), 99, TimelineAction.WAKES_UP)
            new TimelineEntry(new Timestamp(1518, 11, 03, 00, 05), 10, TimelineAction.BEGIN_SHIFT),
                    new TimelineEntry(new Timestamp(1518, 11, 03, 00, 24), 10, TimelineAction.FALLS_ASLEEP),
                    new TimelineEntry(new Timestamp(1518, 11, 03, 00, 29), 10, TimelineAction.WAKES_UP),
            ),timeline.entries());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
