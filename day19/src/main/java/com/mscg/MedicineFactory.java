package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
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
        long step = 0;
        List<Replacement> inverseReplacements = replacements.stream() //
                .map(Replacement::reverse) //
                .map(r -> "e".equals(r.target()) ? new Replacement(Pattern.compile("^" + r.source() + "$"), r.target())
                        : r) //
                .sorted(Comparator.comparingInt((Replacement r) -> r.source().toString().length()).reversed()) //
                .collect(Collectors.toUnmodifiableList());

        String currentMolecule = molecule;

        var reducedMolecule = reduceMolecule(currentMolecule, inverseReplacements, true);
        step += reducedMolecule.steps();

        return step;
    }

    private ReducedMolecule reduceMolecule(String molecule, List<Replacement> inverseReplacements,
            boolean failOnNotUpdated) {
        long step = 0;
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
                if (failOnNotUpdated) {
                    throw new IllegalStateException("Unable to simplify the molecule");
                }
                break;
            }
        }
        return new ReducedMolecule(currentMolecule, step);
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

    private static record ReducedMolecule(String molecule, long steps) {
    }

}
