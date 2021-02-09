package com.mscg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mscg.Tile.Pixel;

public final class Utils {

    public static <T> List<List<T>> immutableMatrix(List<List<T>> orig) {
        for (var it = orig.listIterator(); it.hasPrevious();) {
            List<T> row = it.next();
            it.set(List.copyOf(row));
        }
        return List.copyOf(orig);
    }

    public static <T> List<List<T>> rotate(List<List<T>> orig) {
        int rows = orig.size();
        int cols = orig.get(0).size();
        List<List<T>> rotated = IntStream.range(0, cols) //
                .mapToObj(i -> IntStream.range(0, rows) //
                        .mapToObj(j -> (T) null) //
                        .collect(Collectors.toList())) //
                .collect(Collectors.toList());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated.get(j).set(rows - i - 1, orig.get(i).get(j));
            }
        }

        return immutableMatrix(rotated);
    }

    public static Tile rebuildImage(List<List<Tile>> pieces) {
        int rowsInPieces = pieces.get(0).get(0).image().size();
        int colsInPieces = pieces.get(0).get(0).image().get(0).size();

        List<List<Pixel>> image = IntStream.range(0, (rowsInPieces - 2) * pieces.size()) //
                .mapToObj(i -> Arrays.asList(new Pixel[(colsInPieces - 2) * pieces.get(0).size()])) //
                .collect(Collectors.toList());

        for (int i = 0, r = pieces.size(); i < r; i++) {
            List<Tile> row = pieces.get(i);
            for (int j = 0, c = row.size(); j < c; j++) {
                Tile tile = row.get(j);
                List<List<Pixel>> tileImage = tile.image();
                for (int k1 = 1; k1 < rowsInPieces - 1; k1++) {
                    for (int k2 = 1; k2 < colsInPieces - 1; k2++) {
                        image.get(i * (rowsInPieces - 2) + (k1 - 1)).set(j * (colsInPieces - 2) + (k2 - 1),
                                tileImage.get(k1).get(k2));
                    }
                }
            }
        }

        return new Tile(0L, immutableMatrix(image));
    }

    private Utils() {
    }

}