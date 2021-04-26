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
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

public record ChipFactoryRoom(Map<Floor, List<Component>> floors, Floor elevatorPosition) {

    public List<ChipFactoryRoom> generateNextStates() {
        record ElevatorContent(Component first, Component second) {

            Stream<Component> stream() {
                if (second == null) {
                    return Stream.of(first);
                }
                return Stream.of(first, second);
            }

        }

        final var components = floors.get(elevatorPosition);
        final var possibleElevatorContents = new ArrayList<ElevatorContent>();
        components.forEach(comp -> possibleElevatorContents.add(new ElevatorContent(comp, null)));
        for (int i = 0, l = components.size(); i < l - 1; i++) {
            for (int j = i + 1; j < l; j++) {
                final Component first = components.get(i);
                final Component second = components.get(j);
                if (first.isCompatibleWith(second)) {
                    possibleElevatorContents.add(new ElevatorContent(first, second));
                }
            }
        }

        final List<ChipFactoryRoom> nextPossibleRooms = new ArrayList<>();
        for (final var nextFloor : elevatorPosition.adjacentFloors()) {
            final var nextComponents = floors.get(nextFloor);
            for (final var content : possibleElevatorContents) {
                final Map<ComponentType, List<Component>> typeToComponents = Stream.concat(nextComponents.stream(), content.stream()) //
                        .collect(Collectors.groupingBy(Component::type));

                final List<Component> generators = typeToComponents.getOrDefault(ComponentType.GENERATOR, List.of());
                final List<Component> chips = typeToComponents.getOrDefault(ComponentType.CHIP, List.of());

                if (generators.isEmpty() || chips.stream().allMatch(chip -> generators.stream().anyMatch(generator -> generator.element().equals(chip.element())))) {
                    Map<Floor, List<Component>> newFloors = this.floors.entrySet().stream() //
                            .map(entry -> Map.entry(entry.getKey(), new ArrayList<>(entry.getValue()))) //
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));
                    newFloors.get(elevatorPosition).remove(content.first());
                    newFloors.get(nextFloor).add(content.first());
                    if (content.second() != null) {
                        newFloors.get(elevatorPosition).remove(content.second());
                        newFloors.get(nextFloor).add(content.second());
                    }

                    newFloors = newFloors.entrySet().stream() //
                            .map(entry -> Map.entry(entry.getKey(), List.copyOf(entry.getValue()))) //
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));
                    nextPossibleRooms.add(new ChipFactoryRoom(Collections.unmodifiableMap(newFloors), nextFloor));
                }
            }
        }

        return nextPossibleRooms;
    }

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
            return other == null || type == other.type() || element.equals(other.element());
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
