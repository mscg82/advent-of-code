package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static record Process(String name, int weight, List<String> subprocesses) {

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
