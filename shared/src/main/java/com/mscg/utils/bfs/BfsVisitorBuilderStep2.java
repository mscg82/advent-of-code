package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT>
{

	default BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT> withoutVisitedNodeAccumulatorAllocator()
	{
		return withVisitedNodeAccumulatorAllocator(NoVisitedNodeAccumulator::instance);
	}

	default BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT> withDefaultVisitedNodeAccumulatorAllocator()
	{
		return withVisitedNodeAccumulatorAllocatorList(ArrayList::new);
	}

	default BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT> withVisitedNodeAccumulatorAllocatorList(
			@NonNull final Supplier<? extends List<NODE>> nodeAccumulatorAllocatorList)
	{
		return withVisitedNodeAccumulatorAllocator(
				() -> new VisitedNodeAccumulatorListAdapter<>(nodeAccumulatorAllocatorList.get()));
	}

	BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT> withVisitedNodeAccumulatorAllocator(
			@NonNull Supplier<? extends BfsVisitor.VisitedNodeAccumulator<NODE>> nodeAccumulatorAllocator);

}

@SuppressWarnings("rawtypes")
enum NoVisitedNodeAccumulator implements BfsVisitor.VisitedNodeAccumulator
{
	INSTANCE;

	@SuppressWarnings("unchecked")
	public static <E> BfsVisitor.VisitedNodeAccumulator<E> instance()
	{
		return INSTANCE;
	}

	@Override
	public boolean add(final Object o)
	{
		return false;
	}

	@Override
	public Stream stream()
	{
		return Stream.empty();
	}

}
