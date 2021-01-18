package com.mscg;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public record DepartureSchedule(long minDepartureTime, long[] lineIds) {

    public DepartureInfo findEarliestDeparture() {
        return Arrays.stream(lineIds)
                .filter(line -> line != 0)
                .mapToObj(line -> new DepartureInfo(minDepartureTime + (line - (minDepartureTime % line)), line))
                .min(Comparator.comparingLong(DepartureInfo::minDepartureTime))
                .orElseThrow();
    }

    public long solveContest() {
        record Sieve(long a, long mod) {
            public Sieve {
                while (a < 0) {
                    a += mod;
                }
            }
        }

        var sieves = new ArrayList<Sieve>();
        sieves.add(new Sieve(0L, lineIds[0]));
        for (int i = 1; i < lineIds.length; i++) {
            if (lineIds[i] != 0) {
                sieves.add(new Sieve((lineIds[i] - i), lineIds[i]));
            }
        }
        sieves.sort(Comparator.comparingLong(Sieve::mod).reversed());

        long solution = sieves.get(0).a();
        long step = sieves.get(0).mod();
        for (Sieve sieve : sieves.subList(1, sieves.size())) {
            while (solution % sieve.mod() != sieve.a()) {
                solution += step;
            }
            step *= sieve.mod();
        }

        return solution;
    }

    public static DepartureSchedule parseInput(BufferedReader in) throws Exception {
        long minDepartureTime = Long.parseLong(in.readLine());
        long[] lineIds = Arrays.stream(in.readLine().split(","))
                .map(s -> "x".equals(s) ? "0" : s)
                .mapToLong(Long::parseLong)
                .toArray();

        return new DepartureSchedule(minDepartureTime, lineIds);
    }

    public static record DepartureInfo(long minDepartureTime, long lineId) {}
}
