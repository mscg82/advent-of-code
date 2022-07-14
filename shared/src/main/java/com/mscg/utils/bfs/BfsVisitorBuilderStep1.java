package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep1<NODE, NODE_ID, ADJACENT>
{

	default BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT> withoutVisitedNodesChecks()
	{
		return withVisitedNodesAllocator(NoVisitedNodeCheck::instance);
	}

	default BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT> withDefaultVisitedNodesAllocator()
	{
		return withVisitedNodesAllocatorSet(HashMap::new);
	}

	default BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT> withVisitedNodesAllocatorSet(
			@NonNull final Supplier<? extends Map<NODE_ID, BfsVisitor.NodeStatus>> visitedNodeSetAllocator)
	{
		return withVisitedNodesAllocator(() -> new VisitedNodeMapAdapter<>(visitedNodeSetAllocator.get()));
	}

	BfsVisitorBuilderStep2<NODE, NODE_ID, ADJACENT> withVisitedNodesAllocator(
			@NonNull Supplier<? extends BfsVisitor.VisitedNodeSet<NODE_ID>> visitedNodeAllocator);

}

@SuppressWarnings("java:S119")
record VisitedNodeMapAdapter<NODE_ID>(Map<NODE_ID, BfsVisitor.NodeStatus> map) implements BfsVisitor.VisitedNodeSet<NODE_ID>
{
	@Override
	public boolean mark(final NODE_ID node, final BfsVisitor.NodeStatus status)
	{
		if (status == BfsVisitor.NodeStatus.NOT_VISITED) {
			map.remove(node);
			return false;
		}

		map.put(node, status);
		return true;
	}

	@Override
	public boolean contains(final NODE_ID node)
	{
		return map.containsKey(node);
	}

	@Override
	public BfsVisitor.NodeStatus statusFor(final NODE_ID node)
	{
		return map.getOrDefault(node, BfsVisitor.NodeStatus.NOT_VISITED);
	}

	@Override
	public boolean filters()
	{
		return true;
	}
}

@SuppressWarnings("rawtypes")
enum NoVisitedNodeCheck implements BfsVisitor.VisitedNodeSet
{
	INSTANCE;

	@SuppressWarnings("unchecked")
	public static <E> BfsVisitor.VisitedNodeSet<E> instance()
	{
		return INSTANCE;
	}

	@Override
	public boolean mark(final Object o, final BfsVisitor.NodeStatus status)
	{
		return false;
	}

	@Override
	public boolean contains(final Object o)
	{
		return false;
	}

	@Override
	public BfsVisitor.NodeStatus statusFor(final Object o)
	{
		return BfsVisitor.NodeStatus.NOT_VISITED;
	}

	@Override
	public boolean filters()
	{
		return false;
	}

}
