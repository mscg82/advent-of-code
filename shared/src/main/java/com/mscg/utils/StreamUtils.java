package com.mscg.utils;

import lombok.NonNull;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

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

	private StreamUtils()
	{
		throw new UnsupportedOperationException("StreamUtils constructor can't be called");
	}
}
