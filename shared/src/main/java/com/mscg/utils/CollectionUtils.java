package com.mscg.utils;

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

	private CollectionUtils()
	{
		throw new UnsupportedOperationException("CollectionUtils constructor can't be called");
	}
}
