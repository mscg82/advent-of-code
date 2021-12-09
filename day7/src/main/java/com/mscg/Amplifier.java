package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record Amplifier(IntcodeV2 computer) {

    public static Amplifier parseInput(final BufferedReader in) throws IOException {
        return new Amplifier(IntcodeV2.parseInput(in));
    }

    private static List<int[]> generatePermutations(final int[] elements) {
        final List<int[]> permutations = new ArrayList<>();
        generetePermutationsRecursive(elements.length, elements, permutations);
        return permutations;
    }

    private static void generetePermutationsRecursive(final int n, final int[] elements, final List<int[]> permutations) {
        if (n == 1) {
            permutations.add(elements.clone());
        } else {
            for (int i = 0; i < n - 1; i++) {
                generetePermutationsRecursive(n - 1, elements, permutations);
                if (n % 2 == 0) {
                    swap(elements, i, n - 1);
                } else {
                    swap(elements, 0, n - 1);
                }
            }
            generetePermutationsRecursive(n - 1, elements, permutations);
        }
    }

    private static void swap(final int[] input, final int a, final int b) {
        final int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    public int computeMaxOutput() {
        final List<int[]> permutations = generatePermutations(new int[]{ 0, 1, 2, 3, 4 });
        return permutations.stream() //
                .mapToInt(input -> {
                    final var resultA = computer.execute(null, null, List.of(input[0], 0).iterator());
                    final var resultB = computer.execute(null, null, List.of(input[1], resultA.outputs()[0]).iterator());
                    final var resultC = computer.execute(null, null, List.of(input[2], resultB.outputs()[0]).iterator());
                    final var resultD = computer.execute(null, null, List.of(input[3], resultC.outputs()[0]).iterator());
                    final var resultE = computer.execute(null, null, List.of(input[4], resultD.outputs()[0]).iterator());

                    return resultE.outputs()[0];
                }) //
                .max() //
                .orElseThrow();
    }

}
