package com.msg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record CaveMap(Map<String, List<String>> adjacencyMap)
{

	private static final String START_CAVE = "start";

	private static final String END_CAVE = "end";

	public static CaveMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Map<String, List<String>> adjacencyMap = in.lines() //
					.flatMap(line -> {
						final String[] parts = line.split("-");
						return Stream.of(Map.entry(parts[0], parts[1]), Map.entry(parts[1], parts[0]));
					}) //
					.collect(Collectors.groupingBy(Map.Entry::getKey, //
							Collectors.mapping(Map.Entry::getValue, Collectors.toUnmodifiableList())));
			return new CaveMap(Map.copyOf(adjacencyMap));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public List<List<String>> findAllPaths()
	{
		final List<List<String>> paths = new ArrayList<>();

		final Deque<List<String>> queue = new LinkedList<>();
		queue.add(List.of(START_CAVE));

		while (!queue.isEmpty()) {
			final List<String> path = queue.pop();
			final Set<String> nodesInPath = new HashSet<>(path);
			final String curCave = path.get(path.size() - 1);
			final List<String> adjacentCaves = adjacencyMap.get(curCave);
			adjacentCaves.stream() //
					.filter(cave -> Character.isUpperCase(cave.charAt(0)) || !nodesInPath.contains(cave)) //
					.forEach(cave -> {
						final List<String> newPath = Stream.concat(path.stream(), Stream.of(cave)).toList();
						if (END_CAVE.equals(cave)) {
							paths.add(newPath);
						} else {
							queue.addFirst(newPath);
						}
					});
		}

		return List.copyOf(paths);
	}

	public List<List<String>> findAllPaths2()
	{
		final List<List<String>> paths = new ArrayList<>();

		final Deque<List<String>> queue = new LinkedList<>();
		queue.add(List.of(START_CAVE));

		while (!queue.isEmpty()) {
			final List<String> path = queue.pop();
			final Map<String, Long> smallCavesToFrequency = path.stream() //
					.filter(cave -> Character.isLowerCase(cave.charAt(0))) //
					.collect(Collectors.groupingBy(cave -> cave, Collectors.counting()));
			final boolean allOnes = smallCavesToFrequency.values().stream().allMatch(v -> v == 1L);

			final String curCave = path.get(path.size() - 1);
			final List<String> adjacentCaves = adjacencyMap.get(curCave);
			adjacentCaves.stream() //
					.filter(cave -> !START_CAVE.equals(cave)) //
					.filter(cave -> {
						if (Character.isUpperCase(cave.charAt(0)) || allOnes) {
							return true;
						}
						return !smallCavesToFrequency.containsKey(cave);
					}) //
					.forEach(cave -> {
						final List<String> newPath = Stream.concat(path.stream(), Stream.of(cave)).toList();
						if (END_CAVE.equals(cave)) {
							paths.add(newPath);
						} else {
							queue.addFirst(newPath);
						}
					});
		}

		return List.copyOf(paths);
	}
}
