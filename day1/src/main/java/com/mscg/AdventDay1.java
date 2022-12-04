package com.mscg;

import com.mscg.utils.InputUtils;
import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

public class AdventDay1
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final long[] result = computeCarriedCalories(in);

			System.out.println("Part 1 - Answer %d".formatted(Arrays.stream(result) //
					.max() //
					.orElseThrow()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final long[] result = computeCarriedCalories(in);
			final LongUnaryOperator invertSign = v -> -v;
			System.out.println("Part 2 - Answer %d".formatted(Arrays.stream(result) //
					.map(invertSign) // since we can only sort in ascending order, we need to reverse sign of each value
					.sorted() //
					.limit(3) //
					.map(invertSign) //
					.sum()));
		}
	}

	private static long[] computeCarriedCalories(final BufferedReader in)
	{
		return StreamUtils.splitted(in.lines(), String::isBlank) //
				.map(block -> block.mapToLong(Long::parseLong)) //
				.mapToLong(LongStream::sum) //
				.toArray();
	}

	private static BufferedReader readInput()
	{
		return InputUtils.readInput(AdventDay1.class.getResourceAsStream("/input.txt"));
	}

	@RecordBuilder
	record Accumulator(List<Long> calories, long currentCalories) implements AdventDay1AccumulatorBuilder.With {}

}
