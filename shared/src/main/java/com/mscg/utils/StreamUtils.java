package com.mscg.utils;

import com.mscg.utils.spliterators.PartitionedSpliterator;
import com.mscg.utils.spliterators.SplittingSpliterator;
import com.mscg.utils.spliterators.WindowedSpliterator;
import lombok.NonNull;

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtils
{
	public static <T> @NonNull Iterable<T> iterate(@NonNull final Stream<T> source)
	{
		return source::iterator;
	}

	public static <T> BinaryOperator<T> unsupportedMerger()
	{
		return (acc1, acc2) -> {
			throw new UnsupportedOperationException();
		};
	}

	public static <T> @NonNull Stream<Collection<T>> windowed(@NonNull final Collection<T> source, final int size)
	{
		return windowed(source.stream(), size);
	}

	public static <T> @NonNull Stream<Collection<T>> windowed(@NonNull final Stream<T> stream, final int size)
	{
		final var windower = new WindowedSpliterator<>(stream.spliterator(), size);
		return StreamSupport.stream(windower, false);
	}

	public static <T> @NonNull Stream<Collection<T>> partitioned(@NonNull final Collection<T> source, final int size)
	{
		return partitioned(source.stream(), size);
	}

	public static <T> @NonNull Stream<Collection<T>> partitioned(@NonNull final Stream<T> stream, final int size)
	{
		final Spliterator<Collection<T>> partitioner = new PartitionedSpliterator<>(stream.spliterator(), size);
		return StreamSupport.stream(partitioner, false);
	}

	public static <T> @NonNull Stream<Stream<T>> splitted(@NonNull final Collection<T> source,
			final Predicate<T> splittingConditions)
	{
		return splitted(source.stream(), splittingConditions);
	}

	public static <T> @NonNull Stream<Stream<T>> splitted(@NonNull final Stream<T> stream, final Predicate<T> splittingConditions)
	{
		final Spliterator<Stream<T>> splitter = new SplittingSpliterator<>(stream.spliterator(), splittingConditions);
		return StreamSupport.stream(splitter, false);
	}

	private StreamUtils()
	{
		throw new UnsupportedOperationException("StreamUtils constructor can't be called");
	}

}
