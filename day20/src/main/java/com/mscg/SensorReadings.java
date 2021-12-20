package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public record SensorReadings(int[] mappings, int[][] readings, int rows, int cols)
{

	public static SensorReadings parseInput(final BufferedReader in) throws IOException
	{
		final int[] mappings = in.readLine().chars() //
				.map(SensorReadings::parseReading) //
				.toArray();

		final int[][] readings = in.lines() //
				.skip(1) // skip empty line
				.map(line -> line.chars() //
						.map(SensorReadings::parseReading) //
						.toArray()) //
				.toArray(int[][]::new);

		return new SensorReadings(mappings, readings, readings.length, readings[0].length);
	}

	private static int parseReading(final int c)
	{
		return switch ((char) c) {
			case '#' -> 1;
			case '.' -> 0;
			default -> throw new IllegalArgumentException("Unsupported char " + (char) c);
		};
	}

	public long evolveTwiceAndCountLitPixels()
	{
		final int[][] evolved = evolveTwice(readings, rows, cols);

		return Arrays.stream(evolved) //
				.flatMapToInt(Arrays::stream) //
				.sum();
	}

	public long evolveFiftyTimesAndCountLitPixels()
	{
		final int[][] evolved = Stream.iterate(readings, step -> evolveTwice(step, step.length, step[0].length)) //
				.skip(25) //
				.findFirst() //
				.orElseThrow();

		return Arrays.stream(evolved) //
				.flatMapToInt(Arrays::stream) //
				.sum();
	}

	private int[][] evolveTwice(final int[][] readings, final int rows, final int cols)
	{
		final int[][] step1 = evolve(readings, rows, cols, 0);
		final int[][] step2 = evolve(step1, step1.length, step1[0].length, mappings[0]);
		return step2;
	}

	private int[][] evolve(final int[][] readings, final int rows, final int cols, final int defaultReading)
	{
		final int[][] result = new int[rows + 2][cols + 2];

		for (int i = 0; i < rows + 2; i++) {
			for (int j = 0; j < cols + 2; j++) {
				int index = 0;
				for (int k1 = i - 1; k1 <= i + 1; k1++) {
					final int readingI = k1 - 1;
					for (int k2 = j - 1; k2 <= j + 1; k2++) {
						final int readingJ = k2 - 1;
						final int reading;
						if (readingI < 0 || readingI >= rows || readingJ < 0 || readingJ >= cols) {
							reading = defaultReading;
						} else {
							reading = readings[readingI][readingJ];
						}
						index = (index << 1) + reading;
					}
				}

				result[i][j] = mappings[index];
			}
		}

		return result;
	}
}
