package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdventDay7Test {

    @Test
    public void testParser() throws Exception {
        try (BufferedReader in = readInput()) {
            List<BagRule> rules = BagRule.parseInput(in);
            Assertions.assertEquals(9, rules.size());
            String[] colors = rules.stream()
                    .map(BagRule::color)
                    .toArray(String[]::new);
            Assertions.assertArrayEquals(new String[] { "light red", "dark orange", "bright white", "muted yellow", "shiny gold",
                    "dark olive", "vibrant plum", "faded blue", "dotted black" }, colors);
            {
                var rule = rules.get(0);
                Assertions.assertEquals(Map.of("bright white", 1, "muted yellow", 2), rule.allowedContainedBags());
            }
            {
                var rule = rules.get(5);
                Assertions.assertEquals(Map.of("faded blue", 3, "dotted black", 4), rule.allowedContainedBags());
            }
            {
                var rule = rules.get(7);
                Assertions.assertTrue(rule.allowedContainedBags().isEmpty(), "Rule must have an empty list of contained bags");
            }
        }
    }

    @Test
    public void testFinding() throws Exception {
        try (BufferedReader in = readInput()) {
            List<BagRule> rules = BagRule.parseInput(in);
            Set<String> containingColors = BagRule.findContainingColors("shiny gold", rules);
            Assertions.assertEquals(Set.of("bright white", "muted yellow", "dark orange", "light red"), containingColors);
        }
    }

    @Test
    public void testContained() throws Exception {
        try (BufferedReader in = readInput()) {
            List<BagRule> rules = BagRule.parseInput(in);
            Map<String, Integer> containedBags = BagRule.findContainedBags("shiny gold", rules);
            Assertions.assertEquals(32, containedBags.values().stream().mapToInt(Integer::intValue).sum());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
