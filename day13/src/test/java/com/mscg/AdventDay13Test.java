package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay13Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var schedule = DepartureSchedule.parseInput(in);
            Assertions.assertEquals(939, schedule.minDepartureTime());
            Assertions.assertArrayEquals(new long[]{ 7, 13, 0, 0, 59, 0, 31, 19 }, schedule.lineIds());
        }
    }

    @Test
    public void testEarliestDeparture() throws Exception {
        try (BufferedReader in = readInput()) {
            var schedule = DepartureSchedule.parseInput(in);
            var earliestDeparture = schedule.findEarliestDeparture();
            Assertions.assertEquals(new DepartureSchedule.DepartureInfo(944, 59), earliestDeparture);
            Assertions.assertEquals(295,
                    (earliestDeparture.minDepartureTime() - schedule.minDepartureTime()) * earliestDeparture.lineId());
        }
    }

    @Test
    public void testSolveContest() throws Exception {
        try (BufferedReader in = readInput()) {
            var schedule = DepartureSchedule.parseInput(in);
            Assertions.assertEquals(1068781, schedule.solveContest());
        }
        {
            var schedule = new DepartureSchedule(0, new long[]{ 17, 0, 13, 19 });
            Assertions.assertEquals(3417, schedule.solveContest());
        }
        {
            var schedule = new DepartureSchedule(0, new long[]{ 67, 7, 59, 61 });
            Assertions.assertEquals(754018, schedule.solveContest());
        }
        {
            var schedule = new DepartureSchedule(0, new long[]{ 67, 0, 7, 59, 61 });
            Assertions.assertEquals(779210, schedule.solveContest());
        }
        {
            var schedule = new DepartureSchedule(0, new long[]{ 67, 7, 0, 59, 61 });
            Assertions.assertEquals(1261476, schedule.solveContest());
        }
        {
            var schedule = new DepartureSchedule(0, new long[]{ 1789, 37, 47, 1889 });
            Assertions.assertEquals(1202161486, schedule.solveContest());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
