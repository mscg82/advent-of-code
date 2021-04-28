package com.mscg;

import static com.mscg.ChipFactoryRoomComponentBuilder.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

public record ChipFactoryRoom(Map<Floor, List<Component>> floors, Floor elevatorPosition) {

    public ChipFactoryRoom withAdditionalComponents(final List<Component> components) {
        final Map<Floor, List<Component>> newFloors = floors.entrySet().stream() //
                .map(entry -> {
                    if (entry.getKey() != Floor.FIRST) {
                        return entry;
                    }
                    final var newComponents = new ArrayList<>(entry.getValue());
                    newComponents.addAll(components);
                    Collections.sort(newComponents);
                    return Map.entry(entry.getKey(), List.copyOf(newComponents));
                }) //
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));
        return new ChipFactoryRoom(newFloors, elevatorPosition);
    }

    public List<ChipFactoryRoom> generateNextStates(final boolean optimized) {
        record ElevatorContent(Component first, Component second) {

            public boolean isCouple() {
                return second != null && first.isCompatibleWith(second) && first.type() != second.type();
            }

            public Stream<Component> stream() {
                return second == null ? Stream.of(first) : Stream.of(first, second);
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

        boolean skipLowerFloor = false;
        if (optimized) {
            possibleElevatorContents.stream() //
                    .filter(ElevatorContent::isCouple) //
                    .findFirst() //
                    .ifPresent(firstCouple -> possibleElevatorContents.removeIf(content -> content.isCouple() && content != firstCouple));

            skipLowerFloor = Arrays.stream(Floor.values()) //
                    .filter(floor -> floor.ordinal() < elevatorPosition.ordinal()) //
                    .map(floors::get) //
                    .allMatch(List::isEmpty);
        }

        final List<ChipFactoryRoom> nextPossibleRooms = new ArrayList<>();
        for (final var nextFloor : elevatorPosition.adjacentFloors()) {
            if (skipLowerFloor && nextFloor.ordinal() < elevatorPosition.ordinal()) {
                continue;
            }

            final var nextComponents = floors.get(nextFloor);
            for (final var content : possibleElevatorContents) {
                final Map<ComponentType, List<Component>> typeToComponents = Stream.concat(nextComponents.stream(), content.stream()) //
                        .collect(Collectors.groupingBy(Component::type));

                final List<Component> generators = typeToComponents.getOrDefault(ComponentType.GENERATOR, List.of());
                final List<Component> chips = typeToComponents.getOrDefault(ComponentType.CHIP, List.of());

                if (generators.isEmpty() || chips.stream().allMatch(chip -> generators.stream().anyMatch(generator -> generator.element().equals(chip.element())))) {
                    final var elevatorNewFloor = new ArrayList<>(floors.get(elevatorPosition));
                    final var nextNewFloor = new ArrayList<>(floors.get(nextFloor));

                    elevatorNewFloor.remove(content.first());
                    nextNewFloor.add(content.first());
                    if (content.second() != null) {
                        elevatorNewFloor.remove(content.second());
                        nextNewFloor.add(content.second());
                    }

                    final Map<Floor, List<Component>> newFloors = this.floors.entrySet().stream() //
                            .map(entry -> {
                                if (entry.getKey() == elevatorPosition || entry.getKey() == nextFloor) {
                                    final List<Component> newComponents = entry.getKey() == elevatorPosition ? elevatorNewFloor : nextNewFloor;
                                    Collections.sort(newComponents);
                                    return Map.entry(entry.getKey(), List.copyOf(newComponents));
                                }
                                return entry;
                            }) //
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));
                    nextPossibleRooms.add(new ChipFactoryRoom(Collections.unmodifiableMap(newFloors), nextFloor));
                }
            }
        }

        return nextPossibleRooms;
    }

    public List<ChipFactoryRoom> bringEverythingToTop() {
        record Step(ChipFactoryRoom current, Step previous, int depth) {
        }

        final Set<ChipFactoryRoom> seenStates = new HashSet<>();
        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step(this, null, 0));
        seenStates.add(this);

        while (!queue.isEmpty()) {
            final var step = queue.pop();
            final ChipFactoryRoom current = step.current();

            if (current.floors().get(Floor.FIRST).isEmpty() && current.floors().get(Floor.SECOND).isEmpty() && current.floors().get(Floor.THIRD).isEmpty()) {
                final List<ChipFactoryRoom> steps = Arrays.asList(new ChipFactoryRoom[step.depth() + 1]);
                var currentStep = step;
                do {
                    steps.set(currentStep.depth(), currentStep.current());
                    currentStep = currentStep.previous();
                }
                while (currentStep != null);
                return List.copyOf(steps);
            }

            for (final ChipFactoryRoom next : current.generateNextStates(true)) {
                if (seenStates.contains(next)) {
                    continue;
                }
                seenStates.add(next);
                queue.add(new Step(next, step, step.depth() + 1));
            }
        }

        throw new IllegalArgumentException("Unable to bring all components to top floor");
    }

    @Override
    public String toString() {
        return floors.entrySet().stream() //
                .map(entry -> (entry.getKey().ordinal() + 1) + " -> " + entry.getValue() + (elevatorPosition == entry.getKey() ? " E" : "")) //
                .collect(Collectors.joining("\n"));
    }

    public static ChipFactoryRoom parseInput(final BufferedReader in) throws IOException {
        try {
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

                        Collections.sort(components);
                        return Map.entry(floor, List.copyOf(components));
                    }) //
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, () -> new EnumMap<>(Floor.class)));

            return new ChipFactoryRoom(Collections.unmodifiableMap(floors), Floor.FIRST);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
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
    public record Component(@NonNull String element, @NonNull ComponentType type) implements Comparable<Component> {

        @Override
        public int compareTo(final Component other) {
            final var comparator = Comparator.comparing(Component::type).thenComparing(Component::element);
            return comparator.compare(this, other);
        }

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
