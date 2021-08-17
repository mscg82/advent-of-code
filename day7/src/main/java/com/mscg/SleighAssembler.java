package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        return String.join("", visitedNodes);
    }

    public TimedSequence findTimedSequence(final int workers, final int delay) {
        final Set<String> allNodes = stepToRequired.entrySet().stream() //
                .flatMap(entry -> Stream.concat(Stream.of(entry.getKey()), entry.getValue().stream())) //
                .collect(Collectors.toSet());

        final Set<String> visitedNodes = new LinkedHashSet<>();
        final Set<String> workedNodes = new HashSet<>();
        final Set<String> applicableNodes = new TreeSet<>();

        allNodes.stream() //
                .filter(node -> !stepToRequired.containsKey(node)) //
                .forEach(applicableNodes::add);

        record WorkingData(int startingTick, String node) {

        }

        final WorkingData[] workersData = new WorkingData[workers];
        int tick = -1;
        while (!applicableNodes.isEmpty() || Arrays.stream(workersData).anyMatch(Objects::nonNull)) {
            tick++;

            // make workers start working
            for (int i = 0; i < workersData.length && !applicableNodes.isEmpty(); i++) {
                if (workersData[i] == null) {
                    final Iterator<String> it = applicableNodes.iterator();
                    final String node = it.next();
                    it.remove();
                    workedNodes.add(node);
                    workersData[i] = new WorkingData(tick, node);
                }
            }

            // check if any worker has finished his work
            for (int i = 0; i < workersData.length; i++) {
                final var data = workersData[i];
                if (data == null) {
                    continue;
                }
                final int requiredTime = (data.node().charAt(0) - 'A' + 1) + delay;
                if (tick - data.startingTick >= requiredTime) {
                    visitedNodes.add(data.node());
                    stepToRequired.entrySet().stream() //
                            .filter(entry -> !visitedNodes.contains(entry.getKey()) && !workedNodes.contains(entry.getKey())) //
                            .filter(entry -> visitedNodes.containsAll(entry.getValue())) //
                            .map(Map.Entry::getKey) //
                            .forEach(applicableNodes::add);
                    workersData[i] = null;
                    // make workers start working
                    for (int j = 0; j < workersData.length && !applicableNodes.isEmpty(); j++) {
                        if (workersData[j] == null) {
                            final Iterator<String> it = applicableNodes.iterator();
                            final String node = it.next();
                            it.remove();
                            workedNodes.add(node);
                            workersData[j] = new WorkingData(tick, node);
                        }
                    }
                }
            }

        }

        return new TimedSequence(String.join("", visitedNodes), tick);
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

    public static record TimedSequence(String sequence, int time) {

    }

}
