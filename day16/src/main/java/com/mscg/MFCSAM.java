package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MFCSAM {

    private final Map<String, Integer> target;
    private final List<Aunt> aunts;

    public Aunt findAunt() {
        return aunts.stream() //
                .filter(aunt -> aunt.specs.entrySet().stream() //
                        .allMatch(specEntry -> target.getOrDefault(specEntry.getKey(), 0).equals(specEntry.getValue()))) //
                .findFirst() //
                .orElseThrow(() -> new IllegalStateException("Unable to find matching aunt"));
    }

    public static MFCSAM parseInput(BufferedReader in) throws IOException {
        String line;
        Map<String, Integer> target = new LinkedHashMap<>();
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }

            int index = line.indexOf(":");
            target.put(line.substring(0, index).trim(),
                    Integer.parseInt(line.substring(index + 1, line.length()).trim()));
        }

        List<Aunt> aunts = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            int index1 = line.indexOf(" ");
            int index2 = line.indexOf(":", index1 + 1);
            int number = Integer.parseInt(line.substring(index1 + 1, index2).trim());

            String[] specsStrs = line.substring(index2 + 1).trim().split(", ");
            Map<String, Integer> specs = Arrays.stream(specsStrs) //
                    .map(spec -> {
                        int index = spec.indexOf(":");
                        return Map.entry(spec.substring(0, index).trim(),
                                Integer.parseInt(spec.substring(index + 1, spec.length()).trim()));
                    }) //
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
            aunts.add(new Aunt(number, specs));
        }

        return new MFCSAM(Map.copyOf(target), List.copyOf(aunts));
    }

    public static record Aunt(int number, Map<String, Integer> specs) {
    }

}
