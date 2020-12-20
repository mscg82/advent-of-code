package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdventDay7 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            List<BagRule> rules = BagRule.parseInput(in);
            Set<String> containingColors = BagRule.findContainingColors("shiny gold", rules);
            System.out.println("Part 1: Bags: %d".formatted(containingColors.size()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            List<BagRule> rules = BagRule.parseInput(in);
            Map<String, Integer> containedBags = BagRule.findContainedBags("shiny gold", rules);
            System.out.println("Part 2: Bags: %d".formatted(containedBags.values().stream().mapToInt(Integer::intValue).sum()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay7.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
