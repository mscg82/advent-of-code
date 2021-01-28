package com.mscg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record Tile(long id, List<List<Pixel>> image) {

    public Tile flipHor() {
        List<List<Pixel>> newImage = new ArrayList<>(image.size());
        for (List<Pixel> row : image) {
            List<Pixel> newRow = new ArrayList<>(row.size());
            for (var it = row.listIterator(row.size()); it.hasPrevious();) {
                newRow.add(it.previous());
            }
            newImage.add(List.copyOf(newRow));
        }
        return new Tile(id, List.copyOf(newImage));
    }

    public Tile flipVer() {
        List<List<Pixel>> newImage = new ArrayList<>(image.size());
        for (var it = image.listIterator(image.size()); it.hasPrevious();) {
            newImage.add(List.copyOf(it.previous()));
        }
        return new Tile(id, List.copyOf(newImage));
    }

    public Stream<Tile> rotations() {
        return Stream.iterate(this, tile -> new Tile(tile.id, Utils.rotate(tile.image))) //
                .limit(4);
    }

    public boolean isAdjacentTo(final Tile tile) {
        var otherTiles = List.of(tile, tile.flipHor(), tile.flipVer(), tile.flipHor().flipVer());
        return otherTiles.stream() //
                .anyMatch(otherTile -> this.haveCommonEdge(otherTile));
    }

    public boolean haveCommonEdge(final Tile tile) {
        for (List<Pixel> edge1 : extractEdges()) {
            for (List<Pixel> edge2 : tile.extractEdges()) {
                if (edge1.equals(edge2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public long countBlackPixels() {
        return image.stream() //
                .flatMap(List::stream) //
                .filter(p -> p == Pixel.BLACK) //
                .count();
    }

    private List<List<Pixel>> extractEdges() {
        List<Pixel> left = new ArrayList<>(image.size());
        List<Pixel> right = new ArrayList<>(image.size());
        image.forEach(row -> {
            left.add(row.get(0));
            right.add(row.get(row.size() - 1));
        });
        return List.of(List.copyOf(image.get(0)), //
                List.copyOf(right), //
                List.copyOf(image.get(image.size() - 1)), //
                List.copyOf(left));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Tile ").append(id).append(":");
        image.stream() //
                .map(line -> line.stream().map(Pixel::toString).collect(Collectors.joining())) //
                .forEach(line -> str.append("\n").append(line));
        return str.toString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Pixel {
        BLACK('#'), WHITE('.'), GREY('O'), TRANSPARENT(' ');

        private final char value;

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static Optional<Pixel> fromChar(char c) {
            return switch (c) {
                case '#' -> Optional.of(BLACK);
                case '.' -> Optional.of(WHITE);
                case 'O' -> Optional.of(GREY);
                case ' ' -> Optional.of(TRANSPARENT);
                default -> Optional.empty();
            };
        }
    }

    public static Tile fromStrings(final List<String> lines) {
        String idLine = lines.get(0);
        int startIndex = idLine.indexOf(' ');
        int endIndex = idLine.lastIndexOf(':');
        long id = Long.parseLong(idLine.substring(startIndex + 1, endIndex));
        List<List<Pixel>> image = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            List<Pixel> pixelsLine = line.chars() //
                    .mapToObj(c -> Pixel.fromChar((char) c)) //
                    .filter(Optional::isPresent) //
                    .map(Optional::get) //
                    .collect(Collectors.toList());
            image.add(List.copyOf(pixelsLine));
        }
        return new Tile(id, List.copyOf(image));
    }
}