package com.mscg.utils;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils
{
	@SafeVarargs
	public static <T> List<T> append(final List<T> list, final T newValue, final T... newValues)
	{
		return Stream.concat(Stream.concat(list.stream(), Stream.of(newValue)), Arrays.stream(newValues)) //
				.toList();
	}

	@SafeVarargs
	@SuppressWarnings("FuseStreamOperations")
	public static <T> Set<T> append(final Set<T> set, final T newValue, final T... newValues)
	{
		return Collections.unmodifiableSet( //
				Stream.concat(Stream.concat(set.stream(), Stream.of(newValue)), Arrays.stream(newValues)) //
						.collect(Collectors.toSet()));
	}

	@SuppressWarnings("java:S1319")
	public static <E extends Enum<E>, V> EnumMap<E, V> enumMap(final Map<E, V> source, final Class<E> enumClass)
	{
		if (source == null || source.isEmpty()) {
			return new EnumMap<>(enumClass);
		}

		return new EnumMap<>(source);
	}

	public static <T> List<T> tail(@NonNull final List<T> list)
	{
		return list.subList(1, list.size());
	}

	public static <T> List<T> replaceHead(@NonNull final T newHead, @NonNull final List<T> list)
	{
		return Stream.concat(Stream.of(newHead), tail(list).stream()).toList();
	}

	public static IntList tail(@NonNull final IntList list)
	{
		return list.subList(1, list.size());
	}

	private CollectionUtils()
	{
		throw new UnsupportedOperationException("CollectionUtils constructor can't be called");
	}
}
