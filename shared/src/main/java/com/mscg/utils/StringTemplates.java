package com.mscg.utils;

import java.util.function.Function;

public class StringTemplates
{

	public static final StringTemplate.Processor<IllegalArgumentException, RuntimeException> ILLEGAL_ARGUMENT_EXC = //
			ofException(IllegalArgumentException::new);

	public static final StringTemplate.Processor<UnsupportedOperationException, RuntimeException> UNSUPPORTED_OP_EXC = //
			ofException(UnsupportedOperationException::new);

	public static <T extends Exception> StringTemplate.Processor<T, RuntimeException> ofException(
			final Function<String, T> exceptionAllocator)
	{
		return StringTemplate.Processor.of(template -> exceptionAllocator.apply(STR.process(template)));
	}

	private StringTemplates()
	{
		throw new UnsupportedOperationException("Constructor of class StringTemplates cannot be called");
	}
}
