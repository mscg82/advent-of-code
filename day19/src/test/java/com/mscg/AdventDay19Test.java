package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay19Test {

    @Test
    public void testRuleTransformation() {
        var rule1 = new Rule.BaseRule('a');
        var rule3 = new Rule.BaseRule('b');
        var rule2 = new Rule.OrRule(new Rule.AndRule(rule1, rule3), new Rule.AndRule(rule3, rule1));
        var rule0 = new Rule.AndRule(rule1, rule2);

        Assertions.assertEquals("(a((ab)|(ba)))", rule0.asRegExp());
        var pattern = Pattern.compile("^" + rule0.asRegExp() + "$");
        Assertions.assertTrue(pattern.matcher("aab").matches());
        Assertions.assertTrue(pattern.matcher("aab").matches());
        Assertions.assertTrue(pattern.matcher("aba").matches());
        Assertions.assertFalse(pattern.matcher("abaa").matches());
        Assertions.assertFalse(pattern.matcher("abab").matches());
    }

    @Test
    public void testParse() throws Exception {
        var ruleset = Ruleset.parseInput(readInput());

        Assertions.assertEquals(6, ruleset.getRules().size());
        Assertions.assertEquals(5, ruleset.getMessages().size());

        var rule4 = new Rule.BaseRule('a');
        var rule5 = new Rule.BaseRule('b');
        var rule2 = new Rule.OrRule(new Rule.AndRule(rule4, rule4), new Rule.AndRule(rule5, rule5));
        var rule3 = new Rule.OrRule(new Rule.AndRule(rule4, rule5), new Rule.AndRule(rule5, rule4));
        var rule1 = new Rule.OrRule(new Rule.AndRule(rule2, rule3), new Rule.AndRule(rule3, rule2));
        var rule0 = new Rule.AndRule(rule4, rule1, rule5);
        Assertions.assertEquals(rule0, ruleset.getRules().get(0));
        Assertions.assertEquals(rule1, ruleset.getRules().get(1));
        Assertions.assertEquals(rule2, ruleset.getRules().get(2));
        Assertions.assertEquals(rule3, ruleset.getRules().get(3));
        Assertions.assertEquals(rule4, ruleset.getRules().get(4));
        Assertions.assertEquals(rule5, ruleset.getRules().get(5));
    }

    @Test
    public void testValidMessages() throws Exception {
        var ruleset = Ruleset.parseInput(readInput());
        var validMessages = ruleset.getValidMessage(0);

        Assertions.assertArrayEquals(new String[] { "ababbb", "abbbab" }, validMessages.toArray(new String[0]));
    }

    @Test
    public void testValidMessages2() throws Exception {
        var ruleset = Ruleset.parseInput(readInput2());
        var validMessages = ruleset.getValidMessage(0);

        Assertions.assertArrayEquals(new String[] { "bbabbbbaabaabba", "ababaaaaaabaaab", "ababaaaaabbbaba" },
                validMessages.toArray(new String[0]));
    }

    @Test
    public void testValidMessages2WithPatch() throws Exception {
        var ruleset = Ruleset.parseInput(readInput2());

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
        var validMessages = ruleset.getValidMessage(0);

        Assertions.assertArrayEquals(
                new String[] { "bbabbbbaabaabba", "babbbbaabbbbbabbbbbbaabaaabaaa",
                        "aaabbbbbbaaaabaababaabababbabaaabbababababaaa", "bbbbbbbaaaabbbbaaabbabaaa",
                        "bbbababbbbaaaaaaaabbababaaababaabab", "ababaaaaaabaaab", "ababaaaaabbbaba",
                        "baabbaaaabbaaaababbaababb", "abbbbabbbbaaaababbbbbbaaaababb", "aaaaabbaabaaaaababaa",
                        "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa", "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba" },
                validMessages.toArray(new String[0]));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

    private BufferedReader readInput2() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input2.txt"), StandardCharsets.UTF_8));
    }

}
