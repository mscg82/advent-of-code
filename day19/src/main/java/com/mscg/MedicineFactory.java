package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MedicineFactory {

    private final List<Replacement> replacements;
    private final String molecule;

    public Set<String> generateVariants() {
        return replacements.stream() //
                .flatMap(replacement -> replacement.source().matcher(molecule).results() //
                        .map(res -> new StringBuilder(molecule) //
                                .replace(res.start(), res.end(), replacement.target()) //
                                .toString())) //
                .collect(Collectors.toSet());
    }

    public static MedicineFactory parseInput(BufferedReader in) throws IOException {
        List<Replacement> replacements = in.lines() //
                .takeWhile(Predicate.not(String::isBlank)) //
                .map(s -> {
                    String[] parts = s.split(" => ");
                    return new Replacement(Pattern.compile(parts[0].trim()), parts[1]);
                }) //
                .collect(Collectors.toUnmodifiableList());
        return new MedicineFactory(replacements, in.readLine());
    }

    public static record Replacement(Pattern source, String target) {
    }

}
