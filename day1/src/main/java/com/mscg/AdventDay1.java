package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.mscg.CityMap.Intersection;

public class AdventDay1 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var cityMap = CityMap.parseInput(in);
            var finalPosition = cityMap.run();
            System.out.println("Part 1 - Answer %d".formatted(finalPosition.intersection().distance()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var cityMap = CityMap.parseInput(in);
            Intersection hq = cityMap.findHQ().orElseThrow();
            System.out.println("Part 2 - Answer %d".formatted(hq.distance()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay1.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
