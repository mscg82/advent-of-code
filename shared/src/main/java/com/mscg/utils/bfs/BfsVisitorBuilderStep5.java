package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep5<NODE, NODE_ID, ADJACENT>
{

	BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT> withAdjacentMapper(
			@NonNull Function<? super NODE, ? extends Stream<ADJACENT>> adjacentMapper, //
			@NonNull Function<? super ADJACENT, ? extends NODE_ID> adjacentIdExtractor);

}
