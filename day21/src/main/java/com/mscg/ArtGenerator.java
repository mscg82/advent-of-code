package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record ArtGenerator(Map<Pattern, Pattern> rules) {

    public Pattern generateImage(final int iterations) {
        return generateImage(Pattern.fromString(".#./..#/###"), iterations);
    }

    public Pattern generateImage(final Pattern initialImage, final int iterations) {
        Pattern image = initialImage;

        final Map<Pattern, Pattern> extendedRules = rules.entrySet().stream() //
                .flatMap(entry -> entry.getKey().variants().stream() //
                        .map(variant -> Map.entry(variant, entry.getValue()))) //
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        for (int it = 0; it < iterations; it++) {
            final int blockSize = switch (image.pixels().size() % 2) {
                case 0 -> 2;
                default -> 3;
            };

            final int numBlocks = image.pixels().size() / blockSize;

            final var blocks = IntStream.range(0, numBlocks) //
                    .mapToObj(__ -> Arrays.asList(new Pattern[numBlocks])) //
                    .toList();

            for (int k1 = 0; k1 < numBlocks; k1++) {
                for (int k2 = 0; k2 < numBlocks; k2++) {
                    final var block = IntStream.range(0, blockSize) //
                            .mapToObj(__ -> Arrays.asList(new PixelStatus[blockSize])) //
                            .toList();
                    for (int i = 0; i < blockSize; i++) {
                        final List<PixelStatus> blockRow = block.get(i);
                        final List<PixelStatus> imageRow = image.pixels().get(i + k1 * blockSize);
                        for (int j = 0; j < blockSize; j++) {
                            blockRow.set(j, imageRow.get(j + k2 * blockSize));
                        }
                    }

                    final var blockPattern = new Pattern(block);
                    final var replacedPattern = extendedRules.get(blockPattern);
                    if (replacedPattern == null) {
                        throw new IllegalStateException("Unable to find a replacement for pattern\n" + blockPattern);
                    }
                    blocks.get(k1).set(k2, replacedPattern);
                }
            }

            final int newBlockSize = blockSize + 1;
            final var newImage = IntStream.range(0, newBlockSize * numBlocks) //
                    .mapToObj(__ -> Arrays.asList(new PixelStatus[newBlockSize * numBlocks])) //
                    .toList();
            for (int k1 = 0; k1 < numBlocks; k1++) {
                for (int k2 = 0; k2 < numBlocks; k2++) {
                    final var block = blocks.get(k1).get(k2);
                    for (int i = 0; i < newBlockSize; i++) {
                        for (int j = 0; j < newBlockSize; j++) {
                            newImage.get(i + k1 * newBlockSize).set(j + k2 * newBlockSize, block.pixels().get(i).get(j));
                        }
                    }
                }
            }

            image = new Pattern(newImage);
        }

        return new Pattern(image.pixels().stream().map(List::copyOf).toList());
    }

    public static ArtGenerator parseInput(final BufferedReader in) throws IOException {
        try {
            final Map<Pattern, Pattern> rules = in.lines() //
                    .map(line -> line.split(" => ")) //
                    .collect(Collectors.toMap(parts -> Pattern.fromString(parts[0]), parts -> Pattern.fromString(parts[1]), (v1, v2) -> v1, LinkedHashMap::new));
            return new ArtGenerator(Collections.unmodifiableMap(rules));
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public enum PixelStatus {
        ON, OFF;

        @Override
        public String toString() {
            return switch (this) {
                case ON -> "#";
                case OFF -> ".";
            };
        }

        public static PixelStatus fromChar(final char c) {
            return switch (c) {
                case '#' -> ON;
                case '.' -> OFF;
                default -> throw new IllegalArgumentException("Unsupported pixel status " + c);
            };
        }
    }

    public static record Pattern(List<List<PixelStatus>> pixels) {

        public Set<Pattern> variants() {
            return Stream.iterate(this, Pattern::rotateCW) //
                    .limit(4) //
                    .flatMap(pattern -> Stream.of(pattern, pattern.flipHor(), pattern.flipVer(), pattern.flipBoth()))
                    .collect(Collectors.toUnmodifiableSet());
        }

        public Pattern rotateCW() {
            final List<List<PixelStatus>> rotatedPixels = pixels.stream() //
                    .map(row -> Arrays.asList(new PixelStatus[row.size()])) //
                    .toList();

            for (int i = 0, rows = pixels.size(); i < rows; i++) {
                final List<PixelStatus> row = pixels.get(i);
                for (int j = 0, cols = row.size(); j < cols; j++) {
                    rotatedPixels.get(j).set(row.size() - i - 1, row.get(j));
                }
            }

            return new Pattern(rotatedPixels.stream() //
                    .map(List::copyOf) //
                    .toList());
        }

        public Pattern flipHor() {
            final List<List<PixelStatus>> flippedPixels = pixels.stream() //
                    .map(row -> Arrays.asList(new PixelStatus[row.size()])) //
                    .toList();

            for (int i = 0, rows = pixels.size(); i < rows; i++) {
                final List<PixelStatus> row = pixels.get(i);
                for (int j = 0, cols = row.size(); j < cols; j++) {
                    flippedPixels.get(rows - i - 1).set(j, row.get(j));
                }
            }

            return new Pattern(flippedPixels.stream() //
                    .map(List::copyOf) //
                    .toList());
        }

        public Pattern flipVer() {
            final List<List<PixelStatus>> flippedPixels = pixels.stream() //
                    .map(row -> Arrays.asList(new PixelStatus[row.size()])) //
                    .toList();

            for (int i = 0, rows = pixels.size(); i < rows; i++) {
                final List<PixelStatus> row = pixels.get(i);
                for (int j = 0, cols = row.size(); j < cols; j++) {
                    flippedPixels.get(i).set(cols - j - 1, row.get(j));
                }
            }

            return new Pattern(flippedPixels.stream() //
                    .map(List::copyOf) //
                    .toList());
        }

        public Pattern flipBoth() {
            final List<List<PixelStatus>> flippedPixels = pixels.stream() //
                    .map(row -> Arrays.asList(new PixelStatus[row.size()])) //
                    .toList();

            for (int i = 0, rows = pixels.size(); i < rows; i++) {
                final List<PixelStatus> row = pixels.get(i);
                for (int j = 0, cols = row.size(); j < cols; j++) {
                    flippedPixels.get(rows - i - 1).set(cols - j - 1, row.get(j));
                }
            }

            return new Pattern(flippedPixels.stream() //
                    .map(List::copyOf) //
                    .toList());
        }

        @Override
        public String toString() {
            return pixels.stream() //
                    .map(row -> row.stream().map(PixelStatus::toString).collect(Collectors.joining())) //
                    .collect(Collectors.joining("\n"));
        }

        public static Pattern fromString(final String line) {
            final List<List<PixelStatus>> pixels = Arrays.stream(line.split("/")) //
                    .map(row -> row.chars() //
                            .mapToObj(c -> PixelStatus.fromChar((char) c)) //
                            .toList()) //
                    .toList();
            return new Pattern(pixels);
        }
    }
}
