package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.util.CombinatoricsUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GiftsBag {

    private final int[] weights;

    public int getBlockWeight(int blocks) {
        return Arrays.stream(weights).sum() / blocks;
    }

    public long findQuantumEntanglement(int blocks) {
        int blockWeight = getBlockWeight(blocks);
        int availableWeight = blockWeight;
        Set<Integer> weightsInBag = findShortestConfiguration(availableWeight);

        int[] solution = stream(CombinatoricsUtils.combinationsIterator(weights.length, weightsInBag.size())) //
                .map(indexes -> {
                    int weights[] = new int[indexes.length];
                    for (int i = 0; i < indexes.length; i++) {
                        weights[i] = this.weights[indexes[i]];
                    }
                    return weights;
                }) //
                .filter(weights -> Arrays.stream(weights).sum() == blockWeight) //
                .min(Comparator.comparingLong(weights -> Arrays.stream(weights) //
                        .mapToLong(i -> (long) i) //
                        .reduce(1L, (a, v) -> a * v))) //
                .orElseThrow();

        return Arrays.stream(solution) //
                .mapToLong(i -> (long) i) //
                .reduce(1L, (a, v) -> a * v);
    }

    private static <T> Stream<T> stream(Iterator<T> iterator) {
        var spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false);
    }

    private Set<Integer> findShortestConfiguration(int availableWeight) {
        Set<Integer> weightsInBag = new LinkedHashSet<>();
        while (availableWeight > 0) {
            int previousWeight = availableWeight;
            for (int i = weights.length - 1; i >= 0; i--) {
                int weight = weights[i];
                if (!weightsInBag.contains(weight) && weight <= availableWeight) {
                    weightsInBag.add(weight);
                    availableWeight -= weight;
                    break;
                }
            }
            if (previousWeight == availableWeight) {
                break;
            }
        }
        if (availableWeight != 0) {
            throw new IllegalStateException("Unable to find weights configuration");
        }
        return weightsInBag;
    }

    public static GiftsBag parseInput(BufferedReader in) throws IOException {
        var weights = in.lines() //
                .map(String::trim) //
                .mapToInt(Integer::parseInt) //
                .toArray();
        return new GiftsBag(weights);
    }

}
