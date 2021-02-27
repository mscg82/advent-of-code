package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LightGrid {

    private final List<List<Light>> lights;

    public LightGrid next() {
        List<List<Light>> newLights = cloneLights();
        for (int i = 0, l = lights.size(); i < l; i++) {
            List<Light> row = lights.get(i);
            for (int j = 0, l2 = row.size(); j < l2; j++) {
                List<Light> neighbours = new ArrayList<>(8);
                for (int k1 = Math.max(0, i - 1); k1 <= Math.min(l - 1, i + 1); k1++) {
                    for (int k2 = Math.max(0, j - 1); k2 <= Math.min(l2 - 1, j + 1); k2++) {
                        if (k1 != i || k2 != j) {
                            neighbours.add(lights.get(k1).get(k2));
                        }
                    }
                }
                int onNeighbours = (int) neighbours.stream() //
                        .filter(light -> light == Light.ON) //
                        .count();
                newLights.get(i).set(j, switch (lights.get(i).get(j)) {
                    case ON -> switch (onNeighbours) {
                        case 2, 3 -> Light.ON;
                        default -> Light.OFF;
                    };
                    case OFF -> switch (onNeighbours) {
                        case 3 -> Light.ON;
                        default -> Light.OFF;
                    };
                });
            }
        }

        return new LightGrid(newLights.stream() //
                .map(List::copyOf) //
                .collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public String toString() {
        return lights.stream() //
                .map(row -> row.stream().map(Object::toString).collect(Collectors.joining())) //
                .collect(Collectors.joining("\n"));
    }

    private List<List<Light>> cloneLights() {
        return lights.stream() //
                .map(ArrayList::new) //
                .collect(Collectors.toList());
    }

    public static LightGrid parseInput(BufferedReader in) throws IOException {
        List<List<Light>> lights = in.lines() //
                .map(line -> line.chars() //
                        .mapToObj(c -> Light.of((char) c)) //
                        .collect(Collectors.toUnmodifiableList())) //
                .collect(Collectors.toUnmodifiableList());
        return new LightGrid(lights);
    }

    public enum Light {
        ON, OFF;

        @Override
        public String toString() {
            return switch (this) {
                case ON -> "#";
                case OFF -> ".";
            };
        }

        public static Light of(char c) {
            return switch (c) {
                case '#' -> Light.ON;
                case '.' -> Light.OFF;
                default -> throw new IllegalArgumentException("Invalid light state char " + c);
            };
        }
    }

}
