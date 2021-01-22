package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdventDay19 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var ruleset = Ruleset.parseInput(in);
            System.out.println("Part 1: Answer: %d".formatted(ruleset.getValidMessage(0).size()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var ruleset = Ruleset.parseInput(in);

            Rule rule42 = ruleset.getRules().get(42);
            Rule rule31 = ruleset.getRules().get(31);
            Rule newRule8 = new Rule.ExplicitRule("(" + rule42.asRegExp() + ")+");
            Rule newRule11a = new Rule.AndRule(rule42, rule31);
            Rule newRule11b = new Rule.AndRule(rule42, rule42, rule31, rule31);
            Rule newRule11c = new Rule.AndRule(rule42, rule42, rule42, rule31, rule31, rule31);
            Rule newRule11d = new Rule.AndRule(rule42, rule42, rule42, rule42, rule31, rule31, rule31, rule31);
            Rule newRule11 = new Rule.OrRule(newRule11a, newRule11b, newRule11c, newRule11d);
            Rule newRule0 = new Rule.AndRule(newRule8, newRule11);
            ruleset = ruleset.patchRules(Map.of( //
                    0, newRule0, //
                    8, newRule8, //
                    11, newRule11));
            System.out.println("Part 2: Answer: %d".formatted(ruleset.getValidMessage(0).size()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay19.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
