package com.mscg;

import com.mscg.utils.CollectionUtils;
import com.mscg.utils.bfs.BfsVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record VolcanoChambers(Map<String, Valve> namedValves)
{

	public static VolcanoChambers parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Map<String, Valve> namedValves = in.lines() //
					.map(Valve::from) //
					.collect(Collectors.toUnmodifiableMap(Valve::name, valve -> valve));
			return new VolcanoChambers(namedValves);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static Stream<Status> computeNextStatuses(final Map<String, List<Connection>> adjacencyMap, final Status status)
	{
		final var connections = adjacencyMap.getOrDefault(status.currentNode(), Collections.emptyList());
		return connections.stream() //
				.flatMap(conn -> {
					final String valveName = conn.targetValve().name();
					final int remainingTime = status.remainingTime() - conn.distance() - 1;
					if (remainingTime <= 0 || status.openedValves().contains(valveName)) {
						return Stream.empty();
					}
					final long releasedPressure = status.totalReleasedPressure() //
							+ conn.targetValve().flowRate() * (long) remainingTime;
					return Stream.of(new Status(remainingTime, releasedPressure, valveName,
							CollectionUtils.append(status.openedValves(), valveName)));
				});
	}

	private static long combineSolutionsAndFindMaxPressure(final long maxReleasedPressure, final Status status1,
			final Status status2)
	{
		final long combinedPressure = status1.totalReleasedPressure() + status2.totalReleasedPressure();
		if (maxReleasedPressure < combinedPressure && !overlap(status1, status2)) {
			return combinedPressure;
		}
		return maxReleasedPressure;
	}

	private static boolean overlap(final Status status1, final Status status2)
	{
		final Set<String> longestSet;
		final Set<String> shortestSet;
		if (status1.openedValves().size() <= status2.openedValves().size()) {
			longestSet = status2.openedValves();
			shortestSet = status1.openedValves();
		} else {
			longestSet = status1.openedValves();
			shortestSet = status2.openedValves();
		}
		for (final String valve : shortestSet) {
			if (longestSet.contains(valve)) {
				return true;
			}
		}
		return false;
	}

	public long findMostReleasablePressure()
	{
		final Map<String, List<Connection>> adjacencyMap = buildAdjacencyMap();

		final var visitor = BfsVisitor.<Status, Status, Status>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withVisitedNodeAccumulatorAllocator(MaxPressureAccumulator::new) //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Function.identity()) //
				.withSimpleAdjacentMapper(status -> computeNextStatuses(adjacencyMap, status), //
						Function.identity()) //
				.withoutIntermediateResultBuilder() //
				.withNextNodeMapper((currentNode, adjacent) -> Optional.of(adjacent)) //
				.build();
		final BfsVisitor.VisitResult<Status> result = visitor.visitFrom(new Status(30, 0, "AA", Set.of()));
		final Status maxPressureReleased = result.stream().findFirst().orElseThrow();
		return maxPressureReleased.totalReleasedPressure();
	}

	public long findMostReleasablePressure2()
	{
		final Map<String, List<Connection>> adjacencyMap = buildAdjacencyMap();

		final var visitor = BfsVisitor.<Status, Status, Status>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withDefaultVisitedNodeAccumulatorAllocator() //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Function.identity()) //
				.withSimpleAdjacentMapper(status -> computeNextStatuses(adjacencyMap, status), //
						Function.identity()) //
				.withoutIntermediateResultBuilder() //
				.withNextNodeMapper((currentNode, adjacent) -> Optional.of(adjacent)) //
				.build();
		final BfsVisitor.VisitResult<Status> result = visitor.visitFrom(new Status(26, 0, "AA", Set.of()));

		if (!(result instanceof BfsVisitor.VisitResult.MultiResults<Status>(List<Status> results))) {
			throw new IllegalStateException("Expecting a multi result");
		}

		long maxReleasedPressure = Long.MIN_VALUE;
		for (int i = 0, l = results.size(); i < l - 1; i++) {
			final Status status1 = results.get(i);
			for (int j = i + 1; j < l; j++) {
				final Status status2 = results.get(j);
				maxReleasedPressure = combineSolutionsAndFindMaxPressure(maxReleasedPressure, status1, status2);
			}
		}

		return maxReleasedPressure;
	}

	private Map<String, List<Connection>> buildAdjacencyMap()
	{
		final var adjacencyMap = new HashMap<String, List<Connection>>();

		for (final Valve valve : namedValves.values()) {
			final var visitor = BfsVisitor.<Connection, String, Connection>builder() //
					.withDefaultVisitedNodesAllocator() //
					.withDefaultVisitedNodeAccumulatorAllocator() //
					.withDefaultQueueAllocator() //
					.withNodeIdExtractor(conn -> conn.targetValve().name()) //
					.withSimpleAdjacentMapper( //
							conn -> conn.targetValve().connectedValves().stream() //
									.map(v -> new Connection(namedValves.get(v), conn.distance() + 1)), //
							conn -> conn.targetValve().name()) //
					.withoutIntermediateResultBuilder() //
					.withNextNodeMapper((currentNode, adjacent) -> Optional.of(adjacent)) //
					.build();
			final BfsVisitor.VisitResult<Connection> result = visitor.visitFrom(new Connection(valve, 0));
			if (!(result instanceof BfsVisitor.VisitResult.MultiResults<Connection> multiResults)) {
				throw new IllegalStateException("Can't compute connections for valve " + valve.name());
			}
			final List<Connection> connections = multiResults.results().stream() //
					.filter(conn -> conn.targetValve() != valve) //
					.filter(conn -> conn.targetValve().flowRate() != 0) //
					.toList();
			adjacencyMap.put(valve.name(), connections);
		}

		return Collections.unmodifiableMap(adjacencyMap);
	}

	private interface PressureReleaser
	{
		long totalReleasedPressure();
	}

	public record Valve(String name, int flowRate, List<String> connectedValves)
	{

		public static Valve from(final String line)
		{
			final var pattern = Pattern.compile("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)");
			final var matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid valve description");
			}
			final List<String> connectedValves = Arrays.stream(matcher.group(3).split(",")) //
					.map(String::trim) //
					.toList();
			return new Valve(matcher.group(1), Integer.parseInt(matcher.group(2)), connectedValves);
		}

	}

	private record Status(int remainingTime, long totalReleasedPressure, String currentNode, Set<String> openedValves)
			implements PressureReleaser {}

	private record Connection(Valve targetValve, int distance) {}

	private static class MaxPressureAccumulator<T extends PressureReleaser> implements BfsVisitor.VisitedNodeAccumulator<T>
	{
		private T maxPressure;

		@Override
		public boolean add(final T status)
		{
			if (maxPressure == null || status.totalReleasedPressure() > maxPressure.totalReleasedPressure()) {
				maxPressure = status;
			}
			return true;
		}

		@Override
		public Stream<T> stream()
		{
			return Optional.ofNullable(maxPressure).stream();
		}
	}

}
