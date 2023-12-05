package com.mscg.utils.spliterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class PartitionedSpliterator<T> implements Spliterator<Collection<T>>
{
	private final List<T> partition;

	private final Spliterator<T> source;

	private final int size;

	public PartitionedSpliterator(final Spliterator<T> source, final int size)
	{
		this.size = size;
		this.partition = new ArrayList<>(size);
		this.source = source;
	}

	@Override
	public boolean tryAdvance(final Consumer<? super Collection<T>> action)
	{
		if (source.tryAdvance(partition::add)) {
			if (partition.size() == size) {
				flushPartition(action);
			}
			return true;
		}
		if (!partition.isEmpty()) {
			flushPartition(action);
		}
		return false;
	}

	@Override
	public Spliterator<Collection<T>> trySplit()
	{
		return null;
	}

	@Override
	public long estimateSize()
	{
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics()
	{
		return 0;
	}

	private void flushPartition(final Consumer<? super Collection<T>> action)
	{
		action.accept(Collections.unmodifiableCollection(partition));
		partition.clear();
	}
}
