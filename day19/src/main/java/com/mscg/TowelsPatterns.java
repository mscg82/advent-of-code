package com.mscg;

import com.mscg.utils.CollectionUtils;
import com.mscg.utils.StreamUtils;
import com.mscg.utils.bfs.BfsVisitor;
import com.mscg.utils.bfs.BfsVisitor.VisitResult;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record TowelsPatterns(List<String> availablePatterns, List<String> desiredPatterns)
{

	public static TowelsPatterns parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> blocks = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.toList();
			final List<String> availablePatterns = Arrays.stream(blocks.get(0).getFirst().split(",")) //
					.map(String::trim) //
					.sorted(Comparator.comparingInt(String::length)) //
					.toList();
			return new TowelsPatterns(availablePatterns, blocks.get(1));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int countPossiblePatterns()
	{
		record Node(String originalPattern, String desiredPattern, List<String> usedPatterns)
		{
			@Override
			public String toString()
			{
				return "(" + desiredPattern + ", " + usedPatterns + ")";
			}
		}

		final BfsVisitor<Node, String, Node> bfsVisitor = BfsVisitor.<Node, String, Node>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withoutVisitedNodeAccumulatorAllocator() //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Node::desiredPattern) //
				.withSimpleAdjacentMapper( //
						node -> availablePatterns.stream() //
								.filter(pattern -> node.desiredPattern().startsWith(pattern)) //
								.map(pattern -> new Node(node.originalPattern(), node.desiredPattern().substring(pattern.length()),
										CollectionUtils.append(node.usedPatterns(), pattern))), //
						Node::desiredPattern) //
				.withResultBuilder((node, _) -> node.desiredPattern().isEmpty() ? //
						new VisitResult.SingleResult<>(node) : //
						VisitResult.NotFound.notFound()) //
				.withNextNodeMapper((_, adj) -> Optional.of(adj)) //
				.build();

		int possiblePatterns = 0;
		for (final String desiredPattern : desiredPatterns) {
			final VisitResult<Node> result = bfsVisitor.visitFrom(new Node(desiredPattern, desiredPattern, List.of()),
					BfsVisitor.VisitMode.DFS);
			if (!(result instanceof VisitResult.NotFound)) {
				possiblePatterns++;
			}
		}
		return possiblePatterns;
	}

	public long countAllPossiblePatterns()
	{
		long patterns = 0L;
		for (final String desiredPattern : desiredPatterns) {
			final Object2LongOpenHashMap<String> cache = new Object2LongOpenHashMap<>();
			cache.defaultReturnValue(-1L);
			patterns += countCoveringSequences(desiredPattern, cache);
		}
		return patterns;
	}

	private long countCoveringSequences(final String sequence, final Object2LongMap<String> cache)
	{
		long sequences = 0L;
		for (final String availablePattern : availablePatterns) {
			if (sequence.startsWith(availablePattern)) {
				final String subsequence = sequence.substring(availablePattern.length());
				if (subsequence.isEmpty()) {
					sequences++;
				} else {
					@SuppressWarnings("java:S3824")
					long count = cache.getLong(subsequence);
					if (count < 0) {
						count = countCoveringSequences(subsequence, cache);
						cache.put(subsequence, count);
					}
					sequences += count;
				}
			}
		}

		return sequences;
	}

}
