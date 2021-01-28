package com.mscg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mscg.Tile.Pixel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Mask {

    private final List<List<Pixel>> mask;

    public List<List<Pixel>> mask() {
        return mask;
    }

    public List<List<Pixel>> apply(List<List<Pixel>> image) {
        List<List<Pixel>> newImage = image.stream() //
                .map(ArrayList::new) //
                .collect(Collectors.toList());

        int imageRow = image.size();
        int imageCols = image.get(0).size();

        int maskRows = mask.size();
        int maskCols = mask.get(0).size();

        for (int i = 0; i < imageRow - maskRows; i++) {
            for (int j = 0; j < imageCols - maskCols; j++) {
                boolean matches = maskMatches(image, i, j);
                if (matches) {
                    for (int im = 0; im < maskRows; im++) {
                        for (int jm = 0; jm < maskCols; jm++) {
                            var maskPixel = mask.get(im).get(jm);
                            switch(maskPixel) {
                                case BLACK -> image.get(i + im).set(j + jm, Pixel.GREY);
                                default -> { /* do nothing here */}
                            }
                        }
                    }
                }
            }
        }

        return Utils.immutableMatrix(newImage);
    }

    private boolean maskMatches(List<List<Pixel>> image, int iOffset, int jOffset) {
        int maskRows = mask.size();
        int maskCols = mask.get(0).size();

        for (int im = 0; im < maskRows; im++) {
            for (int jm = 0; jm < maskCols; jm++) {
                var maskPixel = mask.get(im).get(jm);
                var imagePixel = image.get(iOffset + im).get(jOffset + jm);
                boolean matches = switch(maskPixel) {
                    case TRANSPARENT -> true;
                    case BLACK -> imagePixel == Pixel.BLACK;
                    default -> throw new IllegalArgumentException("Invalid pixel " + maskPixel + " in mask");
                };
                if (!matches) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return mask.stream() //
                .map(line -> line.stream().map(Pixel::toString).collect(Collectors.joining())) //
                .collect(Collectors.joining("\n"));
    }

    public static Mask parseStrings(List<String> lines) {
        List<List<Pixel>> image = new ArrayList<>();
        for (String line : lines) {
            List<Pixel> pixelsLine = line.chars() //
                    .mapToObj(c -> Pixel.fromChar((char) c)) //
                    .filter(Optional::isPresent) //
                    .map(Optional::get) //
                    .collect(Collectors.toList());
            image.add(List.copyOf(pixelsLine));
        }
        return new Mask(List.copyOf(image));
    }

}