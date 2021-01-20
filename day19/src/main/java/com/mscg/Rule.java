package com.mscg;

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

        @Override
        public String asRegExp() {
            return rules.stream() //
                    .map(Rule::asRegExp) //
                    .collect(Collectors.joining("", "(", ")"));
        }
    }

    public record OrRule(List<Rule> rules) implements Rule {

        @Override
        public String asRegExp() {
            return rules.stream() //
                    .map(Rule::asRegExp) //
                    .collect(Collectors.joining("|", "(", ")"));
        }
    }

}