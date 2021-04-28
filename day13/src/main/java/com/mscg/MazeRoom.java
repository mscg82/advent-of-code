package com.mscg;

import static com.mscg.MazeRoomPositionBuilder.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

public record MazeRoom(int seed) {

    public Path findPath(@NonNull final Position start, @NonNull final Position end, final int maxDepth) {
        record Step(Position current, Step previous, int depth) {
        }

        final Set<Position> seenPositions = new HashSet<>();
        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step(start, null, 0));

        while (!queue.isEmpty()) {
            final var step = queue.pop();
            final var current = step.current();

            if (step.depth() >= maxDepth) {
                return new Path(null, seenPositions.size());
            }

            if (current.equals(end)) {
                final var result = Arrays.asList(new Position[step.depth() + 1]);
                var solutionStep = step;
                do {
                    result.set(solutionStep.depth(), solutionStep.current());
                    solutionStep = solutionStep.previous();
                }
                while (solutionStep != null);
                return new Path(List.copyOf(result), seenPositions.size());
            }

            current.neighbours().stream() //
                    .filter(p -> p.isOpenSpace(seed)) //
                    .filter(p -> !seenPositions.contains(p)) //
                    .map(p -> new Step(p, step, step.depth() + 1)) //
                    .forEach(p -> {
                        queue.add(p);
                        seenPositions.add(p.current());
                    });
        }
        throw new IllegalArgumentException("Can't reach " + end + " from " + start);
    }

    public static MazeRoom parseInput(final BufferedReader in) throws IOException {
        return new MazeRoom(Integer.parseInt(in.readLine()));
    }

    public static record Path(List<Position> positions, int seenPositions) {

    }

    @RecordBuilder
    public static record Position(int x, int y) {

        public List<Position> neighbours() {
            final var positions = new ArrayList<Position>(4);
            if (x != 0) {
                positions.add(Position(x - 1, y));
            }
            positions.add(Position(x + 1, y));
            if (y != 0) {
                positions.add(Position(x, y - 1));
            }
            positions.add(Position(x, y + 1));
            return List.copyOf(positions);
        }

        public boolean isOpenSpace(final int seed) {
            final long x = this.x;
            final long y = this.y;
            final long[] value = new long[] { x * x + 3 * x + 2 * x * y + y + y * y + seed };

            return (BitSet.valueOf(value).cardinality() % 2) == 0;
        }

    }
}
