package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MedicineFactory {

    private final List<Replacement> replacements;
    private final String molecule;

    public Set<String> generateVariants() {
        return applyVariants(molecule, replacements) //
                .collect(Collectors.toSet());
    }

    private Stream<String> applyVariants(String molecule, List<Replacement> replacements) {
        return replacements.stream() //
                .flatMap(replacement -> replacement.source().matcher(molecule).results() //
                        .map(res -> new StringBuilder(molecule) //
                                .replace(res.start(), res.end(), replacement.target()) //
                                .toString()));
    }

    public long findSteps() {
        Set<String> startingPoints = replacements.stream() //
                .filter(r -> "e".equals(r.source().toString())) //
                .map(Replacement::target) //
                .collect(Collectors.toSet());

        List<Replacement> reverseReplacements = replacements.stream() //
                .map(Replacement::reverse) //
                .collect(Collectors.toUnmodifiableList());

        long step = 1;
        Set<String> molecules = Set.of(molecule);
        while (!matches(startingPoints, molecules)) {
            step++;
            molecules = molecules.stream() //
                    .flatMap(molecule -> applyVariants(molecule, reverseReplacements)) //
                    .filter(s -> s.length() < molecule.length()) //
                    .collect(Collectors.toUnmodifiableSet());
        }
        return step;
    }

    private boolean matches(Set<String> startingPoints, Set<String> molecules) {
        return startingPoints.stream().anyMatch(molecules::contains);
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

        public Replacement reverse() {
            return new Replacement(Pattern.compile(target), source.toString());
        }

    }

}
