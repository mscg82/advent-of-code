package com.mscg;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record Recipe(Set<String> ingredients, Set<String> allergenes) {

    public Recipe {
        ingredients = Collections.unmodifiableSet(ingredients);
        allergenes = Collections.unmodifiableSet(allergenes);
    }

    public static Recipe parseString(String line) {
        String splitter = "(contains";
        int index = line.indexOf(splitter);
        int endIndex = line.lastIndexOf(")");

        var ingredients = Arrays.stream(line.substring(0, index).split(" ")) //
                .filter(s -> !s.isBlank()) //
                .map(String::trim) //
                .collect(Collectors.toCollection(LinkedHashSet::new));
        var allergenes = Arrays.stream(line.substring(index + splitter.length(), endIndex).split(", ")) //
                .filter(s -> !s.isBlank()) //
                .map(String::trim) //
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new Recipe(ingredients, allergenes);
    }

}