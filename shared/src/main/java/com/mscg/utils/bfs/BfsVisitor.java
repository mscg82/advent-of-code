package com.mscg.utils.bfs;

import com.mscg.utils.StreamUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("java:S119")
public class BfsVisitor<NODE, NODE_ID, ADJACENT>
{

	public static <NODE, NODE_ID, ADJACENT> BfsVisitorBuilderStep1<NODE, NODE_ID, ADJACENT> builder()
	{
		return new BfsVisitorBuilder<>();
	}

	private final @NonNull Supplier<? extends Deque<NODE>> queueAllocator;

	private final @NonNull Supplier<? extends BfsVisitor.VisitedNodeSet<NODE_ID>> visitedNodesAllocator;

	private final @NonNull Supplier<? extends BfsVisitor.VisitedNodeAccumulator<NODE>> nodeAccumulatorAllocator;

	private final @NonNull Function<? super NODE, ? extends NODE_ID> idExtractor;

	private final @NonNull BiFunction<? super NODE, BfsVisitor.VisitedNodeAccumulator<NODE>, ? extends Stream<ADJACENT>> adjacentMapper;

	private final @NonNull Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor;

	private final @NonNull BiFunction<? super NODE, Supplier<Stream<? extends ADJACENT>>, ? extends BfsVisitor.VisitResult<NODE>> resultBuilder;

	private final @NonNull BiFunction<? super NODE, ? super ADJACENT, Optional<? extends NODE>> nextNodeMapper;

	public VisitResult<NODE> visitFrom(final NODE initialNode)
	{
		return visitFrom(initialNode, VisitMode.BFS);
	}

	public VisitResult<NODE> visitFrom(final NODE initialNode, @NonNull final VisitMode visitMode)
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

			final Stream<ADJACENT> nonVisitedAdjacents = getNonVisitedAdjacents(visitedNodes, nodeAccumulator, currentNode);

			final CachableStreamSupplier<ADJACENT> cachedNonVisitedAdjacents = CachableStreamSupplier.wrap( //
					nonVisitedAdjacents);

			final var earlyResult = resultBuilder.apply(currentNode, cachedNonVisitedAdjacents);
			if (!(earlyResult instanceof VisitResult.NotFound)) {
				return earlyResult;
			}

			for (final var adjacent : StreamUtils.iterate(cachedNonVisitedAdjacents.checkAndGet())) {
				nextNodeMapper.apply(currentNode, adjacent) //
						.ifPresent(nextNode -> {
							if (visitMode == VisitMode.BFS) {
								queue.add(nextNode);
							} else {
								queue.addFirst(nextNode);
							}
							visitedNodes.mark(idExtractor.apply(nextNode), NodeStatus.QUEUED);
						});
			}
		}

		final List<NODE> results = List.copyOf(nodeAccumulator.asList());
		if (results.isEmpty()) {
			return VisitResult.NotFound.notFound();
		} else if (results.size() == 1) {
			return new VisitResult.SingleResult<>(results.get(0));
		} else {
			return new VisitResult.MultiResults<>(results);
		}
	}

	public <R> Function<NODE, R> cached(final Map<NODE, R> cache, final Function<VisitResult<NODE>, R> resultMapper)
	{
		final Function<NODE, VisitResult<NODE>> visitor = this::visitFrom;
		return initialNode -> cache.computeIfAbsent(initialNode, visitor.andThen(resultMapper));
	}

	private Stream<ADJACENT> getNonVisitedAdjacents(final VisitedNodeSet<NODE_ID> visitedNodes,
			final BfsVisitor.VisitedNodeAccumulator<NODE> nodeAccumulator, final NODE currentNode)
	{
		final Stream<ADJACENT> adjacents = adjacentMapper.apply(currentNode, nodeAccumulator);
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
		static <NODE> Supplier<VisitedNodeAccumulator<NODE>> accumulateIf(final Predicate<NODE> accumulationPredicate)
		{
			return () -> new VisitedNodeAccumulatorListAdapter<>(new ArrayList<>(), accumulationPredicate);
		}

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

	public enum VisitMode
	{
		BFS, DFS
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

@SuppressWarnings({ "java:S117", "java:S119" })
record VisitedNodeAccumulatorListAdapter<NODE>(List<NODE> visitedNodes, Predicate<NODE> accumulationPredicate)
		implements BfsVisitor.VisitedNodeAccumulator<NODE>
{
	public VisitedNodeAccumulatorListAdapter(final List<NODE> visitedNodes)
	{
		this(visitedNodes, __ -> true);
	}

	@Override
	public boolean add(final NODE node)
	{
		if (!accumulationPredicate.test(node)) {
			return false;
		}
		return visitedNodes.add(node);
	}

	@Override
	public Stream<NODE> stream()
	{
		return visitedNodes.stream();
	}

	@Override
	public List<NODE> asList()
	{
		return List.copyOf(visitedNodes);
	}

}
