package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.mscg.utils.bfs.BfsVisitor.VisitResult.NotFound;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep6<NODE, NODE_ID, ADJACENT>
{

	default BfsVisitorBuilderStep7<NODE, NODE_ID, ADJACENT> withoutIntermediateResultBuilder()
	{
		return withResultBuilder((node, adjacents) -> NotFound.notFound());
	}

	BfsVisitorBuilderStep7<NODE, NODE_ID, ADJACENT> withResultBuilder(
			@NonNull BiFunction<? super NODE, Supplier<Stream<? extends ADJACENT>>, ? extends BfsVisitor.VisitResult<NODE>> resultBuilder);

}
