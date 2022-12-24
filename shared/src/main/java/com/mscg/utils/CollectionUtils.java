package com.mscg.utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils
{
	public static <T> List<T> append(final List<T> list, final T newValue)
	{
		return Stream.concat(list.stream(), Stream.of(newValue)) //
				.toList();
	}

	@SuppressWarnings("FuseStreamOperations")
	public static <T> Set<T> append(final Set<T> set, final T newValue)
	{
		return Collections.unmodifiableSet(Stream.concat(set.stream(), Stream.of(newValue)) //
				.collect(Collectors.toSet()));
	}

	private CollectionUtils()
	{
		throw new UnsupportedOperationException("CollectionUtils constructor can't be called");
	}
}
