package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

public class Floor implements Cloneable {

    private final List<List<Direction>> paths;
    private final Tile startingTile;
    private final List<Tile> allTiles;

    public Floor(final List<List<Direction>> paths, int maxLayerWidth) {
        if (maxLayerWidth % 2 == 0) {
            throw new IllegalArgumentException("maxlayerWidth must be odd");
        }
        this.paths = paths;
        
        InitedTiles initedTiles = initTiles(maxLayerWidth);
        
        this.startingTile = initedTiles.startingTile;
        this.allTiles = List.copyOf(initedTiles.allTiles());
    }

    @Override
    public Floor clone() {
        List<List<Direction>> clonedPaths = this.paths.stream() //
                .map(dirs -> List.copyOf(dirs)) //
                .collect(Collectors.toList());
        int maxLayerWidth = (int) Math.sqrt(this.allTiles.size());
        Floor floor = new Floor(List.copyOf(clonedPaths), maxLayerWidth);

        // copy colors
        for (var it = this.allTiles.listIterator(); it.hasNext();) {
            int idx = it.nextIndex();
            Tile tile = it.next();
            floor.allTiles.get(idx).color = tile.color;
        }

        return floor;
    }

    private InitedTiles initTiles(int maxLayerWidth) {
        final Tile startingTile;
        final List<Tile> allTiles = new ArrayList<>(maxLayerWidth * maxLayerWidth);

        List<Tile> previousLayer = List.of(new Tile());
        allTiles.addAll(previousLayer);

        for (int width = 2; width <= maxLayerWidth; width++) {
            List<Tile> currentLayer = new ArrayList<>(width);
            for (int i = 0; i < width; i++) {
                var tile = new Tile();
                allTiles.add(tile);
                currentLayer.add(tile);
                if (i != 0) {
                    tile.connect(previousLayer.get(i - 1), Direction.NW);
                    tile.connect(currentLayer.get(i - 1), Direction.W);
                }
                if (i < previousLayer.size()) {
                    tile.connect(previousLayer.get(i), Direction.NE);
                }
            }
            previousLayer = currentLayer;
        }

        startingTile = previousLayer.get(maxLayerWidth / 2);

        for (int width = maxLayerWidth - 1; width >= 1; width--) {
            List<Tile> currentLayer = new ArrayList<>(width);
            for (int i = 0; i < width; i++) {
                var tile = new Tile();
                allTiles.add(tile);
                currentLayer.add(tile);
                if (i != 0) {
                    tile.connect(currentLayer.get(i - 1), Direction.W);
                }
                tile.connect(previousLayer.get(i), Direction.NW);
                tile.connect(previousLayer.get(i + 1), Direction.NE);
            }
            previousLayer = currentLayer;
        }

        return new InitedTiles(startingTile, allTiles);
    }

    public void run() {
        for (final var directions : paths) {
            Tile currentTile = startingTile;
            for (final var dir : directions) {
                currentTile = currentTile.getNeighbours().get(dir);
            }
            currentTile.flipColor();
        }
    }

    public Floor evolve() {
        Floor cloned = this.clone();

        for (var it = cloned.allTiles.listIterator(); it.hasNext();) {
            int idx = it.nextIndex();
            Tile tile = it.next();
            Map<Direction, Tile> neighbours = this.allTiles.get(idx).neighbours;
            tile.computeNewColor(neighbours);
        }

        return cloned;
    }

    public long countBlackTiles() {
        return allTiles.stream() //
                .filter(tile -> tile.color == Color.BLACK) //
                .count();
    }

    public static Floor parseInput(final BufferedReader in, int maxLayerWidth) throws IOException {
        final List<List<Direction>> paths = in.lines() //
                .map(Direction::parseLine) //
                .collect(Collectors.toList());

        return new Floor(List.copyOf(paths), maxLayerWidth);
    }

    @Getter
    public static class Tile {
        private Color color;
        private final Map<Direction, Tile> neighbours;

        public Tile() {
            this.color = Color.WHITE;
            this.neighbours = new EnumMap<>(Direction.class);
        }

        public void connect(Tile other, Direction direction) {
            this.neighbours.put(direction, other);
            other.neighbours.put(direction.opposite(), this);
        }

        public void flipColor() {
            color = switch (color) {
                case BLACK -> Color.WHITE;
                case WHITE -> Color.BLACK;
            };
        }

        public void computeNewColor(Map<Direction, Tile> neighbours) {
            long blackNeighbours = neighbours.values().stream() //
                    .filter(tile -> tile.getColor() == Color.BLACK) //
                    .count();
            color = switch (color) {
                case BLACK -> {
                    if (blackNeighbours == 0 || blackNeighbours > 2) {
                        yield Color.WHITE;
                    }
                    else {
                        yield Color.BLACK;
                    }
                }
                case WHITE -> {
                    if (blackNeighbours == 2) {
                        yield Color.BLACK;
                    }
                    else {
                        yield Color.WHITE;
                    }
                }
            };
        }
    }

    private static record InitedTiles(Tile startingTile, List<Tile> allTiles) {}

    public enum Color {
        BLACK, WHITE;
    }

    public enum Direction {
        E, SE, SW, W, NW, NE;

        public Direction opposite() {
            return switch (this) {
                case E -> Direction.W;
                case SE -> Direction.NW;
                case SW -> Direction.NE;
                case W -> Direction.E;
                case NW -> Direction.SE;
                case NE -> Direction.SW;
            };
        }

        public static List<Direction> parseLine(String line) {
            final List<Direction> directions = new ArrayList<>();

            line = line.toLowerCase();
            for (int i = 0, l = line.length(); i < l; i++) {
                final Direction dir = switch (line.charAt(i)) {
                    case 'e' -> Direction.E;
                    case 'w' -> Direction.W;
                    case 'n' -> {
                        i++;
                        yield switch (line.charAt(i)) {
                            case 'e' -> Direction.NE;
                            case 'w' -> Direction.NW;
                            default -> throw new IllegalArgumentException("Invalid directions line");
                        };
                    }
                    case 's' -> {
                        i++;
                        yield switch (line.charAt(i)) {
                            case 'e' -> Direction.SE;
                            case 'w' -> Direction.SW;
                            default -> throw new IllegalArgumentException("Invalid directions line");
                        };
                    }
                    default -> throw new IllegalArgumentException("Invalid directions line");
                };
                directions.add(dir);
            }

            return List.copyOf(directions);
        }
    }

}