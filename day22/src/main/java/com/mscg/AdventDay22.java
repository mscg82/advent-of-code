package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay22 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var game = CombatGame.parseInput(readInput());
            System.out.println("Part 1: Answer: %d".formatted(game.getGameValue()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            // var tileset = Tileset.parseInput(in);
            // var arrangedTilesIds = Utils.rotate(Utils.rotate(tileset.arrangeTileIds()));

            // var arrangedTiles = tileset.arrangeTiles(arrangedTilesIds);

            // Tile rebuiltImage = Utils.rebuildImage(arrangedTiles);

            // var mask = Mask.parseStrings(List.of(//
            //         "                  # ", //
            //         "#    ##    ##    ###", //
            //         " #  #  #  #  #  #   "));

            // long blackCount = rebuiltImage.countBlackPixels();

            // Tile monsterImage = Stream.of(rebuiltImage, rebuiltImage.flipHor(), //
            //         rebuiltImage.flipVer(), rebuiltImage.flipHor().flipVer()) //
            //         .flatMap(Tile::rotations) //
            //         .map(t -> new Tile(t.id(), mask.apply(Utils.cast(t.image(), Pixel.class)))) //
            //         .filter(t -> t.countBlackPixels() != blackCount) //
            //         .findAny() //
            //         .orElseThrow(() -> new IllegalArgumentException("Can't find monsters in image"));

            // System.out.println("Part 2: Answer: %d".formatted(monsterImage.countBlackPixels()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay22.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
