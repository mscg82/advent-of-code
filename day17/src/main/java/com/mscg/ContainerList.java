package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContainerList {

    private final int target;
    private final int[] containers;

    public long computeCombinations() {
        return generateCombinationsStream() //
                .count();
    }

    public int computeShorterCombinations() {
        Map<Integer, List<BitSet>> sizeToCombinations = generateCombinationsStream() //
                .collect(Collectors.groupingBy(BitSet::cardinality, TreeMap::new, Collectors.toList()));
        return sizeToCombinations.values().iterator().next().size();
    }

    private Stream<BitSet> generateCombinationsStream() {
        long maxValue = (1L << containers.length) - 1;

        return LongStream.range(1L, maxValue) //
                .parallel() //
                .mapToObj(l -> BitSet.valueOf(new long[] { l })) //
                .filter(bitSet -> {
                    int sum = bitSet.stream() //
                            .map(i -> containers[i]) //
                            .sum();
                    return sum == target;
                });
    }

    public static ContainerList parseInput(BufferedReader in) throws IOException {
        int target = Integer.parseInt(in.readLine());
        in.readLine();
        int[] containers = in.lines() //
                .mapToInt(Integer::parseInt) //
                .toArray();
        return new ContainerList(target, containers);
    }

}
