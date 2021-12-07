package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public record Intcode(int[] data) {

    private static final int CODE_EXIT = 99;

    private static final int CODE_ADD = 1;

    private static final int CODE_MULTIPLY = 2;

    public static Intcode parseInput(final BufferedReader in) throws IOException {
        final int[] data = Arrays.stream(in.readLine().split(",")) //
                .mapToInt(Integer::parseInt) //
                .toArray();
        return new Intcode(data);
    }

    public int execute(final int noun, final int verb) {
        // defensive copy to avoid modifying input data
        final int[] data = this.data.clone();
        data[1] = noun;
        data[2] = verb;

        int ip = 0;
        while (ip >= 0) {
            ip = switch (data[ip]) {
                case CODE_ADD -> {
                    final int p1 = data[ip + 1];
                    final int p2 = data[ip + 2];
                    final int p3 = data[ip + 3];
                    data[p3] = data[p1] + data[p2];
                    yield ip + 4;
                }
                case CODE_MULTIPLY -> {
                    final int p1 = data[ip + 1];
                    final int p2 = data[ip + 2];
                    final int p3 = data[ip + 3];
                    data[p3] = data[p1] * data[p2];
                    yield ip + 4;
                }
                case CODE_EXIT -> -1;
                default -> throw new IllegalStateException("Unknown opcode " + data[ip]);
            };
        }

        return data[0];
    }

}
