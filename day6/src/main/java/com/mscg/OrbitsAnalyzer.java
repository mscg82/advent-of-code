package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record OrbitsAnalyzer(Map<String, List<String>> centerToOrbitingPlanets) {

    public static OrbitsAnalyzer parseInput(final BufferedReader in) throws IOException {
        try {
            final Map<String, List<String>> centerToOrbitingPlanets = in.lines() //
                    .map(line -> line.split("\\)")) //
                    .collect(Collectors.groupingBy(parts -> parts[0], Collectors.mapping(parts -> parts[1], Collectors.toList())));

            return new OrbitsAnalyzer(Map.copyOf(centerToOrbitingPlanets));
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public long countTransfers() {
        final Map<String, List<String>> adjacencyMap = new HashMap<>();
        centerToOrbitingPlanets.forEach((center, planets) -> {
            for (final String planet : planets) {
                adjacencyMap.computeIfAbsent(center, __ -> new ArrayList<>()).add(planet);
                adjacencyMap.computeIfAbsent(planet, __ -> new ArrayList<>()).add(center);
            }
        });

        final String startPlanet = adjacencyMap.get("YOU").get(0);
        final String endPlanet = adjacencyMap.get("SAN").get(0);

        record Move(String planet, long hops) {

        }

        final Set<String> visitedPlanets = new HashSet<>();
        final Deque<Move> queue = new LinkedList<>();
        queue.add(new Move(startPlanet, 0L));

        while (!queue.isEmpty()) {
            final var move = queue.pop();
            if (move.planet().equals(endPlanet)) {
                return move.hops();
            }

            adjacencyMap.get(move.planet()).stream() //
                    .filter(planet -> !visitedPlanets.contains(planet)) //
                    .forEach(planet -> {
                        visitedPlanets.add(planet);
                        queue.add(new Move(planet, move.hops() + 1));
                    });
        }

        throw new IllegalStateException("Can't move from YOU to SAN");
    }

    public long countOrbits() {
        long orbits = 0;

        record PlanetWithOrbits(String name, long orbits) {

        }

        final Deque<PlanetWithOrbits> queue = new LinkedList<>();
        queue.add(new PlanetWithOrbits("COM", 0L));

        while (!queue.isEmpty()) {
            final var planet = queue.pop();
            for (final String subPlanet : centerToOrbitingPlanets.getOrDefault(planet.name(), Collections.emptyList())) {
                final PlanetWithOrbits planetWithOrbits = new PlanetWithOrbits(subPlanet, planet.orbits() + 1);
                orbits += planetWithOrbits.orbits();
                queue.add(planetWithOrbits);
            }
        }

        return orbits;
    }

}
