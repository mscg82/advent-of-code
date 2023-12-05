package com.mscg.utils;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class ConversionUtils
{

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	public static OptionalInt parseInt(final String value)
	{
		return switch (value) {
			case null -> OptionalInt.empty();

			default -> {
				try {
					yield OptionalInt.of(Integer.parseInt(value));
				} catch (final NumberFormatException e) {
					yield OptionalInt.empty();
				}
			}
		};
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	public static OptionalLong parseLong(final String value)
	{
		return switch (value) {
			case null -> OptionalLong.empty();

			default -> {
				try {
					yield OptionalLong.of(Long.parseLong(value));
				} catch (final NumberFormatException e) {
					yield OptionalLong.empty();
				}
			}
		};
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	public static OptionalDouble parseDouble(final String value)
	{
		return switch (value) {
			case null -> OptionalDouble.empty();

			default -> {
				try {
					yield OptionalDouble.of(Double.parseDouble(value));
				} catch (final NumberFormatException e) {
					yield OptionalDouble.empty();
				}
			}
		};
	}

	private ConversionUtils()
	{
		throw new UnsupportedOperationException("ConversionUtils constructor can't be called");
	}

}
