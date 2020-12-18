package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay6 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            List<AnswersGroup> answersGroups = AnswersGroup.parseInput(in);
            int sum = answersGroups.stream()
                    .map(AnswersGroup::getDistinctAnswers)
                    .mapToInt(arr -> arr.length)
                    .sum();
            System.out.println("Part 1: Sum: %d".formatted(sum));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            List<AnswersGroup> answersGroups = AnswersGroup.parseInput(in);
            int sum = answersGroups.stream()
                    .map(AnswersGroup::getCommonAnswers)
                    .mapToInt(arr -> arr.length)
                    .sum();
            System.out.println("Part 2: Sum: %d".formatted(sum));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay6.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
