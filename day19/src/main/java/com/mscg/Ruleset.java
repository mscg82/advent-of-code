package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Ruleset {

    @Getter
    private final Map<Integer, Rule> rules;

    @Getter
    private final List<String> messages;

    public Ruleset patchRules(@NonNull final Map<Integer, Rule> rules) {
        var newRules = new HashMap<Integer, Rule>(this.rules);
        newRules.putAll(rules);
        return new Ruleset(Map.copyOf(newRules), List.copyOf(messages));
    }

    public List<String> getValidMessage(int ruleId) {
        var rule = rules.get(ruleId);
        var pattern = Pattern.compile("^" + rule.asRegExp() + "$");
        return List.copyOf(messages.stream() //
                .filter(message -> pattern.matcher(message).matches()) //
                .collect(Collectors.toList()));
    }

    public static Ruleset parseInput(BufferedReader in) throws IOException {
        var rulesStr = new HashMap<Integer, String>();

        var rules = new HashMap<Integer, Rule>();
        var messages = new ArrayList<String>();

        var status = ParseStatus.RULES;
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                status = ParseStatus.MESSAGES;
                continue;
            }

            switch (status) {
                case RULES -> {
                    var parts = line.split(":");
                    rulesStr.put(Integer.parseInt(parts[0].trim()), parts[1].trim());
                }

                case MESSAGES -> messages.add(line);
            }
        }

        // first iteration: build all simple rules
        rulesStr.forEach((id, ruleStr) -> {
            if (ruleStr.startsWith("\"")) {
                rules.put(id, new Rule.BaseRule(ruleStr.charAt(1)));
            }
        });

        // second iteration: build other rules
        rulesStr.forEach((id, ruleStr) -> {
            if (!rules.containsKey(id)) {
                Rule rule = buildRule(ruleStr, rulesStr, rules);
                rules.put(id, rule);
            }
        });

        return new Ruleset(Map.copyOf(rules), List.copyOf(messages));
    }

    private static Rule buildRule(final String ruleStr, final Map<Integer, String> rulesStr,
            final Map<Integer, Rule> rules) {
        String[] parts = ruleStr.split(" \\| ");
        if (parts.length == 1) {
            return buildAndRule(ruleStr, rulesStr, rules);
        } else {
            List<Rule> subRules = Arrays.stream(parts) //
                    .map(part -> buildAndRule(part, rulesStr, rules)) //
                    .collect(Collectors.toList());
            return new Rule.OrRule(List.copyOf(subRules));
        }
    }

    private static Rule buildAndRule(final String ruleStr, final Map<Integer, String> rulesStr,
            final Map<Integer, Rule> rules) {

        List<Integer> subIds = Arrays.stream(ruleStr.split(" ")) //
                .map(Integer::parseInt) //
                .collect(Collectors.toList());

        var subRules = new ArrayList<Rule>(subIds.size());
        for (var subId : subIds) {
            var subRule = rules.get(subId);
            if (subRule == null) {
                subRule = buildRule(rulesStr.get(subId), rulesStr, rules);
                rules.put(subId, subRule);
            }
            subRules.add(subRule);
        }

        return new Rule.AndRule(List.copyOf(subRules));
    }

    private static enum ParseStatus {
        RULES, MESSAGES
    }
}