package com.mscg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Rule {

    String asRegExp();

    public record BaseRule(char value) implements Rule {

        @Override
        public String asRegExp() {
            return String.valueOf(value);
        }

    }

    public record AndRule(List<Rule> rules) implements Rule {

        public AndRule(Rule first, Rule... rest) {
            this(asList(first, rest));
        }
        
        @Override
        public String asRegExp() {
            return rules.stream() //
            .map(Rule::asRegExp) //
            .collect(Collectors.joining("", "(", ")"));
        }
    }
    
    public record OrRule(List<Rule> rules) implements Rule {
        
        public OrRule(Rule first, Rule... rest) {
            this(asList(first, rest));
        }

        @Override
        public String asRegExp() {
            return rules.stream() //
                    .map(Rule::asRegExp) //
                    .collect(Collectors.joining("|", "(", ")"));
        }
    }

    private static List<Rule> asList(Rule first, Rule... rest) {
        var rules = new ArrayList<Rule>(rest.length + 1);
        rules.add(first);
        rules.addAll(Arrays.asList(rest));
        return rules;
    }

}