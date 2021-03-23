package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Descrambler {
    
    public final List<String> messages;

    public String clean1() {
        return clean(Comparator.comparingInt((Map.Entry<Character, Integer> e) -> e.getValue()).reversed());
    }

    public String clean2() {
        return clean(Comparator.comparingInt((Map.Entry<Character, Integer> e) -> e.getValue()));
    }
    private String clean(Comparator<Map.Entry<Character, Integer>> frequencySorter) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        int length = messages.get(0).length();
        StringBuilder message = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            frequencyMap.clear();
            for (String m : messages) {
                frequencyMap.merge(m.charAt(i), 1, Integer::sum);
            }
            char c = frequencyMap.entrySet().stream() //
                    .sorted(frequencySorter) //
                    .findFirst() //
                    .map(Map.Entry::getKey) //
                    .orElseThrow();
            message.append(c);
        }
        return message.toString();
    }

    public static Descrambler parseInput(BufferedReader in) throws IOException {
        List<String> messages = in.lines() //
                .collect(Collectors.toUnmodifiableList());
        return new Descrambler(messages);
    }
}
