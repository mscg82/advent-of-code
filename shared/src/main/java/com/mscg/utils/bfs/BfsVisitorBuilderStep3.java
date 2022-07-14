package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep3<NODE, NODE_ID, ADJACENT>
{

	default BfsVisitorBuilderStep4<NODE, NODE_ID, ADJACENT> withDefaultQueueAllocator()
	{
		return withQueueAllocator(ArrayDeque::new);
	}

	BfsVisitorBuilderStep4<NODE, NODE_ID, ADJACENT> withQueueAllocator(@NonNull Supplier<? extends Deque<NODE>> queueAllocator);

}
