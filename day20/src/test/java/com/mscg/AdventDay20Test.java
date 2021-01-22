package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay20Test {

    @SuppressWarnings("unchecked")
    private static <T> List<T> cast(List<?> source, Class<T> clazz) {
        return (List<T>) source;
    }

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);

            Assertions.assertEquals(9, tileset.tiles().size());
            Assertions.assertEquals("""
                    Tile 2311:
                    ..##.#..#.
                    ##..#.....
                    #...##..#.
                    ####.#...#
                    ##.##.###.
                    ##...#.###
                    .#.#.#..##
                    ..#....#..
                    ###...#.#.
                    ..###..###""", tileset.tiles().get(0).toString());
            Assertions.assertEquals("""
                    Tile 3079:
                    #.#.#####.
                    .#..######
                    ..#.......
                    ######....
                    ####.#..#.
                    .#...#.##.
                    #.#####.##
                    ..#.###...
                    ..#.......
                    ..#.###...""", tileset.tiles().get(8).toString());
        }
    }

    @Test
    public void testFlipHor() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            Tile tile = cast(tileset.tiles(), Tile.class).get(0);
            Tile flipped = tile.flipHor();
            Assertions.assertEquals("""
                    Tile 2311:
                    .#..#.##..
                    .....#..##
                    .#..##...#
                    #...#.####
                    .###.##.##
                    ###.#...##
                    ##..#.#.#.
                    ..#....#..
                    .#.#...###
                    ###..###..""", flipped.toString());
        }
    }

    @Test
    public void testFlipVer() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            Tile tile = cast(tileset.tiles(), Tile.class).get(0);
            Tile flipped = tile.flipVer();
            Assertions.assertEquals("""
                    Tile 2311:
                    ..###..###
                    ###...#.#.
                    ..#....#..
                    .#.#.#..##
                    ##...#.###
                    ##.##.###.
                    ####.#...#
                    #...##..#.
                    ##..#.....
                    ..##.#..#.""", flipped.toString());
        }
    }

    @Test
    public void testFlipBoth() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            Tile tile = cast(tileset.tiles(), Tile.class).get(0);
            Tile flipped = tile.flipVer().flipHor();
            Assertions.assertEquals("""
                    Tile 2311:
                    ###..###..
                    .#.#...###
                    ..#....#..
                    ##..#.#.#.
                    ###.#...##
                    .###.##.##
                    #...#.####
                    .#..##...#
                    .....#..##
                    .#..#.##..""", flipped.toString());
        }
    }

    @Test
    public void testAdjacencyMap() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            Map<Long, List<Tile>> adjacencyMap = tileset.getAdjacencyMap();
            Map<Long, List<Long>> simpleAdjacencyMap = adjacencyMap.entrySet().stream() //
                    .map(entry -> Map.entry(entry.getKey(),
                            entry.getValue().stream().map(Tile::id).sorted().collect(Collectors.toList()))) //
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Assertions.assertEquals(Map.of( //
                    1951L, List.of(2311L, 2729L), //
                    2311L, List.of(1427L, 1951L, 3079L), //
                    3079L, List.of(2311L, 2473L), //
                    2729L, List.of(1427L, 1951L, 2971L), //
                    1427L, List.of(1489L, 2311L, 2473L, 2729L), //
                    2473L, List.of(1171L, 1427L, 3079L), //
                    2971L, List.of(1489L, 2729L), //
                    1489L, List.of(1171L, 1427L, 2971L), //
                    1171L, List.of(1489L, 2473L)), //
                    simpleAdjacencyMap);
        }
    }

    @Test
    public void testValidate() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);

            Assertions.assertEquals(20899048083289L, tileset.validate());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
