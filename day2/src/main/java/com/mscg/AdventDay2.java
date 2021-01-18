package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AdventDay2 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        long validPasswords = readInput().stream()
            .filter(r -> r.rule().test(r.password()))
            .count();
        System.out.println("Part 1: valid password: %d".formatted(validPasswords));
    }

    private static void part2() throws Exception {
        long validPasswords = readInput().stream()
                .filter(r -> r.rule().testPart2(r.password()))
                .count();
        System.out.println("Part 2: valid password: %d".formatted(validPasswords));
    }

    private static List<Record> readInput() throws Exception {
        try (var in = new BufferedReader(new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8))) {
            return in.lines()
                    .filter(s -> !s.isBlank())
                    .map(Record::fromString)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }
}

record Interval(int min, int max) implements IntPredicate {
    public static Optional<Interval> fromString(String s) {
        try {
            String[] parts = s.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            return Optional.of(new Interval(min, max));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Interval {
        if (min > max) {
            throw new IllegalArgumentException("min must be less that max");
        }
    }

    @Override
    public boolean test(int value) {
        return value >= min && value <= max;
    }

}

record Rule(Interval interval, char letter) implements Predicate<String> {

    public static Optional<Rule> fromString(String s) {
        try {
            String[] parts = s.split(" ");
            char letter;
            if (parts[1].length() != 1) {
                throw new IllegalArgumentException("Rule must contain only one letter");
            }
            letter = parts[1].charAt(0);
            Optional<Interval> interval = Interval.fromString(parts[0]);
            return interval.map(i -> new Rule(i, letter));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean test(String s) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (int i = 0, l = s.length(); i < l; i++) {
            char c = s.charAt(i);
            frequencies.merge(c, 1, Integer::sum);
        }
        int occurrences = frequencies.getOrDefault(letter, 0);
        return interval.test(occurrences);
    }

    public boolean testPart2(String s) {
        try {
            int char1 = s.charAt(interval.min() - 1);
            int char2 = s.charAt(interval.max() - 1);
            return (char1 == letter && char2 != letter) || (char1 != letter && char2 == letter);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }
}

record Record(Rule rule, String password) {
    public static Optional<Record> fromString(String s) {
        try {
            String[] parts = s.split(":");
            String passwd = parts[1].trim();
            Optional<Rule> rule = Rule.fromString(parts[0]);
            return rule.map(r -> new Record(r, passwd));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}