package com.mscg.utils.bfs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Deque;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("java:S119")
class BfsVisitorBuilder<NODE, NODE_ID, ADJACENT> implements //
		BfsVisitorBuilderStep1<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep4<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep5<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep7<NODE, NODE_ID, ADJACENT>, //
		BfsVisitorBuilderStep8<NODE, NODE_ID, ADJACENT>
{

	private @NonNull Supplier<? extends Deque<NODE>> queueAllocator;

	private @NonNull Supplier<? extends BfsVisitor.VisitedNodeSet<NODE_ID>> visitedNodesAllocator;

	private @NonNull Supplier<? extends BfsVisitor.VisitedNodeAccumulator<NODE>> nodeAccumulatorAllocator;

	private @NonNull Function<? super NODE, ? extends NODE_ID> idExtractor;

	private @NonNull BiFunction<? super NODE, BfsVisitor.VisitedNodeAccumulator<NODE>, ? extends Stream<ADJACENT>> adjacentMapper;

	private @NonNull Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor;

	private @NonNull BiFunction<? super NODE, Supplier<Stream<? extends ADJACENT>>, ? extends BfsVisitor.VisitResult<NODE>> resultBuilder;

	private @NonNull BiFunction<? super NODE, ? super ADJACENT, Optional<? extends NODE>> nextNodeMapper;

	@Override
	public BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT> withVisitedNodesAllocator(
			@NonNull final Supplier<? extends BfsVisitor.VisitedNodeSet<NODE_ID>> visitedNodeAllocator)
	{
		this.visitedNodesAllocator = visitedNodeAllocator;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT> withVisitedNodeAccumulatorAllocator(
			@NonNull final Supplier<? extends BfsVisitor.VisitedNodeAccumulator<NODE>> nodeAccumulatorAllocator)
	{
		this.nodeAccumulatorAllocator = nodeAccumulatorAllocator;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep4<NODE, NODE_ID, ADJACENT> withQueueAllocator(
			@NonNull final Supplier<? extends Deque<NODE>> queueAllocator)
	{
		this.queueAllocator = queueAllocator;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep5<NODE, NODE_ID, ADJACENT> withNodeIdExtractor(
			@NonNull final Function<? super NODE, ? extends NODE_ID> idExtractor)
	{
		this.idExtractor = idExtractor;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT> withAdjacentMapper(
			@NonNull final BiFunction<? super NODE, BfsVisitor.VisitedNodeAccumulator<NODE>, ? extends Stream<ADJACENT>> adjacentMapper,
			@NonNull final Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor)
	{
		this.adjacentMapper = adjacentMapper;
		this.adjacentIdExtractor = adjacentIdExtractor;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep7<NODE, NODE_ID, ADJACENT> withResultBuilder(
			@NonNull final BiFunction<? super NODE, Supplier<Stream<? extends ADJACENT>>, ? extends BfsVisitor.VisitResult<NODE>> resultBuilder)
	{
		this.resultBuilder = resultBuilder;
		return this;
	}

	@Override
	public BfsVisitorBuilderStep8<NODE, NODE_ID, ADJACENT> withNextNodeMapper(
			@NonNull final BiFunction<? super NODE, ? super ADJACENT, Optional<? extends NODE>> nextNodeMapper)
	{
		this.nextNodeMapper = nextNodeMapper;
		return this;
	}

	@Override
	public BfsVisitor<NODE, NODE_ID, ADJACENT> build()
	{
		return new BfsVisitor<>(queueAllocator, visitedNodesAllocator, nodeAccumulatorAllocator, idExtractor, adjacentMapper,
				adjacentIdExtractor, resultBuilder, nextNodeMapper);
	}
}

