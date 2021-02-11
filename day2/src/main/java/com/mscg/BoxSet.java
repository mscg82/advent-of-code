package com.mscg;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BoxSet {

    private final List<Box> boxes;

    public static BoxSet parseInput(BufferedReader in) {
        var boxes = in.lines() //
                .map(Box::parseString) //
                .collect(Collectors.toUnmodifiableList());

        return new BoxSet(boxes);
    }

    public record Box(long width, long depth, long height) {

        public LongStream facesPerimeters() {
            return LongStream.of(//
                    width + width + depth + depth, //
                    width + width + height + height, //
                    depth + depth + height + height);
        }

        public LongStream facesAreas() {
            return LongStream.of( //
                    width * depth, //
                    width * height, //
                    depth * height);
        }

        public static Box parseString(String line) {
            String[] parts = line.split("x");
            return new Box(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }

    }
}
