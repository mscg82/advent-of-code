package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record DiskGrid(List<Disk> disks) {

    public int countViablePairs() {
        int count = 0;
        for (int i = 0, l = disks.size(); i < l - 1; i++) {
            for (int j = i + 1; j < l; j++) {
                if (disks.get(i).used() != 0 && disks.get(i).used() <= disks.get(j).available()) {
                    count++;
                } else if (disks.get(j).used() != 0 && disks.get(j).used() <= disks.get(i).available()) {
                    count++;
                }
            }
        }
        return count;
    }

    public static DiskGrid parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Disk> disks = in.lines() //
                    .skip(2) //
                    .map(line -> {
                        final String[] parts = line.split("\\s+");
                        return new Disk(parts[0], //
                                Integer.parseInt(parts[1].substring(0, parts[1].length() - 1)), //
                                Integer.parseInt(parts[2].substring(0, parts[2].length() - 1)), //
                                Integer.parseInt(parts[3].substring(0, parts[3].length() - 1)));
                    }) //
                    .toList();
            return new DiskGrid(disks);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Disk(String path, int size, int used, int available) {

    }

}
