package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record VentReadings(List<Vent> readings) {

    public static VentReadings parseInput(BufferedReader in) throws IOException {
        try {
            final List<Vent> readings = in.lines() //
                    .map(Vent::parse) //
                    .toList();
            return new VentReadings(readings);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public long countDangerousPoints1() {
        final Map<Point, Long> pointToFrequency = readings.stream() //
                .filter(Vent::isHorizontalOrVertical) //
                .flatMap(Vent::getAllPoint) //
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        return pointToFrequency.values().stream() //
                .mapToLong(Long::longValue) //
                .filter(v -> v > 1) //
                .count();
    }

    public long countDangerousPoints2() {
        final Map<Point, Long> pointToFrequency = readings.stream() //
                .flatMap(Vent::getAllPoint) //
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        return pointToFrequency.values().stream() //
                .mapToLong(Long::longValue) //
                .filter(v -> v > 1) //
                .count();
    }

    public record Vent(Point p1, Point p2) {

        public static Vent parse(String input) {
            String[] parts = input.split(" -> ");
            return new Vent(Point.parse(parts[0]), Point.parse(parts[1]));
        }

        public boolean isHorizontal() {
            return p1.y() == p2.y();
        }

        public boolean isVertical() {
            return p1.x() == p2.x();
        }

        public boolean isHorizontalOrVertical() {
            return isHorizontal() || isVertical();
        }

        public Stream<Point> getAllPoint() {
            if (isHorizontal()) {
                return LongStream.rangeClosed(Math.min(p1.x(), p2.x()), Math.max(p1.x(), p2.x())) //
                        .mapToObj(x -> new Point(x, p1.y()));
            } else if (isVertical()) {
                return LongStream.rangeClosed(Math.min(p1.y(), p2.y()), Math.max(p1.y(), p2.y())) //
                        .mapToObj(y -> new Point(p1.x(), y));
            } else {
                long dx = p1.x() > p2.x() ? -1L : 1L;
                long dy = p1.y() > p2.y() ? -1L : 1L;

                return Stream.concat( //
                        // generate all point in the diagonal [p1, p2)
                        Stream.iterate(p1, //
                                p -> !p.equals(p2), //
                                p -> p.with(pb -> pb.x(pb.x() + dx).y(pb.y() + dy))), //

                        // add p2 at the end since previous stream doesn't produce it
                        Stream.of(p2));
            }
        }

    }

    @RecordBuilder
    public record Point(long x, long y) implements VentReadingsPointBuilder.With {

        public static Point parse(String input) {
            String[] parts = input.split(",");
            return new Point(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }

    }

}
