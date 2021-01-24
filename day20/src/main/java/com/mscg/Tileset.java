package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Tileset(List<Tile> tiles) {

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

    public List<List<Long>> arrangeTiles() {
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
         List<Long> row = arrangedTiles.get(0);
         for (int i = 0; i < sideLength; i++) {
             row.set(i, pathToCorners.get(0).get(i));
         }
         for (int i = 1; i < sideLength; i++) {
             arrangedTiles.get(i).set(0, pathToCorners.get(1).get(i));
         }

        return List.copyOf(arrangedTiles.stream() //
                .map(List::copyOf) //
                .collect(Collectors.toList()));
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