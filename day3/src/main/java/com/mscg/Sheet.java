package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public record Sheet(List<Area> areas) {

    public long measureOverlappingAreas() {
        final IntSummaryStatistics xStats = areas.stream() //
                .flatMapToInt(area -> IntStream.of(area.bottomLeft().x(), area.topRight().x())) //
                .summaryStatistics();
        final IntSummaryStatistics yStats = areas.stream() //
                .flatMapToInt(area -> IntStream.of(area.bottomLeft().y(), area.topRight().y())) //
                .summaryStatistics();

        final int[][] sheet = new int[xStats.getMax() - xStats.getMin() + 1][yStats.getMax() - yStats.getMin() + 1];
        for (final Area area : areas) {
            for (int x = area.bottomLeft().x(); x <= area.topRight().x(); x++) {
                final int xCoord = x - xStats.getMin();
                for (int y = area.bottomLeft().y(); y <= area.topRight().y(); y++) {
                    final int yCoord = y - yStats.getMin();
                    sheet[xCoord][yCoord]++;
                }
            }
        }

        return Arrays.stream(sheet)
                .flatMapToInt(Arrays::stream)
                .filter(i -> i > 1)
                .count();
    }

    public int findNonOverlappingArea() {
        final IntSummaryStatistics xStats = areas.stream() //
                .flatMapToInt(area -> IntStream.of(area.bottomLeft().x(), area.topRight().x())) //
                .summaryStatistics();
        final IntSummaryStatistics yStats = areas.stream() //
                .flatMapToInt(area -> IntStream.of(area.bottomLeft().y(), area.topRight().y())) //
                .summaryStatistics();

        final int[][] sheet = new int[xStats.getMax() - xStats.getMin() + 1][yStats.getMax() - yStats.getMin() + 1];
        for (final Area area : areas) {
            for (int x = area.bottomLeft().x(); x <= area.topRight().x(); x++) {
                final int xCoord = x - xStats.getMin();
                for (int y = area.bottomLeft().y(); y <= area.topRight().y(); y++) {
                    final int yCoord = y - yStats.getMin();
                    sheet[xCoord][yCoord]++;
                }
            }
        }

        for (final Area area : areas) {
            final int extension = (area.topRight().x() - area.bottomLeft().x() + 1) * (area.topRight().y() - area.bottomLeft().y() + 1);

            int sheetArea = 0;
            for (int x = area.bottomLeft().x(); x <= area.topRight().x(); x++) {
                final int xCoord = x - xStats.getMin();
                for (int y = area.bottomLeft().y(); y <= area.topRight().y(); y++) {
                    final int yCoord = y - yStats.getMin();
                    sheetArea += sheet[xCoord][yCoord];
                }
            }

            if (sheetArea == extension) {
                return area.id();
            }
        }

        return -1;
    }

    public static Sheet parseInput(final BufferedReader in) throws IOException {
        try {
            final var pattern = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");

            final List<Area> areas = in.lines() //
                    .map(pattern::matcher) //
                    .filter(Matcher::matches) //
                    .map(matcher -> {
                        final int id = Integer.parseInt(matcher.group(1));
                        final int x1 = Integer.parseInt(matcher.group(2));
                        final int y1 = Integer.parseInt(matcher.group(3));
                        final int width = Integer.parseInt(matcher.group(4));
                        final int height = Integer.parseInt(matcher.group(5));

                        return new Area(id, new Point(x1, y1), new Point(x1 + width - 1, y1 + height - 1));
                    }) //
                    .toList();
            return new Sheet(areas);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Point(int x, int y) {
    }

    public static record Area(int id, Point bottomLeft, Point topRight) {
    }

}
