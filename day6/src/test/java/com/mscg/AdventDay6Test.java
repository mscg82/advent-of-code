package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay6Test {

    @Test
    public void testAnswers() throws Exception {
        try (BufferedReader in = readInput()) {
            List<AnswersGroup> answersGroups = AnswersGroup.parseInput(in);
            Assertions.assertEquals(5, answersGroups.size());

            int[] answersPerGroup = answersGroups.stream()
                    .mapToInt(g -> g.answers().size())
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 1, 3, 2, 4, 1 }, answersPerGroup);

            int[] distinctAnswersPerGroup = answersGroups.stream()
                    .map(AnswersGroup::getDistinctAnswers)
                    .mapToInt(arr -> arr.length)
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 3, 3, 3, 1, 1 }, distinctAnswersPerGroup);

            int[] commonAnswers = answersGroups.stream()
                    .map(AnswersGroup::getCommonAnswers)
                    .mapToInt(arr -> arr.length)
                    .toArray();
            Assertions.assertArrayEquals(new int[] { 3, 0, 1, 1, 1 }, commonAnswers);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
