package com.mscg.utils.spliterators;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

public class WindowedSpliterator<T> implements Spliterator<Collection<T>>
{
	private final Deque<T> window;

	private final Spliterator<T> source;

	private final int size;

	private boolean windowConsumed;

	public WindowedSpliterator(final Spliterator<T> source, final int size)
	{
		this.window = new ArrayDeque<>(size);
		this.source = source;
		this.size = size;
		this.windowConsumed = false;
	}

	@Override
	public boolean tryAdvance(final Consumer<? super Collection<T>> action)
	{
		if (source.tryAdvance(window::addLast)) {
			if (window.size() == size) {
				windowConsumed = true;
				action.accept(Collections.unmodifiableCollection(window));
				window.removeFirst();
			}
			return true;
		}

		if (!windowConsumed) {
			action.accept(Collections.unmodifiableCollection(window));
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
}
