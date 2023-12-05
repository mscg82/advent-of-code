package com.mscg.utils.spliterators;

import lombok.Getter;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SplittingSpliterator<T> implements Spliterator<Stream<T>>
{
	private final Spliterator<T> source;

	private final Predicate<T> splittingCondition;

	private final boolean includeSplittedItem;

	private SplittedBlockSpliterator<T> splittedBlock;

	public SplittingSpliterator(final Spliterator<T> source, final boolean includeSplittedItem,
			final Predicate<T> splittingCondition)
	{
		this.source = source;
		this.includeSplittedItem = includeSplittedItem;
		this.splittingCondition = splittingCondition;
	}

	@Override
	public boolean tryAdvance(final Consumer<? super Stream<T>> action)
	{
		if (splittedBlock == null || !splittedBlock.isSourceFinished()) {
			consumeBlock();
			if (splittedBlock != null && splittedBlock.isSourceFinished()) {
				return false;
			}

			splittedBlock = new SplittedBlockSpliterator<>(source, includeSplittedItem, splittingCondition);
			action.accept(StreamSupport.stream(splittedBlock, false));
			return true;
		}

		return false;
	}

	@Override
	public Spliterator<Stream<T>> trySplit()
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

	private void consumeBlock()
	{
		if (splittedBlock != null && !splittedBlock.isDone()) {
			while (!splittedBlock.isDone()) {
				splittedBlock.tryAdvance(ignored -> {
				});
			}
		}
	}

	private static final class SplittedBlockSpliterator<T> implements Spliterator<T>
	{
		private final Spliterator<T> source;

		private final boolean includeSplittedItem;

		private final Predicate<T> splittingCondition;

		@Getter
		private boolean sourceFinished;

		@Getter
		private boolean done;

		private SplittedBlockSpliterator(final Spliterator<T> source, final boolean includeSplittedItem,
				final Predicate<T> splittingCondition)
		{
			this.source = source;
			this.includeSplittedItem = includeSplittedItem;
			this.splittingCondition = splittingCondition;
			this.sourceFinished = false;
			this.done = false;
		}

		@Override
		public boolean tryAdvance(final Consumer<? super T> action)
		{
			@SuppressWarnings("unchecked")
			final T[] newItemWrapper = (T[]) new Object[1];
			if (source.tryAdvance(item -> newItemWrapper[0] = item)) {
				final T newItem = newItemWrapper[0];
				if (splittingCondition.test(newItem)) {
					done = true;
					if (includeSplittedItem) {
						action.accept(newItem);
					}
					return false;
				} else {
					action.accept(newItem);
					return true;
				}
			}
			sourceFinished = true;
			done = true;
			return false;
		}

		@Override
		public Spliterator<T> trySplit()
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
}
