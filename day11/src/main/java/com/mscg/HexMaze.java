package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class HexMaze {

    private final InitedTiles initedTiles;
    private final List<Direction> directions;

    public HexMaze(final int maxLayerWidth, final List<Direction> directions) {
        this.initedTiles = initTiles(maxLayerWidth);
        this.directions = directions;
    }

    public int findDistance() {
        final Diagonals diagonals = computeDiagonals();

        Tile targetTile = initedTiles.startingTile();
        for (final var direction : directions) {
            targetTile = targetTile.getNeighbours().get(direction);
        }

        return computeDistanceFromStart(targetTile, diagonals);
    }

    public int findMaxDistance() {
        final Diagonals diagonals = computeDiagonals();

        final List<Tile> targetTiles = new ArrayList<>(directions.size());
        Tile targetTile = initedTiles.startingTile();
        targetTiles.add(targetTile);
        for (final var direction : directions) {
            targetTile = targetTile.getNeighbours().get(direction);
            targetTiles.add(targetTile);
        }

        return targetTiles.stream() //
                .parallel() //
                .mapToInt(tile -> computeDistanceFromStart(tile, diagonals)) //
                .max() //
                .orElseThrow();
    }

    private Diagonals computeDiagonals() {
        final Stream<Tile> upperDescDiagonal = Stream.iterate(initedTiles.startingTile(), Objects::nonNull, t -> t.getNeighbours().get(Direction.NW));
        final Stream<Tile> lowerDescDiagonal = Stream.iterate(initedTiles.startingTile(), Objects::nonNull, t -> t.getNeighbours().get(Direction.SE));
        final Set<Integer> idsInDescDiagonal = Stream.concat(upperDescDiagonal, lowerDescDiagonal) //
                .map(Tile::getId) //
                .collect(Collectors.toUnmodifiableSet());

        final Stream<Tile> upperAscDiagonal = Stream.iterate(initedTiles.startingTile(), Objects::nonNull, t -> t.getNeighbours().get(Direction.NE));
        final Stream<Tile> lowerAscDiagonal = Stream.iterate(initedTiles.startingTile(), Objects::nonNull, t -> t.getNeighbours().get(Direction.SW));
        final Set<Integer> idsInAscDiagonal = Stream.concat(upperAscDiagonal, lowerAscDiagonal) //
                .map(Tile::getId) //
                .collect(Collectors.toUnmodifiableSet());

        return new Diagonals(idsInDescDiagonal, idsInAscDiagonal);
    }

    private int computeDistanceFromStart(final Tile targetTile, final Diagonals diagonals) {
        Tile currentTile = targetTile;
        int steps = 0;
        while (!diagonals.idsInDescDiagonal().contains(currentTile.getId()) && !diagonals.idsInAscDiagonal().contains(currentTile.getId())) {
            if (currentTile.getId() > initedTiles.startingTile().getId()) {
                currentTile = currentTile.getNeighbours().get(Direction.S);
            } else {
                currentTile = currentTile.getNeighbours().get(Direction.N);
            }
            steps++;
        }

        if (diagonals.idsInDescDiagonal().contains(currentTile.getId())) {
            return steps + Math.abs(currentTile.getId() - initedTiles.startingTile().getId());
        } else {
            while (!currentTile.equals(initedTiles.startingTile())) {
                if (currentTile.getId() > initedTiles.startingTile().getId()) {
                    currentTile = currentTile.getNeighbours().get(Direction.SW);
                } else {
                    currentTile = currentTile.getNeighbours().get(Direction.NE);
                }
                steps++;
            }
            return steps;
        }
    }

    private InitedTiles initTiles(final int maxLayerWidth) {
        final Tile startingTile;
        final List<Tile> allTiles = new ArrayList<>(maxLayerWidth * maxLayerWidth);

        int id = 0;

        List<Tile> previousLayer = List.of(new Tile(id++));
        allTiles.addAll(previousLayer);

        for (int width = 2; width <= maxLayerWidth; width++) {
            final List<Tile> currentLayer = new ArrayList<>(width);
            for (int i = 0; i < width; i++) {
                final var tile = new Tile(id++);
                allTiles.add(tile);
                currentLayer.add(tile);
                if (i != 0) {
                    tile.connect(previousLayer.get(i - 1), Direction.SW);
                    tile.connect(currentLayer.get(i - 1), Direction.NW);
                }
                if (i < previousLayer.size()) {
                    tile.connect(previousLayer.get(i), Direction.S);
                }
            }
            previousLayer = currentLayer;
        }

        startingTile = previousLayer.get(maxLayerWidth / 2);

        for (int width = maxLayerWidth - 1; width >= 1; width--) {
            final List<Tile> currentLayer = new ArrayList<>(width);
            for (int i = 0; i < width; i++) {
                final var tile = new Tile(id++);
                allTiles.add(tile);
                currentLayer.add(tile);
                if (i != 0) {
                    tile.connect(currentLayer.get(i - 1), Direction.NW);
                }
                tile.connect(previousLayer.get(i), Direction.SW);
                tile.connect(previousLayer.get(i + 1), Direction.S);
            }
            previousLayer = currentLayer;
        }

        return new InitedTiles(startingTile, allTiles);
    }

    public static HexMaze parseInput(final BufferedReader in, final int maxLayerWidth) throws IOException {
        final List<Direction> directions = Arrays.stream(in.readLine().split(",")) //
                .map(Direction::from) //
                .toList();
        return new HexMaze(maxLayerWidth, directions);
    }

    public enum Direction {
        N, NE, SE, S, SW, NW;

        public Direction opposite() {
            return switch (this) {
                case N -> Direction.S;
                case NE -> Direction.SW;
                case SE -> Direction.NW;
                case S -> Direction.N;
                case SW -> Direction.NE;
                case NW -> Direction.SE;
            };
        }

        public static Direction from(final String value) {
            return switch (value.toLowerCase()) {
                case "n" -> N;
                case "ne" -> NE;
                case "se" -> SE;
                case "s" -> S;
                case "sw" -> SW;
                case "nw" -> NW;
                default -> throw new IllegalArgumentException("Invalid direction " + value);
            };
        }
    }

    @Getter
    @EqualsAndHashCode(of = "id")
    public static class Tile {
        private final int id;
        private final Map<Direction, Tile> neighbours;

        public Tile(final int id) {
            this.id = id;
            this.neighbours = new EnumMap<>(Direction.class);
        }

        public void connect(final Tile other, final Direction direction) {
            this.neighbours.put(direction, other);
            other.neighbours.put(direction.opposite(), this);
        }
    }

    private static record InitedTiles(Tile startingTile, List<Tile> allTiles) {

    }

    private record Diagonals(Set<Integer> idsInDescDiagonal, Set<Integer> idsInAscDiagonal) {

    }
}
