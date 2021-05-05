package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.soabase.recordbuilder.core.RecordBuilder;

public record DiskGrid(List<Disk> disks) {

    public int countStepsToExtractData() {
        final var positionToDisk = disks.stream().collect(Collectors.toMap(Disk::position, d -> d));

        final var adjacencyMap = disks.stream() //
                .collect(Collectors.toMap(Disk::position, disk -> //
                        Stream.of( //
                                positionToDisk.get(disk.position().withY(disk.position().y() - 1)), //
                                positionToDisk.get(disk.position().withX(disk.position().x() + 1)), //
                                positionToDisk.get(disk.position().withY(disk.position().y() + 1)), //
                                positionToDisk.get(disk.position().withX(disk.position().x() - 1)) //
                        ) //
                                .filter(Objects::nonNull) //
                                .filter(d -> d.used() < 100) //
                                .toList()));

        // find the only empty node
        final Disk startDisk = disks.stream() //
                .filter(disk -> disk.used() == 0) //
                .findFirst() //
                .orElseThrow();

        // top right corner disk
        final Disk topRightDisk = disks.stream() //
                .filter(disk -> disk.position().y() == 0) //
                .max(Comparator.comparingInt(disk -> disk.position().x())) //
                .orElseThrow();

        final Disk nearTopRightDisk = positionToDisk.get(topRightDisk.position().withX(topRightDisk.position().x() - 1));

        // find path from start disk to top right corner
        record Step(Disk disk, int depth) {

        }
        final Set<Disk> visitedDisks = new HashSet<>();
        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step(startDisk, 0));
        visitedDisks.add(startDisk);
        int distance = -1;

        while (!queue.isEmpty()) {
            final var currentStep = queue.pop();
            if (currentStep.disk().equals(nearTopRightDisk)) {
                distance = currentStep.depth();
                break;
            }

            adjacencyMap.get(currentStep.disk().position()).stream() //
                    .filter(disk -> !visitedDisks.contains(disk)) //
                    .forEach(disk -> {
                        queue.add(new Step(disk, currentStep.depth() + 1));
                        visitedDisks.add(disk);
                    });
        }

        if (distance == -1) {
            throw new IllegalStateException("Can't reach top right corner from empty disk");
        }

        // from nearTopRightDisk you do a move right and you have the empty node
        // on the left of the goal node. From there on, you can loop the empty node
        // around the goal node to move it one cell to the left. It takes 5 moves.
        // From that, the following formula arises
        return distance + 1 + 5 * nearTopRightDisk.position().x();
    }

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
                        return new Disk(Position.fromName(parts[0]), //
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

    @RecordBuilder
    public static record Position(int x, int y) implements DiskGridPositionBuilder.With {

        public static Position fromName(final String name) {
            final var pattern = Pattern.compile(".+-x(\\d+)-y(\\d+)");
            final var matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Can't extract position from name " + name);
            }
            return new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }

    }

    public static record Disk(Position position, int size, int used, int available) {

    }

}
