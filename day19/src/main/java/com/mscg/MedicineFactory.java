package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
        long step = 0;
        List<Replacement> inverseReplacements = replacements.stream() //
                .map(Replacement::reverse) //
                .map(r -> "e".equals(r.target()) ? new Replacement(Pattern.compile("^" + r.source() + "$"), r.target())
                        : r) //
                .sorted(Comparator.comparingInt((Replacement r) -> r.source().toString().length()).reversed()) //
                .collect(Collectors.toUnmodifiableList());

        var reducedMolecule = reduceMolecule(molecule, inverseReplacements, true);
        step += reducedMolecule.steps();

        return step;
    }

    private ReducedMolecule reduceMolecule(String molecule, List<Replacement> inverseReplacements,
            boolean failOnNotUpdated) {
        long step = 0;
        List<String> simplifiedTokens = new ArrayList<>();
        List<String> tokens = tokenizeMolecule(molecule);
        for (int i = 0, l = tokens.size(); i < l; i++) {
            String currentToken = tokens.get(i);
            simplifiedTokens.add(currentToken);
            if ("Rn".equals(currentToken)) {
                int count = 1;
                for (int j = i + 1; j < l; j++) {
                    String secondToken = tokens.get(j);
                    switch (secondToken) {
                        case "Rn" -> count++;
                        case "Ar" -> count--;
                        default -> {}
                    }
                    if (count == 0) {
                        String submolecule = tokens.subList(i + 1, j).stream().collect(Collectors.joining());
                        String[] parts = submolecule.split("Y");
                        for (int k = 0; k < parts.length; k++) {
                            String part = parts[k];
                            var reducedSubmolecule = reduceMolecule(part, inverseReplacements, false);
                            step += reducedSubmolecule.steps();
                            simplifiedTokens.add(reducedSubmolecule.molecule());
                            if (k != parts.length - 1) {
                                simplifiedTokens.add("Y");
                            }
                        }
                        simplifiedTokens.add(secondToken);
                        i = j;
                        break;
                    }
                }
            }
        }

        String currentMolecule = simplifiedTokens.stream().collect(Collectors.joining());
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
                    throw new IllegalStateException("Unable to simplify the molecule " + currentMolecule);
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
