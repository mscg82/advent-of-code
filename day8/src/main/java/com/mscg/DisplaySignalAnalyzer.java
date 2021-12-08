package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record DisplaySignalAnalyzer(List<TestSignal> signals) {

    public static DisplaySignalAnalyzer parseInput(final BufferedReader in) throws IOException {
        try {
            final List<TestSignal> signals = in.lines() //
                    .map(line -> line.split(" \\| ")) //
                    .map(parts -> {
                        final List<String> tests = List.copyOf(Arrays.asList(parts[0].split(" ")));
                        final List<String> outputs = List.copyOf(Arrays.asList(parts[1].split(" ")));
                        return new TestSignal(tests, outputs);
                    }) //
                    .toList();
            return new DisplaySignalAnalyzer(signals);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static String[] decodeTests(final List<String> tests) {
        final String[] decoded = new String[10];

        for (final String s : tests) {
            final int decodedIndex = switch (s.length()) {
                case 2 -> 1;
                case 3 -> 7;
                case 4 -> 4;
                case 7 -> 8;
                default -> -1;
            };
            if (decodedIndex != -1) {
                decoded[decodedIndex] = s;
            }
        }

        final char[] segBD = asChar(decoded[4].chars().filter(c -> decoded[1].indexOf(c) == -1).toArray());
        final char[] segEG = asChar(decoded[8].chars() //
                .filter(c -> decoded[7].indexOf(c) == -1).filter(c -> decoded[4].indexOf(c) == -1) //
                .toArray());

        final List<String> set235 = tests.stream() // 2, 3 or 5
                .filter(s -> s.length() == 5) //
                .toList();

        final Map<Boolean, List<String>> split235 = set235.stream() //
                .collect(Collectors.partitioningBy(s -> s.indexOf(decoded[1].charAt(0)) != -1 && s.indexOf(decoded[1].charAt(1)) != -1));

        decoded[3] = split235.get(Boolean.TRUE).get(0); // only 3 contains 1

        final char segD = decoded[3].indexOf(segBD[0]) == -1 ? segBD[1] : segBD[0];

        final char segE = decoded[3].indexOf(segEG[0]) == -1 ? segEG[0] : segEG[1];

        final Map<Boolean, List<String>> split25 = split235.get(Boolean.FALSE).stream() //
                .collect(Collectors.partitioningBy(s -> s.indexOf(segE) != -1));

        decoded[2] = split25.get(Boolean.TRUE).get(0); // only 2 contains segment e
        decoded[5] = split25.get(Boolean.FALSE).get(0); // only 5 doesn't contain segment e

        final List<String> set069 = tests.stream() // 0, 6 or 9
                .filter(s -> s.length() == 6) //
                .toList();
        final Map<Boolean, List<String>> split069 = set069.stream() //
                .collect(Collectors.partitioningBy(s -> s.indexOf(segD) != -1));

        decoded[0] = split069.get(Boolean.FALSE).get(0); // only 0 doesn't contain segment d

        final Map<Boolean, List<String>> split69 = split069.get(Boolean.TRUE).stream() //
                .collect(Collectors.partitioningBy(s -> s.indexOf(segE) != -1));

        decoded[6] = split69.get(Boolean.TRUE).get(0); // only 6 contains segment e
        decoded[9] = split69.get(Boolean.FALSE).get(0); // only 9 doesn't contain segment e

        return decoded;
    }

    private static char[] asChar(final int[] chars) {
        final char[] ret = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            ret[i] = (char) chars[i];
        }
        return ret;
    }

    private static Set<Integer> stringAsCharSet(final String str) {
        return str.chars().boxed().collect(Collectors.toSet());
    }

    private static int decodeString(final List<Set<Integer>> decodedAsSets, final String str) {
        final Set<Integer> strAsChars = stringAsCharSet(str);
        final int val = decodedAsSets.indexOf(strAsChars);
        if (val < 0) {
            throw new IllegalArgumentException("Unable to decode string " + str);
        }
        return val;
    }

    public long countUniqueOutputs() {
        return signals.stream() //
                .flatMap(signal -> signal.outputs().stream()) //
                .filter(value -> value.length() == 2 || value.length() == 3 || value.length() == 4 || value.length() == 7) //
                .count();
    }

    public long decodeAnSum() {
        return signals.stream() //
                .mapToLong(signal -> {
                    final String[] decoded = decodeTests(signal.tests());
                    final List<Set<Integer>> decodedAsSets = Arrays.stream(decoded) //
                            .map(DisplaySignalAnalyzer::stringAsCharSet) //
                            .toList();
                    final int val0 = decodeString(decodedAsSets, signal.outputs().get(0));
                    final int val1 = decodeString(decodedAsSets, signal.outputs().get(1));
                    final int val2 = decodeString(decodedAsSets, signal.outputs().get(2));
                    final int val3 = decodeString(decodedAsSets, signal.outputs().get(3));

                    return val0 * 1000L + val1 * 100L + val2 * 10L + val3;
                }) //
                .sum();
    }

    public record TestSignal(List<String> tests, List<String> outputs) {

    }

}
