package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;

@SuppressWarnings("SpellCheckingInspection")
public record AirductMap(List<List<Tile>> map, Map<Character, Position> poiToPosition, Map<Position, Character> positionToPoi) {

    public Path findShortestPath() {
        final Map<Position, List<Position>> adjacencyMap = new HashMap<>();
        for (int y = 0, rows = map.size(); y < rows; y++) {
            final List<Tile> row = map.get(y);
            for (int x = 0, l = row.size(); x < l; x++) {
                final var cell = row.get(x);
                if (cell == Tile.WALL) {
                    continue;
                }
                final List<Position> neighbours = Stream.of(new Position(x + 1, y), new Position(x, y + 1), new Position(x - 1, y), new Position(x, y - 1)) //
                        .filter(p -> p.x() >= 0 && p.y() >= 0 && p.x() < l && p.y() < rows) //
                        .filter(p -> map.get(p.y()).get(p.x()) != Tile.WALL) //
                        .toList();
                adjacencyMap.put(new Position(x, y), neighbours);
            }
        }

        record Step(Position position, Set<Position> poisToConsider, List<Character> visitedPois, int length) {
        }

        final Set<List<Character>> visitedChains = new HashSet<>();
        final Deque<Step> queue = new LinkedList<>();
        Path shortestPath = new Path(List.of(), Integer.MAX_VALUE);

        final Position start = poiToPosition.get('0');
        final Set<Position> poisToConsider = new HashSet<>(positionToPoi.keySet());
        poisToConsider.remove(start);

        final List<Character> initialChain = List.of('0');
        queue.add(new Step(start, poisToConsider, initialChain, 0));
        visitedChains.add(initialChain);

        while (!queue.isEmpty()) {
            final var step = queue.pop();
            if (step.poisToConsider().isEmpty()) {
                if (step.length() < shortestPath.length()) {
                    shortestPath = new Path(step.visitedPois(), step.length());
                }
                continue;
            }

            final Map<Position, Integer> nearestPois = findPathToNearestPois(adjacencyMap, step.position(), step.poisToConsider());
            nearestPois.forEach((pos, distance) -> {
                final char poi = positionToPoi.get(pos);
                final List<Character> newChain = Stream.concat(step.visitedPois().stream(), Stream.of(poi)).toList();
                if (!visitedChains.contains(newChain)) {
                    final Set<Position> newPoisToConsider = new HashSet<>(step.poisToConsider());
                    newPoisToConsider.remove(pos);
                    queue.add(new Step(pos, newPoisToConsider, newChain, step.length() + distance));
                    visitedChains.add(newChain);
                }
            });
        }

        return shortestPath;
    }

    private Map<Position, Integer> findPathToNearestPois(final Map<Position, List<Position>> adjacencyMap, final Position start, final Set<Position> poisToConsider) {
        record Step(Position position, int distance) {
        }

        final Set<Position> visitedPositions = new HashSet<>();
        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step(start, 0));
        visitedPositions.add(start);

        final List<Step> pois = new ArrayList<>();

        while (!queue.isEmpty()) {
            final var step = queue.pop();

            if (positionToPoi.containsKey(step.position())) {
                pois.add(step);
                if (pois.size() == positionToPoi.size()) {
                    break;
                }
            }

            adjacencyMap.get(step.position()).stream() //
                    .filter(p -> !visitedPositions.contains(p)) //
                    .forEach(p -> {
                        queue.add(new Step(p, step.distance() + 1));
                        visitedPositions.add(p);
                    });
        }

        return pois.stream() //
                .filter(s -> poisToConsider.contains(s.position())) //
                .collect(Collectors.toMap(Step::position, Step::distance));
    }

    public static AirductMap parseInput(final BufferedReader in) throws IOException {
        final Map<Character, Position> poiToPosition = new HashMap<>();
        final List<List<Tile>> map = new ArrayList<>();

        String line;
        int y = 0;
        final List<Tile> row = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            for (int x = 0, l = line.length(); x < l; x++) {
                final var tile = Tile.fromChar(line.charAt(x));
                row.add(tile);
                if (tile == Tile.POI) {
                    poiToPosition.put(line.charAt(x), new Position(x, y));
                }
            }
            map.add(List.copyOf(row));
            row.clear();
            y++;
        }

        final Map<Position, Character> positionToPoi = poiToPosition.entrySet().stream() //
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new AirductMap(List.copyOf(map), Map.copyOf(poiToPosition), Map.copyOf(positionToPoi));
    }

    @Override
    public String toString() {
        return StreamUtils.zipWithIndex(map.stream()) //
                .map((rowIdx -> {
                    final int y = (int) rowIdx.getIndex();
                    return StreamUtils.zipWithIndex(rowIdx.getValue().stream()) //
                            .map(cellIdx -> {
                                final int x = (int) cellIdx.getIndex();
                                return switch (cellIdx.getValue()) {
                                    case PASSAGE, WALL -> cellIdx.getValue().toString();
                                    case POI -> positionToPoi.get(new Position(x, y)).toString();
                                };
                            }) //
                            .collect(Collectors.joining());
                }))
                .collect(Collectors.joining("\n"));
    }

    public static record Path(List<Character> pois, int length) {
    }

    public static record Position(int x, int y) {
    }

    public enum Tile {
        WALL, PASSAGE, POI;

        @Override
        public String toString() {
            return switch (this) {
                case WALL -> "#";
                case PASSAGE -> ".";
                case POI -> " ";
            };
        }

        public static Tile fromChar(final char c) {
            return switch (c) {
                case '#' -> WALL;
                case '.' -> PASSAGE;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> POI;
                default -> throw new IllegalArgumentException("Unsupported char " + c);
            };
        }
    }
}
