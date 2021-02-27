package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import com.mscg.LightGrid.Light;

public class AdventDay18 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var lightGrid = LightGrid.parseInput(in, false);
            LightGrid step100 = Stream.iterate(lightGrid, LightGrid::next) //
                    .skip(100) //
                    .limit(1) //
                    .findFirst() //
                    .orElseThrow();
            long lightsOn = step100.getLights().stream() //
                    .flatMap(List::stream) //
                    .filter(l -> l == Light.ON) //
                    .count();

            System.out.println("Part 1 - Answer %d".formatted(lightsOn));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var lightGrid = LightGrid.parseInput(in, true);
            LightGrid step100 = Stream.iterate(lightGrid, LightGrid::next) //
                    .skip(100) //
                    .limit(1) //
                    .findFirst() //
                    .orElseThrow();
            long lightsOn = step100.getLights().stream() //
                    .flatMap(List::stream) //
                    .filter(l -> l == Light.ON) //
                    .count();

            System.out.println("Part 1 - Answer %d".formatted(lightsOn));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay18.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
