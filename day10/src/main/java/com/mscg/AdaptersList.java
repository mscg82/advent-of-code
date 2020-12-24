package com.mscg;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public record AdaptersList(long[] adapters) {

    public Map<Long, Long> countDifferences() {
        final var differences = new HashMap<Long, Long>();

        final var allAdapters = getAllAdapters();

        for (int i = 1; i < allAdapters.length; i++) {
            differences.merge(allAdapters[i] - allAdapters[i - 1], 1L, Long::sum);
        }

        return differences;
    }

    public long countArrangments() {
        final var allAdapters = getAllAdapters();

        var arragmentsPerNode = new TreeMap<Long, Long>();
        arragmentsPerNode.put(allAdapters[allAdapters.length - 1], 1L);

        for (int i = allAdapters.length - 2; i >= 0; i--) {
            long adapterI = allAdapters[i];
            for (int j = i + 1; j < allAdapters.length; j++) {
                long adapterJ = allAdapters[j];
                if (adapterJ - adapterI <= 3L) {
                    arragmentsPerNode.merge(adapterI, arragmentsPerNode.get(adapterJ), Long::sum);
                }
                else {
                    break;
                }
            }
        }

        return arragmentsPerNode.get(0L);
    }

    private long[] getAllAdapters() {
        long maxAdapter = Arrays.stream(adapters).max().orElseThrow();

        final var allAdapters = new long[adapters.length + 2];
        allAdapters[0] = 0L;
        allAdapters[1] = maxAdapter + 3;
        System.arraycopy(adapters, 0, allAdapters, 2, adapters.length);
        Arrays.sort(allAdapters);

        return allAdapters;
    }

    public static AdaptersList parseInput(BufferedReader in) {
        long[] adapters = in.lines()
                .mapToLong(Long::parseLong)
                .toArray();
        return new AdaptersList(adapters);
    }

}
