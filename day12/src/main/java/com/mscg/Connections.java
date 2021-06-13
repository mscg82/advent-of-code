package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public record Connections(Map<Integer, Node> nodes) {

    public Set<Integer> findConnectedNodes(final int nodeId) {
        final Set<Integer> nodes = new HashSet<>();
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(nodeId);

        while (!queue.isEmpty()) {
            final int currentNodeId = queue.remove();
            nodes.add(currentNodeId);
            final Node currentNode = this.nodes.get(currentNodeId);
            currentNode.connectedIds().stream() //
                    .filter(id -> !nodes.contains(id)) //
                    .forEach(queue::add);
        }

        return Set.copyOf(nodes);
    }

    public static Connections parseInput(final BufferedReader in) throws IOException {
        try {
            final Map<Integer, Node> nodes = in.lines() //
                    .map(Node::from) //
                    .collect(Collectors.toMap(Node::id, n -> n));
            return new Connections(nodes);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Node(int id, Set<Integer> connectedIds) {

        public static Node from(final String line) {
            final String[] parts = line.split("<->");
            final Set<Integer> connectedIds = Arrays.stream(parts[1].split(",")) //
                    .map(v -> Integer.parseInt(v.trim())) //
                    .collect(Collectors.toUnmodifiableSet());
            return new Node(Integer.parseInt(parts[0].trim()), connectedIds);
        }

    }

}
