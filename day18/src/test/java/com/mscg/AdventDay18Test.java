package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay18Test {

    @Test
    public void testParse() throws Exception {
        var ligthGrid = LightGrid.parseInput(readInput(), false);

        Assertions.assertEquals("""
                .#.#.#
                ...##.
                #....#
                ..#...
                #.#..#
                ####..""", ligthGrid.toString());
    }

    @Test
    public void testNext() throws Exception {
        var ligthGrid = LightGrid.parseInput(readInput(), false);
        Assertions.assertEquals("""
                .#.#.#
                ...##.
                #....#
                ..#...
                #.#..#
                ####..""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ..##..
                ..##.#
                ...##.
                ......
                #.....
                #.##..""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ..###.
                ......
                ..###.
                ......
                .#....
                .#....""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ...#..
                ......
                ...#..
                ..##..
                ......
                ......""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ......
                ......
                ..##..
                ..##..
                ......
                ......""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ......
                ......
                ..##..
                ..##..
                ......
                ......""", ligthGrid.toString());
    }

    @Test
    public void testNextStuck() throws Exception {
        var ligthGrid = LightGrid.parseInput(readInput(), true);
        Assertions.assertEquals("""
                ##.#.#
                ...##.
                #....#
                ..#...
                #.#..#
                ####.#""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                #.##.#
                ####.#
                ...##.
                ......
                #...#.
                #.####""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                #..#.#
                #....#
                .#.##.
                ...##.
                .#..##
                ##.###""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                #...##
                ####.#
                ..##.#
                ......
                ##....
                ####.#""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                #.####
                #....#
                ...#..
                .##...
                #.....
                #.#..#""", ligthGrid.toString());

        ligthGrid = ligthGrid.next();
        Assertions.assertEquals("""
                ##.###
                .##..#
                .##...
                .##...
                #.#...
                ##...#""", ligthGrid.toString());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
