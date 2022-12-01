package com.mscg.utils;

import java.util.List;
import java.util.stream.Stream;

public class CollectionUtils
{
	public static <T> List<T> append(final List<T> list, final T newValue)
	{
		return Stream.concat(list.stream(), Stream.of(newValue)) //
				.toList();
	}

	private CollectionUtils()
	{
		throw new UnsupportedOperationException("CollectionUtils constructor can't be called");
	}
}
