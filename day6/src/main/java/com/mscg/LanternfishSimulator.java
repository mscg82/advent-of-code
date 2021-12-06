package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public record LanternfishSimulator(long[] fishesByLifecycleDay) {

    public static LanternfishSimulator parseInput(final BufferedReader in) throws IOException {
        final long[] fishesByLifecycleDay = new long[9];

        Arrays.stream(in.readLine().split(",")) //
                .map(Integer::parseInt) //
                .forEach(day -> fishesByLifecycleDay[day]++);

        return new LanternfishSimulator(fishesByLifecycleDay);
    }

    public long countLanternFishes(final int days) {
        // defensive copy to avoid changing the input data
        final long[] fishes = new long[fishesByLifecycleDay.length];
        System.arraycopy(fishesByLifecycleDay, 0, fishes, 0, fishesByLifecycleDay.length);

        int day0 = 0;
        int day6 = 6;

        for (int day = 0; day < days; day++) {
            final long reproducingFishes = fishes[day0];

            day0 = (day0 + 1) % fishes.length;
            day6 = (day6 + 1) % fishes.length;

            fishes[day6] += reproducingFishes;
        }

        return Arrays.stream(fishes).sum();
    }

}
