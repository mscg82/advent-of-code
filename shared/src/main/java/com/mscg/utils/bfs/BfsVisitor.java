package com.mscg.utils.bfs;

import com.mscg.utils.StreamUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("java:S119")
public class BfsVisitor<NODE, NODE_ID, ADJACENT>
{

	private final @NonNull Supplier<? extends Deque<NODE>> queueAllocator;

	private final @NonNull Supplier<? extends BfsVisitor.VisitedNodeSet<NODE_ID>> visitedNodesAllocator;

	private final @NonNull Supplier<? extends BfsVisitor.VisitedNodeAccumulator<NODE>> nodeAccumulatorAllocator;

	private final @NonNull Function<? super NODE, ? extends NODE_ID> idExtractor;

	private final @NonNull Function<? super NODE, ? extends Stream<ADJACENT>> adjacentMapper;

	private final @NonNull Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor;

	private final @NonNull BiFunction<? super NODE, Supplier<Stream<? extends ADJACENT>>, ? extends BfsVisitor.VisitResult<NODE>> resultBuilder;

	private final @NonNull BiFunction<? super NODE, ? super ADJACENT, Optional<? extends NODE>> nextNodeMapper;

	public static <NODE, NODE_ID, ADJACENT> BfsVisitorBuilderStep1<NODE, NODE_ID, ADJACENT> builder()
	{
		return new BfsVisitorBuilder<>();
	}

	public VisitResult<NODE> visitFrom(final NODE initialNode)
	{
		final BfsVisitor.VisitedNodeAccumulator<NODE> nodeAccumulator = nodeAccumulatorAllocator.get();
		final BfsVisitor.VisitedNodeSet<NODE_ID> visitedNodes = visitedNodesAllocator.get();
		final Deque<NODE> queue = queueAllocator.get();

		queue.add(initialNode);
		visitedNodes.mark(idExtractor.apply(initialNode), NodeStatus.QUEUED);

		while (!queue.isEmpty()) {
			final var currentNode = queue.pop();
			visitedNodes.mark(idExtractor.apply(currentNode), NodeStatus.VISITED);
			nodeAccumulator.add(currentNode);

			final Stream<ADJACENT> nonVisitedAdjacents = getNonVisitedAdjacents(visitedNodes, currentNode);

			final CachableStreamSupplier<ADJACENT> cachedNonVisitedAdjacents = CachableStreamSupplier.wrap( //
					nonVisitedAdjacents);

			final var earlyResult = resultBuilder.apply(currentNode, cachedNonVisitedAdjacents);
			if (!(earlyResult instanceof VisitResult.NotFound)) {
				return earlyResult;
			}

			for (final var adjacent : StreamUtils.iterate(cachedNonVisitedAdjacents.checkAndGet())) {
				nextNodeMapper.apply(currentNode, adjacent) //
						.ifPresent(nextNode -> {
							queue.add(nextNode);
							visitedNodes.mark(idExtractor.apply(nextNode), NodeStatus.QUEUED);
						});
			}
		}

		final List<NODE> results = List.copyOf(nodeAccumulator.asList());
		if (results.isEmpty()) {
			return VisitResult.NotFound.notFound();
		} else {
			return new VisitResult.MultiResults<>(results);
		}
	}

	private Stream<ADJACENT> getNonVisitedAdjacents(final VisitedNodeSet<NODE_ID> visitedNodes, final NODE currentNode)
	{
		final Stream<ADJACENT> adjacents = adjacentMapper.apply(currentNode);
		final Stream<ADJACENT> nonVisitedAdjacents;
		if (visitedNodes.filters()) {
			nonVisitedAdjacents = adjacents //
					.filter(adjacent -> !visitedNodes.contains(adjacentIdExtractor.apply(adjacent)));
		} else {
			nonVisitedAdjacents = adjacents;
		}
		return nonVisitedAdjacents;
	}

	public sealed interface VisitResult<NODE> permits VisitResult.SingleResult, VisitResult.MultiResults, VisitResult.NotFound
	{

		@SuppressWarnings("unused")
		default Stream<NODE> stream()
		{
			return switch (this) {
				case SingleResult<NODE> sr -> Stream.of(sr.result);
				case MultiResults<NODE> mr -> mr.results.stream();
				case NotFound nf -> Stream.empty();
			};
		}

		record SingleResult<NODE>(NODE result) implements VisitResult<NODE> {}

		record MultiResults<NODE>(List<NODE> results) implements VisitResult<NODE> {}

		@SuppressWarnings("rawtypes")
		enum NotFound implements VisitResult
		{
			NOT_FOUND;

			@SuppressWarnings("unchecked")
			public static <NODE> VisitResult<NODE> notFound()
			{
				return NOT_FOUND;
			}
		}
	}

	public interface VisitedNodeSet<NODE_ID>
	{
		boolean mark(NODE_ID node, NodeStatus status);

		boolean contains(NODE_ID node);

		NodeStatus statusFor(NODE_ID node);

		default boolean filters()
		{
			return true;
		}
	}

	public interface VisitedNodeAccumulator<NODE>
	{
		boolean add(NODE node);

		Stream<NODE> stream();

		default List<NODE> asList()
		{
			return stream().toList();
		}
	}

	public enum NodeStatus
	{
		NOT_VISITED, QUEUED, VISITED
	}
}

@RequiredArgsConstructor(staticName = "wrap")
class CachableStreamSupplier<E> implements Supplier<Stream<? extends E>>
{
	private final Stream<? extends E> data;

	private List<? extends E> cache;

	@Override
	public Stream<? extends E> get()
	{
		if (cache == null) {
			cache = data.toList();
		}
		return cache.stream();
	}

	@SuppressWarnings("java:S1452")
	public Stream<? extends E> checkAndGet()
	{
		return cache != null ? cache.stream() : data;
	}
}
