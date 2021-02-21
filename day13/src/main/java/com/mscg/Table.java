package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Table {

    private final Map<Couple, Integer> coupleToHappiness;

    public Table withMyself() {
        Map<Couple, Integer> coupleToHappiness = new HashMap<>(this.coupleToHappiness);
        getAllNames().forEach(name -> {
            coupleToHappiness.put(new Couple("me", name), 0);
            coupleToHappiness.put(new Couple(name, "me"), 0);
        });

        return new Table(coupleToHappiness);
    }

    public List<String> getAllNames() {
        return coupleToHappiness.keySet().stream() //
                .map(Couple::left) //
                .distinct() //
                .sorted() //
                .collect(Collectors.toUnmodifiableList());
    }

    public Arrangement findBestArrangement() {
        List<List<String>> variants = generateVariants(getAllNames());
        Arrangement bestArragement = null;
        for (List<String> variant : variants) {
            int happiness = 0;
            for (int i = 0, l = variant.size(); i < l; i++) {
                String current = variant.get(i);
                String previous = i == 0 ? variant.get(l - 1) : variant.get(i - 1);
                String next = i == l - 1 ? variant.get(0) : variant.get(i + 1);
                happiness += coupleToHappiness.get(new Couple(current, previous));
                happiness += coupleToHappiness.get(new Couple(current, next));
            }
            if (bestArragement == null || happiness > bestArragement.happiness()) {
                bestArragement = new Arrangement(variant, happiness);
            }
        }
        return bestArragement;
    }

    public static <T> List<List<T>> generateVariants(List<T> values) {
        List<List<T>> variants = new ArrayList<>();
        
        int size = values.size();
        int[] c = new int[size];
        variants.add(List.copyOf(values));
        
        var modifiableValues = new ArrayList<>(values);
        int i = 0;
        while (i < size) {
            if (c[i] < i) {
                if (i % 2 == 0) {
                    swap(modifiableValues, 0, i);
                } else {
                    swap(modifiableValues, c[i], i);
                }
                variants.add(List.copyOf(modifiableValues));
                c[i] = c[i] + 1;
                i = 1;
            } else {
                c[i] = 0;
                i++;
            }
        }

        return variants;
    }

    private static <T> void swap(List<T> values, int index1, int index2) {
        T tmp = values.get(index1);
        values.set(index1, values.get(index2));
        values.set(index2, tmp);
    }

    public static Table parseInput(BufferedReader in) throws IOException {
        var pattern = Pattern.compile("^(.+?) would (gain|lose) (\\d+) happiness units by sitting next to (.+?)\\.?$");
        Map<Couple, Integer> coupleToHappiness = in.lines() //
                .map(pattern::matcher) //
                .filter(Matcher::matches) //
                .map(matcher -> Map.entry(new Couple(matcher.group(1), matcher.group(4)), switch (matcher.group(2)) {
                    case "gain" -> Integer.parseInt(matcher.group(3));
                    case "lose" -> -Integer.parseInt(matcher.group(3));
                    default -> throw new IllegalArgumentException("Invalid verb " + matcher.group(2));
                })) //
                .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
        return new Table(coupleToHappiness);
    }

    public static record Couple(String left, String right) {
    }

    public static record Arrangement(List<String> arrangement, int happiness){
    }

}
