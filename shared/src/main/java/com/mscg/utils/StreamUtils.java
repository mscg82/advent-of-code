package com.mscg.utils;

import com.mscg.utils.spliterators.PartitionedSpliterator;
import com.mscg.utils.spliterators.SplittingSpliterator;
import com.mscg.utils.spliterators.WindowedSpliterator;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

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
		final Spliterator<Stream<T>> splitter = new SplittingSpliterator<>(stream.spliterator(), false, splittingConditions);
		return StreamSupport.stream(splitter, false);
	}

	public static <T> @NonNull Stream<Stream<T>> splittedIncluding(@NonNull final Collection<T> source,
			final Predicate<T> splittingConditions)
	{
		return splittedIncluding(source.stream(), splittingConditions);
	}

	public static <T> @NonNull Stream<Stream<T>> splittedIncluding(@NonNull final Stream<T> stream,
			final Predicate<T> splittingConditions)
	{
		final Spliterator<Stream<T>> splitter = new SplittingSpliterator<>(stream.spliterator(), true, splittingConditions);
		return StreamSupport.stream(splitter, false);
	}

	public static <T> @NonNull Collector<T, ?, List<List<T>>> splitAt(final Predicate<T> splittingConditions)
	{
		class Accumulator
		{
			final List<List<T>> parts = new ArrayList<>();

			List<T> current = new ArrayList<>();
		}

		return Collector.of(Accumulator::new, //
				(acc, obj) -> {
					if (splittingConditions.test(obj)) {
						if (!acc.current.isEmpty()) {
							acc.parts.add(acc.current);
						}
						acc.current = new ArrayList<>();
					}
					acc.current.add(obj);
				}, //
				unsupportedMerger(), //
				acc -> {
					if (!acc.current.isEmpty()) {
						acc.parts.add(acc.current);
					}
					return acc.parts.stream() //
							.map(List::copyOf) //
							.toList();
				});
	}

	public static <N extends Number> Collector<N, BitSet, BitSet> toBitSet()
	{
		return Collector.of(BitSet::new, //
				(bitset, number) -> bitset.set(number.intValue()), //
				(bitset1, bitset2) -> {
					bitset1.or(bitset2);
					return bitset1;
				}, //
				CONCURRENT, UNORDERED, IDENTITY_FINISH);
	}

	public static <T> Collector<T, ? extends Set<T>, Set<T>> toUnmodifiableHashSet()
	{
		return toUnmodifiableSet(HashSet::new);
	}

	public static <T> Collector<T, ? extends Set<T>, Set<T>> toUnmodifiableLinkedHashSet()
	{
		return toUnmodifiableSet(LinkedHashSet::new);
	}

	public static <T> Collector<T, ? extends Set<T>, Set<T>> toUnmodifiableSet(final Supplier<? extends Set<T>> setAllocator)
	{
		return Collector.of(setAllocator, //
				Set::add, //
				(set1, set2) -> {
					set1.addAll(set2);
					return set1;
				}, //
				Collections::unmodifiableSet, //
				CONCURRENT, UNORDERED);
	}

	public static <K, V> BiFunction<? super K, ? super V, ? extends V> mapValuesMerger(final V newValue,
			final Function<? super V, ? extends V> defaultValueMapper, final BiConsumer<? super V, ? super V> merger)
	{
		return (key, oldValue) -> {
			if (oldValue == null) {
				return defaultValueMapper.apply(newValue);
			}
			merger.accept(oldValue, newValue);
			return oldValue;
		};
	}

	private StreamUtils()
	{
		throw new UnsupportedOperationException("StreamUtils constructor can't be called");
	}

}
