package com.mscg.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class InputUtils
{

	public static BufferedReader readInput(final InputStream source)
	{
		return new BufferedReader(new InputStreamReader(Objects.requireNonNull(source), StandardCharsets.UTF_8));
	}

	public static BufferedReader readInput(final String source)
	{
		return new BufferedReader(new StringReader(Objects.requireNonNull(source)));
	}

	private InputUtils()
	{
		throw new UnsupportedOperationException("InputUtils constructor can't be called");
	}
}
