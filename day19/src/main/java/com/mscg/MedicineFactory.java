package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    public static List<String> tokenizeMolecule(String molecule) {
        List<String> tokens = new ArrayList<>();
        int lastCut = 0;
        for (int i = 1, l = molecule.length(); i < l; i++) {
            char c = molecule.charAt(i);
            if (Character.isUpperCase(c)) {
                tokens.add(molecule.substring(lastCut, i));
                lastCut = i;
            }
        }
        tokens.add(molecule.substring(lastCut));
        return List.copyOf(tokens);
    }

    public long findSteps() {
        int i = 100;
        while (true) {
            try {
                return doFindSteps(i != 0);
            } catch (IllegalArgumentException e) {
                i++;
                if (i == 5) {
                    throw e;
                }
            }
        }
    }

    private long doFindSteps(boolean shuffle) {
        long step = 0;
        List<Replacement> inverseReplacements = replacements.stream() //
                .map(Replacement::reverse) //
                .map(r -> "e".equals(r.target()) ? new Replacement(Pattern.compile("^" + r.source() + "$"), r.target())
                        : r) //
                .collect(Collectors.toList());
        if (shuffle) {
            Collections.shuffle(inverseReplacements);
        }

        String currentMolecule = molecule;

        while (!"e".equals(currentMolecule)) {
            boolean updated = false;
            for (Replacement replacement : inverseReplacements) {
                var matcher = replacement.source().matcher(currentMolecule);
                if (matcher.find()) {
                    updated = true;
                    step++;
                    currentMolecule = matcher.replaceFirst(replacement.target());
                }
            }
            if (!updated) {
                throw new IllegalArgumentException("Unable to simplify molecule " + currentMolecule);
            }
        }

        return step;
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
