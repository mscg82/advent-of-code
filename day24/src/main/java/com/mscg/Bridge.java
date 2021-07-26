package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public record Bridge(List<Piece> pieces) {

    public int findStrongestBridge() {
        final Set<List<Piece>> bridges = findBridges();

        return bridges.stream() //
                .mapToInt(bridge -> bridge.stream().mapToInt(p -> p.left() + p.right()).sum()) //
                .max() //
                .orElseThrow();
    }

    public int findStrongestAndLongestBridge() {
        final Set<List<Piece>> bridges = findBridges();
        final int maxLength = bridges.stream().mapToInt(List::size).max().orElseThrow();

        return bridges.stream() //
                .filter(bridge -> bridge.size() == maxLength) //
                .mapToInt(bridge -> bridge.stream().mapToInt(p -> p.left() + p.right()).sum()) //
                .max() //
                .orElseThrow();
    }

    private Set<List<Piece>> findBridges() {
        record BridgeStep(List<Piece> connectedPieces, int right, List<Piece> availablePieces) {
        }

        final Set<List<Piece>> bridges = new HashSet<>();
        final var queue = new LinkedList<BridgeStep>();
        for (int i = 0, l = pieces.size(); i < l; i++) {
            final var piece = pieces.get(i);
            if (piece.left() == 0) {
                final var availablePieces = Stream.concat( //
                        this.pieces.subList(0, i).stream(), //
                        this.pieces.subList(i + 1, this.pieces.size()).stream()) //
                        .toList();
                final var step = new BridgeStep(List.of(piece), piece.right(), availablePieces);
                bridges.add(step.connectedPieces());
                queue.add(step);
            }
        }

        while (!queue.isEmpty()) {
            final var current = queue.pop();
            final var availablePieces = current.availablePieces();
            for (int i = 0, l = availablePieces.size(); i < l; i++) {
                final int currentIdx = i;
                final var piece = availablePieces.get(i);
                piece.attach(current.right()) //
                        .map(attaching -> {
                            final List<Piece> connectedPieces = Stream.concat(current.connectedPieces().stream(), Stream.of(attaching)).toList();
                            final List<Piece> newAvailablePieces = Stream.concat( //
                                    availablePieces.subList(0, currentIdx).stream(), //
                                    availablePieces.subList(currentIdx + 1, availablePieces.size()).stream()) //
                                    .toList();
                            return new BridgeStep(connectedPieces, attaching.right(), newAvailablePieces);
                        }) //
                        .filter(step -> bridges.add(step.connectedPieces())) //
                        .ifPresent(queue::add);
            }
        }
        return bridges;
    }

    public static Bridge parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Piece> pieces = in.lines() //
                    .map(Piece::from) //
                    .toList();
            return new Bridge(pieces);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static record Piece(int left, int right) {

        public Optional<Piece> attach(final int value) {
            if (value == left) {
                return Optional.of(this);
            } else if (value == right) {
                return Optional.of(new Piece(right, left));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            return left + "/" + right;
        }

        public static Piece from(final String value) {
            final String[] pieces = value.split("/");
            return new Piece(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
        }

    }

}
