package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay21Test {

    @Test
    public void testParsePattern() {
        final var pattern2by2 = ArtGenerator.Pattern.fromString(".#/#.");
        Assertions.assertEquals("""
                .#
                #.""", pattern2by2.toString());
        final var pattern3by3 = ArtGenerator.Pattern.fromString("###/#../#..");
        Assertions.assertEquals("""
                ###
                #..
                #..""", pattern3by3.toString());
        final var pattern4by4 = ArtGenerator.Pattern.fromString("##.#/...#/####/#.##");
        Assertions.assertEquals("""
                ##.#
                ...#
                ####
                #.##""", pattern4by4.toString());
    }

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var generator = ArtGenerator.parseInput(in);
            final Map<ArtGenerator.Pattern, ArtGenerator.Pattern> rules = generator.rules();
            Assertions.assertEquals(2, rules.size());
            final var iterator = rules.entrySet().iterator();
            var rule = iterator.next();
            Assertions.assertEquals("""
                    ..
                    .#""", rule.getKey().toString());
            Assertions.assertEquals("""
                    ##.
                    #..
                    ...""", rule.getValue().toString());

            rule = iterator.next();
            Assertions.assertEquals("""
                    .#.
                    ..#
                    ###""", rule.getKey().toString());
            Assertions.assertEquals("""
                    #..#
                    ....
                    ....
                    #..#""", rule.getValue().toString());
        }
    }

    @Test
    public void testRotateCW() {
        final var pattern2by2 = ArtGenerator.Pattern.fromString(".#/#.");
        Assertions.assertEquals("""
                #.
                .#""", pattern2by2.rotateCW().toString());
        final var pattern3by3 = ArtGenerator.Pattern.fromString("###/#../#..");
        Assertions.assertEquals("""
                ###
                ..#
                ..#""", pattern3by3.rotateCW().toString());
        final var pattern4by4 = ArtGenerator.Pattern.fromString("##.#/...#/####/#.##");
        Assertions.assertEquals("""
                ##.#
                .#.#
                ##..
                ####""", pattern4by4.rotateCW().toString());
    }

    @Test
    public void testFlipHor() {
        final var pattern2by2 = ArtGenerator.Pattern.fromString(".#/#.");
        Assertions.assertEquals("""
                #.
                .#""", pattern2by2.flipHor().toString());
        final var pattern3by3 = ArtGenerator.Pattern.fromString("###/#../#..");
        Assertions.assertEquals("""
                #..
                #..
                ###""", pattern3by3.flipHor().toString());
        final var pattern4by4 = ArtGenerator.Pattern.fromString("##.#/...#/####/#.##");
        Assertions.assertEquals("""
                #.##
                ####
                ...#
                ##.#""", pattern4by4.flipHor().toString());
    }

    @Test
    public void testFlipVer() {
        final var pattern2by2 = ArtGenerator.Pattern.fromString(".#/#.");
        Assertions.assertEquals("""
                #.
                .#""", pattern2by2.flipVer().toString());
        final var pattern3by3 = ArtGenerator.Pattern.fromString("###/#../#..");
        Assertions.assertEquals("""
                ###
                ..#
                ..#""", pattern3by3.flipVer().toString());
        final var pattern4by4 = ArtGenerator.Pattern.fromString("##.#/...#/####/#.##");
        Assertions.assertEquals("""
                #.##
                #...
                ####
                ##.#""", pattern4by4.flipVer().toString());
    }

    @Test
    public void testFlipBoth() {
        final var pattern2by2 = ArtGenerator.Pattern.fromString(".#/#.");
        Assertions.assertEquals("""
                .#
                #.""", pattern2by2.flipBoth().toString());
        final var pattern3by3 = ArtGenerator.Pattern.fromString("###/#../#..");
        Assertions.assertEquals("""
                ..#
                ..#
                ###""", pattern3by3.flipBoth().toString());
        final var pattern4by4 = ArtGenerator.Pattern.fromString("##.#/...#/####/#.##");
        Assertions.assertEquals("""
                ##.#
                ####
                #...
                #.##""", pattern4by4.flipBoth().toString());
    }

    @Test
    public void testGeneration1Step() throws Exception {
        try (BufferedReader in = readInput()) {
            final var generator = ArtGenerator.parseInput(in);
            final ArtGenerator.Pattern image = generator.generateImage(1);
            Assertions.assertEquals("""
                    #..#
                    ....
                    ....
                    #..#""", image.toString());
        }
    }

    @Test
    public void testGeneration2Step() throws Exception {
        try (BufferedReader in = readInput()) {
            final var generator = ArtGenerator.parseInput(in);
            final ArtGenerator.Pattern image = generator.generateImage(ArtGenerator.Pattern.fromString("#..#/..../..../#..#"), 1);
            Assertions.assertEquals("""
                    ##.##.
                    #..#..
                    ......
                    ##.##.
                    #..#..
                    ......""", image.toString());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
