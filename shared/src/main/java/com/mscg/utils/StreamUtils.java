package com.mscg.utils;

import lombok.NonNull;

import java.util.stream.Stream;

public final class StreamUtils
{
	public static <T> @NonNull Iterable<T> iterate(@NonNull final Stream<T> source)
	{
		return source::iterator;
	}

	private StreamUtils()
	{
		throw new UnsupportedOperationException("StreamUtils constructor can't be called");
	}
}
