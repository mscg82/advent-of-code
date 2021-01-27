package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mscg.Tile.Pixel;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Tileset {

    private static <T> List<List<T>> immutableMatrix(List<List<T>> orig) {
        for (var it = orig.listIterator(); it.hasPrevious();) {
            List<T> row = it.next();
            it.set(List.copyOf(row));
        }
        return List.copyOf(orig);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<List<T>> cast(List<?> source, Class<T> clazz) {
        return (List<List<T>>) source;
    }

    public static <T> List<List<T>> rotate(List<List<T>> orig) {
        int rows = orig.size();
        int cols = orig.get(0).size();
        List<List<T>> rotated = IntStream.range(0, cols) //
                .mapToObj(i -> IntStream.range(0, rows) //
                        .mapToObj(j -> (T) null) //
                        .collect(Collectors.toList())) //
                .collect(Collectors.toList());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated.get(j).set(rows - i - 1, orig.get(i).get(j));
            }
        }

        return immutableMatrix(rotated);
    }

    public static Tile rebuildImage(List<List<Tile>> pieces) {
        int rowsInPieces = pieces.get(0).get(0).image().size();
        int colsInPieces = cast(pieces.get(0).get(0).image(), Pixel.class).get(0).size();

        List<List<Pixel>> image = IntStream.range(0, (rowsInPieces - 2) * pieces.size()) //
                .mapToObj(i -> Arrays.asList(new Pixel[(colsInPieces - 2) * pieces.get(0).size()])) //
                .collect(Collectors.toList());

        for (int i = 0, r = pieces.size(); i < r; i++) {
            List<Tile> row = pieces.get(i);
            for (int j = 0, c = row.size(); j < c; j++) {
                Tile tile = row.get(j);
                List<List<Pixel>> tileImage = cast(tile.image(), Pixel.class);
                for (int k1 = 1; k1 < rowsInPieces - 1; k1++) {
                    for (int k2 = 1; k2 < colsInPieces - 1; k2++) {
                        image.get(i * (rowsInPieces - 2) + (k1 - 1)).set(j * (colsInPieces - 2) + (k2 - 1), tileImage.get(k1).get(k2));
                    }
                }
            }
        }

        return new Tile(0L, immutableMatrix(image));
    }

    private final List<Tile> tiles;

    public List<Tile> tiles() {
        return tiles;
    }

    public long validate() {
        Map<Long, List<Tile>> adjacencyMap = getAdjacencyMap();
        return adjacencyMap.entrySet().stream() //
                .filter(entry -> entry.getValue().size() == 2) //
                .mapToLong(Map.Entry::getKey) //
                .reduce(1L, (res, val) -> res * val);
    }

    public Map<Long, List<Tile>> getAdjacencyMap() {
        Map<Long, List<Tile>> adjacencyMap = getModifiableAdjacencyMap();

        // Make everything immutable
        adjacencyMap.replaceAll((id, tiles) -> List.copyOf(tiles));
        return Map.copyOf(adjacencyMap);
    }

    private Map<Long, List<Tile>> getModifiableAdjacencyMap() {
        Map<Long, List<Tile>> adjacencyMap = new HashMap<>();

        for (int i = 0, l = tiles.size(); i < l - 1; i++) {
            Tile first = tiles.get(i);
            for (int j = i + 1; j < l; j++) {
                Tile second = tiles.get(j);
                if (first.isAdjacentTo(second)) {
                    adjacencyMap.computeIfAbsent(first.id(), __ -> new ArrayList<>()).add(second);
                    adjacencyMap.computeIfAbsent(second.id(), __ -> new ArrayList<>()).add(first);
                }
            }
        }

        return adjacencyMap;
    }

    public List<List<Long>> arrangeTileIds() {
        Map<Long, List<Tile>> adjacencyMap = getModifiableAdjacencyMap();
        int sideLength = (int) Math.floor(Math.sqrt(tiles.size()));
        List<List<Long>> arrangedTiles = IntStream.range(0, sideLength) //
                .mapToObj(i -> IntStream.range(0, sideLength) //
                        .mapToObj(j -> 0L) //
                        .collect(Collectors.toList())) //
                .collect(Collectors.toList());

        long[] corners = adjacencyMap.entrySet().stream() //
                .filter(entry -> entry.getValue().size() == 2) //
                .mapToLong(Map.Entry::getKey) //
                .sorted() //
                .toArray();

        Map<Long, List<Long>> paths = getPathsFromNode(corners[0], adjacencyMap);
        List<List<Long>> pathToCorners = Arrays.stream(corners) //
                .filter(id -> paths.getOrDefault(id, List.of()).size() == sideLength) //
                .mapToObj(paths::get) //
                .collect(Collectors.toList());
        Set<Long> visitedNodes = new HashSet<>();
        List<Long> row = arrangedTiles.get(0);
        for (int i = 0; i < sideLength; i++) {
            long node = pathToCorners.get(0).get(i);
            row.set(i, node);
            visitedNodes.add(node);
        }
        for (int i = 1; i < sideLength; i++) {
            long node = pathToCorners.get(1).get(i);
            arrangedTiles.get(i).set(0, node);
            visitedNodes.add(node);
        }

        for (int i = 1; i < sideLength; i++) {
            for (int j = 1; j < sideLength; j++) {
                long upNode = arrangedTiles.get(i - 1).get(j);
                long leftNode = arrangedTiles.get(i).get(j - 1);
                Set<Long> upNodeNeighbours = adjacencyMap.get(upNode).stream() //
                        .map(Tile::id) //
                        .collect(Collectors.toSet());
                Set<Long> leftNodeNeighbours = adjacencyMap.get(leftNode).stream() //
                        .map(Tile::id) //
                        .collect(Collectors.toSet());
                upNodeNeighbours.retainAll(leftNodeNeighbours);
                upNodeNeighbours.removeAll(visitedNodes);
                long node = upNodeNeighbours.iterator().next();
                arrangedTiles.get(i).set(j, node);
                visitedNodes.add(node);
            }
        }

        return List.copyOf(arrangedTiles.stream() //
                .map(List::copyOf) //
                .collect(Collectors.toList()));
    }

    public List<List<Tile>> arrangeTiles(List<List<Long>> arrangedIds) {
        int rows = arrangedIds.size();
        int cols = arrangedIds.get(0).size();
        List<List<Tile>> arrangedTiles = IntStream.range(0, arrangedIds.size()) //
                .mapToObj(__ -> Arrays.asList(new Tile[cols])) //
                .collect(Collectors.toList());

        Map<Long, Tile> mappedTiles = tiles.stream() //
                .collect(Collectors.toMap(Tile::id, t -> t));

        Tile firstTile = flipFirstTile(orientFirstTile(arrangedIds, mappedTiles), arrangedIds, mappedTiles);
        arrangedTiles.get(0).set(0, firstTile);
        // build the first row
        for (int j = 1; j < cols; j++) {
            final int currentCol = j;
            Tile currentTile = arrangedTiles.get(0).get(j - 1);
            List<Pixel> lastCol = cast(currentTile.image(), Pixel.class).stream() //
                    .map(row -> row.get(row.size() - 1)) //
                    .collect(Collectors.toList());

            Tile nextTile = mappedTiles.get(arrangedIds.get(0).get(j));
            Tile placedNextTile = Stream.of(nextTile, nextTile.flipHor(), //
                    nextTile.flipVer(), nextTile.flipHor().flipVer()) //
                    .flatMap(Tile::rotations) //
                    .filter(nextTileVar -> {
                        List<Pixel> firstCol = cast(nextTileVar.image(), Pixel.class).stream() //
                                .map(row -> row.get(0)) //
                                .collect(Collectors.toList());
                        return firstCol.equals(lastCol);
                    }) //
                    .findAny() //
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Unable to find orientation for tile (0, " + currentCol + ")"));
            arrangedTiles.get(0).set(j, placedNextTile);
        }

        // build all other rows
        for (int i = 1; i < rows; i++) {
            final int currentRow = i;
            for (int j = 0; j < cols; j++) {
                final int currentCol = j;
                Tile currentTile = arrangedTiles.get(i - 1).get(j);
                List<Pixel> lastRow = cast(currentTile.image(), Pixel.class).get(currentTile.image().size() - 1);

                Tile nextTile = mappedTiles.get(arrangedIds.get(i).get(j));
                Tile placedNextTile = Stream.of(nextTile, nextTile.flipHor(), //
                        nextTile.flipVer(), nextTile.flipHor().flipVer()) //
                        .flatMap(Tile::rotations) //
                        .filter(nextTileVar -> {
                            List<Pixel> firstRow = cast(nextTileVar.image(), Pixel.class).get(0);
                            return firstRow.equals(lastRow);
                        }) //
                        .findAny() //
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Unable to find orientation for tile (" + currentRow + ", " + currentCol + ")"));
                arrangedTiles.get(i).set(j, placedNextTile);
            }
        }

        return immutableMatrix(arrangedTiles);
    }

    private Tile orientFirstTile(List<List<Long>> arrangedIds, Map<Long, Tile> mappedTiles) {
        Tile secondTile = mappedTiles.get(arrangedIds.get(0).get(1));
        List<Tile> secondTileVariations = Stream.of(secondTile, secondTile.flipHor(), //
                secondTile.flipVer(), secondTile.flipHor().flipVer()) //
                .flatMap(Tile::rotations) //
                .collect(Collectors.toList());

        return mappedTiles.get(arrangedIds.get(0).get(0)).rotations() //
                .filter(tile -> {
                    List<Pixel> lastCol = cast(tile.image(), Pixel.class).stream() //
                            .map(row -> row.get(row.size() - 1)) //
                            .collect(Collectors.toList());
                    return secondTileVariations.stream() //
                            .anyMatch(secondTileVar -> {
                                List<Pixel> firstCol = cast(secondTileVar.image(), Pixel.class).stream() //
                                        .map(row -> row.get(0)) //
                                        .collect(Collectors.toList());
                                return firstCol.equals(lastCol);
                            });
                }) //
                .findAny() //
                .orElseThrow(() -> new IllegalStateException("Unable to find orientation for first tile"));
    }

    private Tile flipFirstTile(Tile firstTile, List<List<Long>> arrangedIds, Map<Long, Tile> mappedTiles) {
        Tile secondTile = mappedTiles.get(arrangedIds.get(1).get(0));
        List<Tile> secondTileVariations = Stream.of(secondTile, secondTile.flipHor(), //
                secondTile.flipVer(), secondTile.flipHor().flipVer()) //
                .flatMap(Tile::rotations) //
                .collect(Collectors.toList());

        return Stream.of(firstTile, firstTile.flipVer()) //
                .filter(tile -> {
                    List<Pixel> lastRow = cast(tile.image(), Pixel.class).get(tile.image().size() - 1);
                    return secondTileVariations.stream() //
                            .anyMatch(secondTileVar -> {
                                List<Pixel> firstRow = cast(secondTileVar.image(), Pixel.class).get(0);
                                return firstRow.equals(lastRow);
                            });
                }) //
                .findAny() //
                .orElseThrow(() -> new IllegalStateException("Unable to find flip for first tile"));
    }

    private Map<Long, List<Long>> getPathsFromNode(long node, Map<Long, List<Tile>> adjacencyMap) {
        Map<Long, Long> parents = new HashMap<>();
        parents.put(node, null);
        Deque<Long> queue = new ArrayDeque<>(tiles.size());
        queue.add(node);
        while (!queue.isEmpty()) {
            long currentNode = queue.removeFirst();
            List<Tile> adjacentNodes = adjacencyMap.get(currentNode);
            adjacentNodes.stream() //
                    .map(Tile::id) //
                    .filter(id -> !parents.containsKey(id)) //
                    .forEach(id -> {
                        parents.put(id, currentNode);
                        queue.add(id);
                    });
        }

        Map<Long, List<Long>> paths = new HashMap<>();
        tiles.stream() //
                .map(Tile::id) //
                .filter(id -> id != node) //
                .forEach(id -> {
                    long currentNode = id;
                    Long parent;
                    List<Long> nodePath = paths.computeIfAbsent(id, __ -> new ArrayList<>());
                    nodePath.add(id);
                    while ((parent = parents.get(currentNode)) != null) {
                        nodePath.add(0, parent);
                        currentNode = parent;
                    }
                });
        return paths;
    }

    public static Tileset parseInput(BufferedReader in) throws IOException {
        List<Tile> tiles = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.isBlank()) {
                lines.add(line);
            } else {
                tiles.add(Tile.fromStrings(lines));
                lines.clear();
            }
        }
        if (!lines.isEmpty()) {
            tiles.add(Tile.fromStrings(lines));
        }
        return new Tileset(List.copyOf(tiles));
    }

}