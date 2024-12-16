package com.mscg.utils;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Position8Bits(int x, int y) implements Position8BitsBuilder.With
{

	// Custom equals and hashCode are needed to optimize the performances
	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof final Position8Bits other)) {
			return false;
		}
		return hashCode() == other.hashCode();
	}

	@Override
	public int hashCode()
	{
		if (x < 0 || x >= 256) {
			throw new IllegalArgumentException("x out of range: " + x);
		}
		if (y < 0 || y >= 256) {
			throw new IllegalArgumentException("y out of range: " + y);
		}
		return y << 8 | x;
	}

	public boolean isValid(final int rows, final int cols)
	{
		return x >= 0 && x < rows && y >= 0 && y < cols;
	}

}
