package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

public record MessageBuilder(List<MsgPoint> points) {

    public List<MessageInfo> findMessage(final int maxIterations) {
        List<MsgPoint> points = List.copyOf(this.points);
        long minHeight = computeHeightDiff(points);
        final List<PointInfo> candidateMessages = new ArrayList<>();
        candidateMessages.add(new PointInfo(points, 0));

        List<MsgPoint> newPoints;
        for (int i = 1; i <= maxIterations; i++) {
            newPoints = points.stream() //
                    .map(msgPoint -> msgPoint.withPoint(msgPoint.point().with(p -> {
                        p.x(p.x() + msgPoint.speed().dx());
                        p.y(p.y() + msgPoint.speed().dy());
                    }))) //
                    .toList();
            points = newPoints;
            final long height = computeHeightDiff(points);
            if (height < minHeight) {
                minHeight = height;
                candidateMessages.clear();
                candidateMessages.add(new PointInfo(points, i));
            } else if (height == minHeight) {
                candidateMessages.add(new PointInfo(points, i));
            }
        }

        return candidateMessages.stream() //
                .map(this::printPoints) //
                .toList();
    }

    private MessageInfo printPoints(final PointInfo pointsInfo) {
        final LongSummaryStatistics xStats = pointsInfo.points().stream() //
                .mapToLong(p -> p.point().x()) //
                .summaryStatistics();
        final LongSummaryStatistics yStats = pointsInfo.points().stream() //
                .mapToLong(p -> p.point().y()) //
                .summaryStatistics();

        final Set<Point> allPoints = pointsInfo.points().stream() //
                .map(MsgPoint::point) //
                .collect(Collectors.toSet());

        final var res = new StringBuilder();
        for (long y = yStats.getMin(); y <= yStats.getMax(); y++) {
            for (long x = xStats.getMin(); x <= xStats.getMax(); x++) {
                if (allPoints.contains(new Point(x, y))) {
                    res.append('#');
                } else {
                    res.append('.');
                }
            }
            res.append('\n');
        }

        return new MessageInfo(res.toString(), pointsInfo.time());
    }

    private long computeHeightDiff(final List<MsgPoint> points) {
        final LongSummaryStatistics statistics = points.stream() //
                .mapToLong(p -> p.point().y()) //
                .summaryStatistics();
        return statistics.getMax() - statistics.getMin() + 1;
    }

    public static MessageBuilder parseInput(final BufferedReader in) throws IOException {
        try {
            final var pattern = Pattern.compile("position=<(.+?), (.+?)> velocity=<(.+?), (.+?)>");
            final List<MsgPoint> points = in.lines() //
                    .map(pattern::matcher) //
                    .filter(Matcher::matches) //
                    .map(matcher -> new MsgPoint( //
                            new Point(Long.parseLong(matcher.group(1).trim()), Long.parseLong(matcher.group(2).trim())), //
                            new Speed(Long.parseLong(matcher.group(3).trim()), Long.parseLong(matcher.group(4).trim())) //
                    )) //
                    .toList();
            return new MessageBuilder(points);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static record PointInfo(List<MsgPoint> points, int time) {

    }

    public static record MessageInfo(String message, int time) {

    }

    @RecordBuilder
    public static record Point(long x, long y) implements MessageBuilderPointBuilder.With {

    }

    public static record Speed(long dx, long dy) {

    }

    @RecordBuilder
    public static record MsgPoint(Point point, Speed speed) implements MessageBuilderMsgPointBuilder.With {

    }

}
