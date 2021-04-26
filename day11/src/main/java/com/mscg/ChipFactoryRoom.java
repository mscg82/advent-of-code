package com.mscg;

import static com.mscg.ChipFactoryRoomComponentBuilder.Component;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

public record ChipFactoryRoom(Map<Floor, List<Component>> floors, Floor elevatorPosition) {

    @Override
    public String toString() {
        return floors.entrySet().stream() //
                .map(entry -> (entry.getKey().ordinal() + 1) + " -> " + entry.getValue() + (elevatorPosition == entry.getKey() ? " E" : "")) //
                .collect(Collectors.joining("\n"));
    }

    public static ChipFactoryRoom parseInput(final BufferedReader in) throws Exception {
        final var pattern = Pattern.compile("([a-z]+?)( generator|-compatible microchip)");

        final Map<Floor, List<Component>> floors = StreamUtils.zipWithIndex(in.lines()) //
                .map(indexAndLine -> {
                    final int index = (int) indexAndLine.getIndex();
                    final var floor = Floor.values()[index];

                    final String line = indexAndLine.getValue();

                    final var matcher = pattern.matcher(line);
                    final List<Component> components = new ArrayList<>();
                    while (matcher.find()) {
                        final String element = matcher.group(1);
                        final ComponentType type = switch (matcher.group(2).trim()) {
                            case "generator" -> ComponentType.GENERATOR;
                            default -> ComponentType.CHIP;
                        };
                        components.add(Component(element, type));
                    }

                    return Map.entry(floor, List.copyOf(components));
                }) //
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));

        return new ChipFactoryRoom(Collections.unmodifiableMap(floors), Floor.FIRST);
    }

    public enum Floor {
        FIRST, SECOND, THIRD, FOURTH;

        public List<Floor> adjacentFloors() {
            return switch (this) {
                case FIRST -> List.of(Floor.SECOND);
                case SECOND -> List.of(Floor.FIRST, Floor.THIRD);
                case THIRD -> List.of(Floor.SECOND, Floor.FOURTH);
                case FOURTH -> List.of(Floor.THIRD);
            };
        }
    }

    public enum ComponentType {
        GENERATOR, CHIP;

        @Override
        public String toString() {
            return switch (this) {
                case GENERATOR -> "generator";
                case CHIP -> "chip";
            };
        }
    }

    @RecordBuilder
    public record Component(@NonNull String element, @NonNull ComponentType type) {

        public boolean isCompatibleWith(final Component other) {
            return type == other.type() || element.equals(other.element());
        }

        @Override
        public String toString() {
            return switch (type) {
                case GENERATOR -> element + " " + type;
                case CHIP -> element + "-compatible " + type;
            };
        }

    }

}
