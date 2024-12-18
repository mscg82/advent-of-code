package com.mscg;

import com.mscg.utils.Position8Bits;
import com.mscg.utils.StreamUtils;
import com.mscg.utils.bfs.BfsVisitor;
import com.mscg.utils.bfs.BfsVisitor.VisitResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record RamRun(List<Position8Bits> corruptedBytesPositions)
{
	public static RamRun parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Position8Bits> corruptedBytesPositions = in.lines() //
					.map(line -> {
						final var parts = line.split(",");
						if (parts.length != 2) {
							throw new IllegalArgumentException("Invalid line format: " + line);
						}
						return new Position8Bits(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
					}) //
					.toList();
			return new RamRun(corruptedBytesPositions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int findMinPathToExit(final int maxCorruptedBytes, final int maxX, final int maxY)
	{
		final var pathToExit = visit(maxCorruptedBytes, maxX, maxY) //
				.orElseThrow(() -> new IllegalStateException("Unable to find path to exit"));

		return pathToExit.size();
	}

	public Position8Bits findFirstBlockingByte(final int maxX, final int maxY)
	{
		int start = 1;
		int end = corruptedBytesPositions.size();
		while (start < end - 1) {
			final int middle = (start + end) / 2;
			final var result = visit(middle, maxX, maxY);
			if (result.isEmpty()) {
				end = middle;
			} else {
				start = middle;
			}
		}
		return corruptedBytesPositions.get(start);
	}

	private Optional<List<Position8Bits>> visit(final int maxCorruptedBytes, final int maxX, final int maxY)
	{
		final Set<Position8Bits> corruptedBytesToUse = Set.copyOf(
				new HashSet<>(corruptedBytesPositions.subList(0, maxCorruptedBytes)));
		final var endPosition = new Position8Bits(maxX, maxY);
		final var startPosition = new Position8Bits(0, 0);

		record Node(Position8Bits position, Node parent)
		{
			@Override
			public String toString()
			{
				return "(" + position + ") <- (" + (parent == null ? "" : parent.position()) + ")";
			}
		}

		final var bfsVisitor = BfsVisitor.<Node, Position8Bits, Position8Bits>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withoutVisitedNodeAccumulatorAllocator() //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Node::position) //
				.withSimpleAdjacentMapper( //
						current -> Stream.of(current) //
								.map(Node::position) //
								.flatMap(currPos -> Stream.of( //
												currPos.withY(currPos.y() - 1), //
												currPos.withX(currPos.x() + 1), //
												currPos.withY(currPos.y() + 1), //
												currPos.withX(currPos.x() - 1)) //
										.filter(pos -> pos.isValid(maxY + 1, maxX + 1)) //
										.filter(not(corruptedBytesToUse::contains))), //
						Function.identity()) //
				.withResultBuilder((curr, _) -> endPosition.equals(curr.position()) ? //
						new VisitResult.SingleResult<>(curr) : VisitResult.NotFound.notFound()) //
				.withNextNodeMapper((curr, adj) -> Optional.of(new Node(adj, curr))) //
				.build();

		final VisitResult<Node> nodeVisitResult = bfsVisitor.visitFrom(new Node(startPosition, null));

		return nodeVisitResult.stream().findAny() //
				.map(lastNode -> Stream.iterate(lastNode, curr -> curr.parent() != null, Node::parent) //
						.map(Node::position) //
						.collect(StreamUtils.toReversedList()));
	}
}
