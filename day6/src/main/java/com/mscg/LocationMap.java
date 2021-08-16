package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record LocationMap(List<Point> points) {

    public int findBiggestArea() {
        final var area = computeArea();

        record PositionInfo(int minDistance, List<Integer> points) {

        }

        final PositionInfo[][] areaPoints = new PositionInfo[area.xMax() - area.xMin() + 1][area.yMax() - area.yMin() + 1];

        for (int x = area.xMin(); x <= area.xMax(); x++) {
            for (int y = area.yMin(); y <= area.yMax(); y++) {
                final var areaPoint = new Point(x, y);
                int minDistance = Integer.MAX_VALUE;
                final var nearPoints = new ArrayList<Integer>();
                for (final var it = points.listIterator(); it.hasNext(); ) {
                    final var idx = it.nextIndex();
                    final var point = it.next();
                    final int distance = point.distance(areaPoint);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearPoints.clear();
                        nearPoints.add(idx);
                    } else if (distance == minDistance) {
                        nearPoints.add(idx);
                    }
                }
                areaPoints[x - area.xMin()][y - area.yMin()] = new PositionInfo(minDistance, List.copyOf(nearPoints));
            }
        }

        final Set<Integer> boundaryNodes = new HashSet<>();
        for (final PositionInfo areaPointInfo : areaPoints[0]) {
            if (areaPointInfo.points().size() == 1) {
                boundaryNodes.add(areaPointInfo.points().get(0));
            }
        }
        for (final PositionInfo areaPointInfo : areaPoints[areaPoints.length - 1]) {
            if (areaPointInfo.points().size() == 1) {
                boundaryNodes.add(areaPointInfo.points().get(0));
            }
        }
        for (int x = 0; x < areaPoints.length - 1; x++) {
            if (areaPoints[x][0].points().size() == 1) {
                boundaryNodes.add(areaPoints[x][0].points().get(0));
            }
        }
        final int cols = areaPoints[0].length;
        for (int x = 0; x < areaPoints.length - 1; x++) {
            if (areaPoints[x][cols - 1].points().size() == 1) {
                boundaryNodes.add(areaPoints[x][cols - 1].points().get(0));
            }
        }

        final Map<Integer, Integer> pointToArea = new HashMap<>();
        for (final PositionInfo[] row : areaPoints) {
            for (final PositionInfo info : row) {
                if (info.points().size() > 1) {
                    continue;
                }
                final int point = info.points().get(0);
                if (boundaryNodes.contains(point)) {
                    continue;
                }
                pointToArea.merge(point, 1, Integer::sum);
            }
        }

        return pointToArea.entrySet().stream() //
                .max(Map.Entry.comparingByValue()) //
                .map(Map.Entry::getValue) //
                .orElseThrow();
    }

    private Area computeArea() {
        final IntSummaryStatistics xStats = points.stream() //
                .mapToInt(Point::x) //
                .summaryStatistics();

        final IntSummaryStatistics yStats = points.stream() //
                .mapToInt(Point::y) //
                .summaryStatistics();

        return new Area(xStats.getMin(), xStats.getMax(), yStats.getMin(), yStats.getMax());
    }

    public static LocationMap parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Point> points = in.lines() //
                    .map(line -> {
                        final String[] parts = line.split(",");
                        return new Point(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
                    }) //
                    .toList();
            return new LocationMap(points);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Point(int x, int y) {

        public int distance(final Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

    }

    public static record Area(int xMin, int xMax, int yMin, int yMax) {

    }

}
