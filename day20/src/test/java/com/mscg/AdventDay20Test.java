package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mscg.Tile.Pixel;

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

    @Test
    public void testArrangedTilesIds() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            var arrangedTilesIds = tileset.arrangeTileIds();

            Assertions.assertEquals(List.of( //
                    List.of(1171L, 1489L, 2971L), //
                    List.of(2473L, 1427L, 2729L), //
                    List.of(3079L, 2311L, 1951L)), //
                    arrangedTilesIds);
        }
    }

    @Test
    public void testArrangedTiles() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            var arrangedTilesIds = Utils.rotate(Utils.rotate(tileset.arrangeTileIds()));

            var arrangedTiles = tileset.arrangeTiles(arrangedTilesIds);

            Assertions.assertEquals("""
                    Tile 1951:
                    #...##.#..
                    ..#.#..#.#
                    .###....#.
                    ###.##.##.
                    .###.#####
                    .##.#....#
                    #...######
                    .....#..##
                    #.####...#
                    #.##...##.""", arrangedTiles.get(0).get(0).toString());
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
                    ..#.###...""", arrangedTiles.get(0).get(2).toString());
            Assertions.assertEquals("""
                    Tile 2971:
                    ...#.#.#.#
                    ..#.#.###.
                    ..####.###
                    #..#.#..#.
                    .#..####.#
                    .#####..##
                    ##.##..#..
                    #.#.###...
                    #...###...
                    ..#.#....#""", arrangedTiles.get(2).get(0).toString());
            Assertions.assertEquals("""
                    Tile 1171:
                    .##...####
                    #..#.##..#
                    .#.#..#.##
                    .####.###.
                    ####.###..
                    .##....##.
                    .####...#.
                    .####.##.#
                    ...#..####
                    ...##.....""", arrangedTiles.get(2).get(2).toString());
        }
    }

    @Test
    public void testRebuiltImage() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            var arrangedTilesIds = Utils.rotate(Utils.rotate(tileset.arrangeTileIds()));

            var arrangedTiles = tileset.arrangeTiles(arrangedTilesIds);

            Tile rebuiltImage = Utils.rebuildImage(arrangedTiles);

            Assertions.assertEquals("""
                    Tile 0:
                    .#.#..#.##...#.##..#####
                    ###....#.#....#..#......
                    ##.##.###.#.#..######...
                    ###.#####...#.#####.#..#
                    ##.#....#.##.####...#.##
                    ...########.#....#####.#
                    ....#..#...##..#.#.###..
                    .####...#..#.....#......
                    #..#.##..#..###.#.##....
                    #.####..#.####.#.#.###..
                    ###.#.#...#.######.#..##
                    #.####....##..########.#
                    ##..##.#...#...#.#.#.#..
                    ...#..#..#.#.##..###.###
                    .#.#....#.##.#...###.##.
                    ###.#...#..#.##.######..
                    .#.#.###.##.##.#..#.##..
                    .####.###.#...###.#..#.#
                    ..#.#..#..#.#.#.####.###
                    #..####...#.#.#.###.###.
                    #####..#####...###....##
                    #.##..#..#...#..####...#
                    .#.###..##..##..####.##.
                    ...###...##...#...#..###""", rebuiltImage.toString());
        }
    }

    @Test
    public void testRotate() {
        List<List<Long>> matrix = List.of( //
                List.of(1171L, 1489L, 2971L), //
                List.of(2473L, 1427L, 2729L), //
                List.of(3079L, 2311L, 1951L));

        List<List<Long>> rotated1 = Utils.rotate(matrix);
        Assertions.assertEquals(List.of( //
                List.of(3079L, 2473L, 1171L), //
                List.of(2311L, 1427L, 1489L), //
                List.of(1951L, 2729L, 2971L)), //
                rotated1);

        List<List<Long>> rotated2 = Utils.rotate(rotated1);
        Assertions.assertEquals(List.of( //
                List.of(1951L, 2311L, 3079L), //
                List.of(2729L, 1427L, 2473L), //
                List.of(2971L, 1489L, 1171L)), //
                rotated2);
    }

    @Test
    public void testRotateTile() {
        Tile tile = new Tile(1L, List.of( //
                List.of(Pixel.BLACK, Pixel.WHITE, Pixel.BLACK), //
                List.of(Pixel.WHITE, Pixel.WHITE, Pixel.WHITE), //
                List.of(Pixel.BLACK, Pixel.WHITE, Pixel.WHITE)));
        List<Tile> rotatedTiles = tile.rotations().collect(Collectors.toList());

        Assertions.assertEquals("""
                Tile 1:
                #.#
                ...
                #..""", rotatedTiles.get(0).toString());
        Assertions.assertEquals("""
                Tile 1:
                #.#
                ...
                ..#""", rotatedTiles.get(1).toString());
        Assertions.assertEquals("""
                Tile 1:
                ..#
                ...
                #.#""", rotatedTiles.get(2).toString());
        Assertions.assertEquals("""
                Tile 1:
                #..
                ...
                #.#""", rotatedTiles.get(3).toString());
    }

    @Test
    public void testParseMask() {
        var mask = Mask.parseStrings(List.of(//
                "                  # ", //
                "#    ##    ##    ###", //
                " #  #  #  #  #  #   "));

        Assertions.assertEquals("                  # \n"//
                + "#    ##    ##    ###\n"//
                + " #  #  #  #  #  #   ", mask.toString());
    }

    @Test
    public void testApplyMask() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            var arrangedTilesIds = Utils.rotate(Utils.rotate(tileset.arrangeTileIds()));

            var arrangedTiles = tileset.arrangeTiles(arrangedTilesIds);

            Tile rebuiltImage = Utils.rebuildImage(arrangedTiles);

            var mask = Mask.parseStrings(List.of(//
                    "                  # ", //
                    "#    ##    ##    ###", //
                    " #  #  #  #  #  #   "));

            long blackCount = rebuiltImage.countBlackPixels();

            Tile monsterImage = Stream.of(rebuiltImage, rebuiltImage.flipHor(), //
                    rebuiltImage.flipVer(), rebuiltImage.flipHor().flipVer()) //
                    .flatMap(Tile::rotations) //
                    .map(t -> new Tile(t.id(), mask.apply(Utils.cast(t.image(), Pixel.class)))) //
                    .filter(t -> t.countBlackPixels() != blackCount) //
                    .findAny() //
                    .orElseThrow(() -> new IllegalArgumentException("Can't find monsters in image"));

            Assertions.assertEquals(273, monsterImage.countBlackPixels());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
