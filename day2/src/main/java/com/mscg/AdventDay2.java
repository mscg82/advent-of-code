package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;

public class AdventDay2 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final List<String> twos = new ArrayList<>();
            final List<String> threes = new ArrayList<>();
            in.lines() //
                    .forEach(line -> {
                        final Map<Character, Long> frequencyMap = line.chars() //
                                .boxed() //
                                .collect(Collectors.groupingBy(c -> (char) c.intValue(), Collectors.counting()));
                        final Set<Long> frequencies = new HashSet<>(frequencyMap.values());
                        if (frequencies.contains(2L)) {
                            twos.add(line);
                        }
                        if (frequencies.contains(3L)) {
                            threes.add(line);
                        }
                    });
            System.out.println("Part 1 - Answer %d".formatted(twos.size() * threes.size()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final List<String> ids = in.lines() //
                    .toList();
            String baseId = null;
            outerLoop:
            for (int i = 0, l = ids.size(); i < l - 1; i++) {
                final String idI = ids.get(i);
                for (int j = i + 1; j < l; j++) {
                    final String idJ = ids.get(j);
                    record CharCouple(int position, char first, char second) {
                    }
                    final List<CharCouple> differentChars = StreamUtils.zip(StreamUtils.zipWithIndex(idI.chars().boxed()), idJ.chars().boxed(),  //
                            (c1WithPos, c2) -> new CharCouple((int) c1WithPos.getIndex(), (char) c1WithPos.getValue().intValue(), (char) c2.intValue())) //
                            .filter(c -> c.first != c.second) //
                            .toList();
                    if (differentChars.size() == 1) {
                        final CharCouple charCouple = differentChars.get(0);
                        baseId = idI.substring(0, charCouple.position) + idI.substring(charCouple.position + 1);
                        break outerLoop;
                    }
                }
            }
            System.out.println("Part 2 - Answer %s".formatted(baseId));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay2.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
