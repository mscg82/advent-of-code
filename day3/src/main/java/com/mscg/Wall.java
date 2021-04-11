package com.mscg;

import static com.mscg.WallTriangleBuilder.Triangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Wall {

    private static final Pattern PATTERN = Pattern.compile("(\\s+(\\d+))(\\s+(\\d+))(\\s+(\\d+))");

    private final List<Triangle> triangles;

    public long countValidTriangles() {
        return triangles.stream() //
                .filter(Triangle::isValid) //
                .count();
    }

    public static Wall parseInput1(BufferedReader in) throws IOException {
        List<Triangle> triangles = in.lines() //
                .map(PATTERN::matcher) //
                .filter(Matcher::find) //
                .map(matcher -> Triangle(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(6)))) //
                .toList();

        return new Wall(triangles);
    }

    public static Wall parseInput2(BufferedReader in) throws IOException {
        int[] values = in.lines() //
                .map(PATTERN::matcher) //
                .filter(Matcher::find) //
                .flatMap(matcher -> Stream.of(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(6)))) //
                .mapToInt(Integer::intValue) //
                .toArray();
        List<Triangle> triangles = new ArrayList<>(values.length / 3);
        for (int i = 0; i < values.length; i += 9) {
            triangles.add(Triangle(values[i], values[i + 3], values[i + 6]));
            triangles.add(Triangle(values[i + 1], values[i + 4], values[i + 7]));
            triangles.add(Triangle(values[i + 2], values[i + 5], values[i + 8]));
        }

        return new Wall(List.copyOf(triangles));
    }

    @RecordBuilder
    public static record Triangle(int a, int b, int c) implements WallTriangleBuilder.With {

        public boolean isValid() {
            return (a + b > c) && (a + c > b) && (b + c > a);
        }

    }

}
