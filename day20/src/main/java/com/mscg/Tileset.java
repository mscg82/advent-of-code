package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Tileset(List<Tile> tiles) {

    public long validate() {
        Map<Long, List<Tile>> adjacencyMap = getAdjacencyMap();
        long[] corners = adjacencyMap.entrySet().stream() //
                .filter(entry -> entry.getValue().size() == 2) //
                .mapToLong(entry -> entry.getKey()) //
                .toArray();
        long prod = 1L;
        for (long corner : corners) {
            prod *= corner;
        }
        return prod;
    }

    public Map<Long, List<Tile>> getAdjacencyMap() {
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

        // Make everything immutable
        adjacencyMap.replaceAll((id, tiles) -> List.copyOf(tiles));
        return Map.copyOf(adjacencyMap);
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