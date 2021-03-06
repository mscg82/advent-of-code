package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public class AdventDay20 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var tileset = Tileset.parseInput(in);
            System.out.println("Part 1: Answer: %d".formatted(tileset.validate()));
        }
    }

    private static void part2() throws Exception {
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
                    .map(t -> new Tile(t.id(), mask.apply(t.image()))) //
                    .filter(t -> t.countBlackPixels() != blackCount) //
                    .findAny() //
                    .orElseThrow(() -> new IllegalArgumentException("Can't find monsters in image"));

            System.out.println("Part 2: Answer: %d".formatted(monsterImage.countBlackPixels()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay20.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
