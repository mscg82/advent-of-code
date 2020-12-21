package com.mscg;

import java.io.BufferedReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.OptionalLong;

public record XMASReader(long[] values, int preambleSize) {

    public OptionalLong getFirstInvalidValue() {
        Deque<Long> queue = new ArrayDeque<>(preambleSize);
        Arrays.stream(values)
                .limit(preambleSize)
                .forEach(queue::add);

        return Arrays.stream(values)
                .skip(preambleSize)
                .filter(value -> {
                    long[] queueValues = queue.stream()
                            .mapToLong(Long::longValue)
                            .toArray();

                    boolean isValid = false;
                    chekcsumLoop: for (int i = 0; i < queueValues.length - 1; i++) {
                        for (int j = i + 1; j < queueValues.length; j++) {
                            if (queueValues[i] + queueValues[j] == value) {
                                isValid = true;
                                break chekcsumLoop;
                            }
                        }
                    }

                    queue.pop();
                    queue.add(value);

                    return !isValid;
                })
                .findFirst();
    }

    public Optional<long[]> getBreakingSequence() {
        long invalidValue;
        {
            OptionalLong optInvalidValue = getFirstInvalidValue();
            if (optInvalidValue.isEmpty()) {
                return Optional.empty();
            }
            invalidValue = optInvalidValue.getAsLong();
        }

        for (int i = 0; i < values.length; i++) {
            long sum = values[i];
            int j = i + 1;
            while (sum < invalidValue) {
                sum += values[j++];
            }
            if (sum == invalidValue) {
                long[] sequence = new long[j - i];
                System.arraycopy(values, i, sequence, 0, sequence.length);
                return Optional.of(sequence);
            }
        }

        return Optional.empty();
    }

    public static XMASReader parseInput(BufferedReader in, int preambleSize) {
        long[] values = in.lines()
                .mapToLong(Long::parseLong)
                .toArray();
        return new XMASReader(values, preambleSize);
    }

}
