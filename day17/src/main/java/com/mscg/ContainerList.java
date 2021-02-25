package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.stream.LongStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContainerList {
    
    private final int target;
    private final int[] containers;

    public long computeCombinations() {
        long maxValue = (1L << containers.length) - 1;

        return LongStream.range(1L, maxValue) //
                .parallel() //
                .filter(l -> {
                    var bitSet = BitSet.valueOf(new long[] { l });
                    int sum = bitSet.stream() //
                            .map(i -> containers[i]) //
                            .sum();
                    return sum == target;
                }) //
                .count();
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
