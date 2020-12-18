package com.mscg;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record AnswersGroup(List<String> answers) {

    public int[] getDistinctAnswers() {
        return answers.stream()
                .flatMapToInt(String::codePoints)
                .distinct()
                .toArray();
    }

    public int[] getCommonAnswers() {
        List<Set<Integer>> distinctAnswers = new ArrayList<>();
        for (String answer : answers) {
            distinctAnswers.add(answer.codePoints()
                    .boxed()
                    .collect(Collectors.toSet()));
        }
        Set<Integer> intersection = new HashSet<>(distinctAnswers.get(0));
        distinctAnswers.forEach(set -> intersection.retainAll(set));
        return intersection.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static List<AnswersGroup> parseInput(BufferedReader in) throws Exception {
        final var answersGroups = new ArrayList<AnswersGroup>();
        String line;
        var answers = new ArrayList<String>();
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                answersGroups.add(new AnswersGroup(answers));
                answers = new ArrayList<>();
            }
            else {
                answers.add(line);
            }
        }
        if (!answers.isEmpty()) {
            answersGroups.add(new AnswersGroup(answers));
        }
        return answersGroups;
    }

}
