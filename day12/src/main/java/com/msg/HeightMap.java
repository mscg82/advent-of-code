package com.msg;

import com.mscg.utils.StreamUtils;
import com.mscg.utils.bfs.BfsVisitor;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@RecordBuilder
public record HeightMap(List<String> heights, int rows, int cols, Position start, Position end)
{

	public static HeightMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final HeightMapBuilder heightMapBuilder = Seq.zipWithIndex(in.lines()) //
					.reduce(HeightMapBuilder.builder().heights(new ArrayList<>()), //
							(builder, idxLine) -> {
								final int row = idxLine.v2().intValue();
								final String line = idxLine.v1();
								final int startIndex = line.indexOf('S');
								final int endIndex = line.indexOf('E');
								if (startIndex >= 0) {
									builder.start(new Position(startIndex, row));
								}
								if (endIndex >= 0) {
									builder.end(new Position(endIndex, row));
								}
								builder.heights().add(line.replace('S', 'a').replace('E', 'z'));
								return builder;
							}, //
							StreamUtils.unsupportedMerger());

			return heightMapBuilder.build();
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public HeightMap
	{
		Objects.requireNonNull(start, "Start cannot be null");
		Objects.requireNonNull(end, "End cannot be null");
		rows = heights.size();
		cols = heights.get(0).length();
		heights = List.copyOf(heights);
	}

	public long computeLengthOfShortestPathToExit()
	{
		final List<Position> path = visitFrom(start);

		return path.size() - 1L;
	}

	public long computeShortestLengthFromAllLowestElevations()
	{
		final List<Position> lowestPositions = new ArrayList<>();
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				final char elevation = heights.get(y).charAt(x);
				if (elevation == 'a') {
					lowestPositions.add(new Position(x, y));
				}
			}
		}

		final List<Position> shortestPath = lowestPositions.stream() //
				.parallel() //
				.map(this::visitFrom) //
				.filter(not(List::isEmpty)) //
				.min(Comparator.comparingInt(List::size)) //
				.orElseThrow();

		return shortestPath.size() - 1L;
	}

	private List<Position> visitFrom(final Position start)
	{
		final var adjacencyMap = buildAdjacencyMap();

		final var parents = new HashMap<Position, Position>();

		final var bfsVisitor = BfsVisitor.<Position, String, Position>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withoutVisitedNodeAccumulatorAllocator() //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Position::toString) //
				.withAdjacentMapper( //
						pos -> adjacencyMap.get(pos).stream(), //
						Position::toString) //
				.withResultBuilder((pos, adjacents) -> {
					if (pos.equals(end)) {
						final Deque<Position> path = new ArrayDeque<>();
						path.add(end);
						Position parent = parents.get(end);
						while (parent != null) {
							path.addFirst(parent);
							parent = parents.get(parent);
						}

						return new BfsVisitor.VisitResult.MultiResults<>(List.copyOf(path));
					} else {
						adjacents.get().forEach(adj -> parents.put(adj, pos));
						return BfsVisitor.VisitResult.NotFound.notFound();
					}
				}) //
				.withNextNodeMapper((__, pos) -> Optional.of(pos)) //
				.build();

		final BfsVisitor.VisitResult<Position> visitResult = bfsVisitor.visitFrom(start);
		return visitResult.stream().toList();
	}

	private Map<Position, List<Position>> buildAdjacencyMap()
	{
		final var adjacencyMap = new HashMap<Position, List<Position>>();

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				final var current = new Position(x, y);
				final char currentHeight = heights.get(current.y()).charAt(current.x());
				final List<Position> adjacents = current.neighbours(rows, cols) //
						.filter(pos -> {
							final char height = heights.get(pos.y()).charAt(pos.x());
							return height <= currentHeight + 1;
						}) //
						.toList();
				adjacencyMap.put(current, adjacents);
			}
		}

		return Collections.unmodifiableMap(adjacencyMap);
	}

	@RecordBuilder
	record Position(int x, int y) implements HeightMapPositionBuilder.With
	{

		public Stream<Position> neighbours(final int rows, final int cols)
		{
			return Stream.of( //
							this.withY(y - 1), //
							this.withX(x + 1), //
							this.withY(y + 1), //
							this.withX(x - 1)) //
					.filter(pos -> pos.x >= 0 && pos.x < cols && pos.y >= 0 && pos.y < rows);
		}

		@Override
		public String toString()
		{
			return "(" + x + "," + y + ")";
		}
	}

}
