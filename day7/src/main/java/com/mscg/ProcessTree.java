package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

public record ProcessTree(List<Process> processes) {

    public String findBaseProcess() {
        final Set<String> allSubProcesses = processes.stream() //
                .flatMap(p -> p.subprocesses().stream()) //
                .collect(Collectors.toSet());
        return processes.stream() //
                .filter(p -> !allSubProcesses.contains(p.name())) //
                .findFirst() //
                .map(Process::name) //
                .orElseThrow();
    }

    public Process findBalancedProcess() {
        final Map<String, Process> nameToProcess = processes.stream() //
                .collect(Collectors.toMap(Process::name, p -> p));

        final String baseProcess = findBaseProcess();

        final Map<String, Long> processToTotalWeight = new HashMap<>();
        computeWeight(nameToProcess.get(baseProcess), nameToProcess, processToTotalWeight);

        final List<Process> unbalancedProcesses = processes.stream() //
                .filter(p -> !p.subprocesses().isEmpty()) //
                .filter(p -> {
                    final long targetWeight = processToTotalWeight.get(p.subprocesses().get(0));
                    return p.subprocesses().stream().anyMatch(sub -> processToTotalWeight.get(sub) != targetWeight);
                }) //
                .toList();

        final Process unbalancedProcessParent = unbalancedProcesses.stream() //
                .min(Comparator.comparingLong(p -> p.subprocesses().stream().mapToLong(processToTotalWeight::get).min().orElseThrow())) //
                .orElseThrow();

        final LongSummaryStatistics statistics = unbalancedProcessParent.subprocesses.stream() //
                .mapToLong(processToTotalWeight::get) //
                .summaryStatistics();
        final int delta = (int) (statistics.getMax() - statistics.getMin());

        final Process unbalancedProcess = unbalancedProcessParent.subprocesses().stream() //
                .max(Comparator.comparingLong(processToTotalWeight::get)) //
                .map(nameToProcess::get) //
                .orElseThrow();

        final var balancedProcess = unbalancedProcess.with(p -> p.weight(p.weight() - delta));
        return balancedProcess;
    }

    private void computeWeight(final Process process, final Map<String, Process> nameToProcess, final Map<String, Long> processToTotalWeight) {
        if (process.subprocesses().isEmpty()) {
            processToTotalWeight.put(process.name(), (long) process.weight());
        } else {
            process.subprocesses().stream() //
                    .map(nameToProcess::get) //
                    .forEach(p -> computeWeight(p, nameToProcess, processToTotalWeight));
            processToTotalWeight.put(process.name(), process.weight() + process.subprocesses().stream().mapToLong(processToTotalWeight::get).sum());
        }
    }

    public static ProcessTree parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Process> processes = in.lines() //
                    .map(Process::parseLine) //
                    .toList();
            return new ProcessTree(processes);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @RecordBuilder
    public static record Process(String name, int weight, List<String> subprocesses) implements ProcessTreeProcessBuilder.With {

        public static final Pattern FORMAT = Pattern.compile("^(\\w+) \\((\\d+)\\)( -> (.*))?$");

        public static Process parseLine(final String line) {
            final var matcher = FORMAT.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Illegal input line " + line);
            }

            final List<String> subprocesses;
            if (matcher.group(4) != null) {
                subprocesses = Arrays.stream(matcher.group(4).split(",")) //
                        .map(String::trim) //
                        .toList();
            } else {
                subprocesses = List.of();
            }

            return new Process(matcher.group(1), Integer.parseInt(matcher.group(2)), subprocesses);
        }

    }

}
