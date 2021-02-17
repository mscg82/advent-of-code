package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocationMap {

    private final Map<String, List<Connection>> nodeToConnections;

    public Optional<Path> findShortestPath() {
        Path shortestPath = null;
        for (var startingNode : nodeToConnections.keySet()) {
            Path path = new Path(0, List.of(startingNode));
            while (path.nodes().size() != nodeToConnections.size()) {
                final var currentPath = path;
                String lastNode = path.nodes().get(path.nodes().size() - 1);
                var connections = nodeToConnections.get(lastNode);
                var minConnection = connections.stream() //
                        .filter(conn -> !currentPath.nodes().contains(conn.target())) //
                        .min(Comparator.comparingInt(Connection::weight)) //
                        .orElseThrow();
                path = path.append(minConnection);
            }
            if (shortestPath == null || path.length() < shortestPath.length()) {
                shortestPath = path;
            }
        }
        return Optional.ofNullable(shortestPath);
    }

    public Optional<Path> findLongestPath() {
        Path longerPath = null;
        for (var startingNode : nodeToConnections.keySet()) {
            Path path = new Path(0, List.of(startingNode));
            while (path.nodes().size() != nodeToConnections.size()) {
                final var currentPath = path;
                String lastNode = path.nodes().get(path.nodes().size() - 1);
                var connections = nodeToConnections.get(lastNode);
                var minConnection = connections.stream() //
                        .filter(conn -> !currentPath.nodes().contains(conn.target())) //
                        .max(Comparator.comparingInt(Connection::weight)) //
                        .orElseThrow();
                path = path.append(minConnection);
            }
            if (longerPath == null || path.length() > longerPath.length()) {
                longerPath = path;
            }
        }
        return Optional.ofNullable(longerPath);
    }

    public static LocationMap parseInput(BufferedReader in) throws IOException {
        final var parsePattern = Pattern.compile("(.+) to (.+) = (\\d+)");

        Map<String, List<Connection>> partialNodeToConnections = in.lines() //
                .map(parsePattern::matcher) //
                .filter(Matcher::matches) //
                .collect(Collectors.groupingBy( //
                        m -> m.group(1), //
                        Collectors.mapping(m -> new Connection(m.group(2), Integer.parseInt(m.group(3))), //
                                Collectors.toList())));

        Map<String, List<Connection>> nodeToConnections = new LinkedHashMap<>();
        partialNodeToConnections.forEach((source, connections) -> {
            for (var connection : connections) {
                nodeToConnections.computeIfAbsent(source, __ -> new ArrayList<>()).add(connection);
                nodeToConnections.computeIfAbsent(connection.target(), __ -> new ArrayList<>())
                        .add(new Connection(source, connection.weight()));
            }
        });
        nodeToConnections.replaceAll((source, connections) -> connections.stream() //
                .sorted(Comparator.comparing(Connection::target)) //
                .collect(Collectors.toUnmodifiableList()));

        return new LocationMap(Map.copyOf(nodeToConnections));
    }

    public static record Connection(String target, int weight) {
    }

    public static record Path(int length, List<String> nodes) {

        public Path append(Connection connection) {
            return new Path(length + connection.weight(), //
                    Stream.concat(nodes.stream(), Stream.of(connection.target())) //
                    .collect(Collectors.toUnmodifiableList()));
        }

    }
}
