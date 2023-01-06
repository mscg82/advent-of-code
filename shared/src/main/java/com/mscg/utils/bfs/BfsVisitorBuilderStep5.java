package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep5<NODE, NODE_ID, ADJACENT>
{

	default BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT> withSimpleAdjacentMapper(
			@NonNull final Function<? super NODE, ? extends Stream<ADJACENT>> adjacentMapper, //
			@NonNull final Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor)
	{
		return withAdjacentMapper((node, accumulator) -> adjacentMapper.apply(node), adjacentIdExtractor);
	}

	BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT> withAdjacentMapper(
			@NonNull BiFunction<? super NODE, BfsVisitor.VisitedNodeAccumulator<NODE>, ? extends Stream<ADJACENT>> adjacentMapper,
			@NonNull Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor);

}
