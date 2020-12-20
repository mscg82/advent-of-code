package com.mscg;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record BagRule(String color, Map<String, Integer> allowedContainedBags) {

    private static final Pattern CONTAINED_PATTERN = Pattern.compile("(\\d+) (.+?) bag,?");

    public static Set<String> findContainingColors(String color, List<BagRule> rules) {
        var colors = new HashSet<String>();

        Deque<String> queue = new LinkedList<>();
        queue.add(color);

        while (!queue.isEmpty()) {
            String currentColor = queue.poll();
            rules.stream()
                    .filter(rule -> rule.allowedContainedBags.containsKey(currentColor))
                    .map(BagRule::color)
                    .filter(colors::add) // get only the colors not yet known
                    .forEach(queue::add); // add the new colors in the queue
        }

        return colors;
    }

    public static Map<String, Integer> findContainedBags(String color, List<BagRule> rules) {
        var counts = new HashMap<String, Integer>();

        record QueueEl(int multiplier, String color) {}

        Deque<QueueEl> queue = new LinkedList<>();
        queue.add(new QueueEl(1, color));

        while (!queue.isEmpty()) {
            QueueEl currentColor = queue.poll();

            rules.stream()
                    .filter(rule -> rule.color().equals(currentColor.color()))
                    .findAny()
                    .ifPresent(rule -> {
                        rule.allowedContainedBags().forEach((containedColor, allowedCount) -> {
                            counts.merge(containedColor, allowedCount * currentColor.multiplier(), Integer::sum);

                            queue.add(new QueueEl(allowedCount * currentColor.multiplier(), containedColor));
                        });
                    });
        }

        return counts;
    }

    public static List<BagRule> parseInput(BufferedReader in) throws Exception {
        var rules = new ArrayList<BagRule>();
        int lineNum = 0;
        String line;
        while ((line = in.readLine()) != null) {
            lineNum++;

            PartiallyParsedRule partialRule;
            {
                String[] parts = line.split(" bags contain ");
                if (parts.length != 2 || !parts[1].endsWith(".")) {
                    throw new IllegalArgumentException("Invalid rule line on line " + lineNum);
                }
                partialRule = new PartiallyParsedRule(parts[0], parts[1].substring(0, parts[1].length() - 1));

                var rule = new BagRule(partialRule.color(), new HashMap<>());
                rules.add(rule);
                if (!"no other bags".equals(partialRule.rulesStr())) {
                    Matcher matcher = CONTAINED_PATTERN.matcher(partialRule.rulesStr());
                    if (!matcher.find()) {
                        throw new IllegalArgumentException("Invalid rule line on line " + lineNum);
                    }
                    matcher.reset();

                    while (matcher.find()) {
                        if (matcher.groupCount() != 2) {
                            throw new IllegalArgumentException("Invalid rule line on line " + lineNum);
                        }
                        int number;
                        try {
                            number = Integer.parseInt(matcher.group(1));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid rule line on line " + lineNum, e);
                        }
                        String color = matcher.group(2);
                        rule.allowedContainedBags.put(color, number);
                    }
                }
            }


        }
        return rules;
    }

}

record PartiallyParsedRule(String color, String rulesStr) {}
