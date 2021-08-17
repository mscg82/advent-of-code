package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SleighAssembler(Map<String, List<String>> stepToRequired) {

    public String findSequence() {
        final Set<String> allNodes = stepToRequired.entrySet().stream() //
                .flatMap(entry -> Stream.concat(Stream.of(entry.getKey()), entry.getValue().stream())) //
                .collect(Collectors.toSet());

        final Set<String> visitedNodes = new LinkedHashSet<>();
        final Set<String> applicableNodes = new TreeSet<>();

        allNodes.stream() //
                .filter(node -> !stepToRequired.containsKey(node)) //
                .forEach(applicableNodes::add);

        while (!applicableNodes.isEmpty()) {
            final Iterator<String> it = applicableNodes.iterator();
            final String node = it.next();
            it.remove();
            visitedNodes.add(node);
            stepToRequired.entrySet().stream() //
                    .filter(entry -> !visitedNodes.contains(entry.getKey())) //
                    .filter(entry -> visitedNodes.containsAll(entry.getValue())) //
                    .map(Map.Entry::getKey) //
                    .forEach(applicableNodes::add);
        }

        return visitedNodes.stream().collect(Collectors.joining());
    }

    public static SleighAssembler parseInput(final BufferedReader in) throws IOException {
        try {
            final var pattern = Pattern.compile("Step ([A-Z]+) must be finished before step ([A-Z]+) can begin\\.");
            final Map<String, List<String>> stepToRequired = in.lines() //
                    .map(pattern::matcher) //
                    .filter(Matcher::find) //
                    .collect(Collectors.groupingBy(m -> m.group(2), Collectors.mapping(m -> m.group(1), Collectors.toList())));

            return new SleighAssembler(stepToRequired);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

}
