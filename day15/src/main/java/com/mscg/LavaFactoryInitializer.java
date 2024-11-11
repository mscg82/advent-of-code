package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public record LavaFactoryInitializer(List<String> instructions)
{
	public static LavaFactoryInitializer parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> instructions = in.lines() //
					.flatMap(line -> Arrays.stream(line.split(","))) //
					.map(String::trim) //
					.filter(not(String::isBlank)) //
					.toList();

			return new LavaFactoryInitializer(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public static int hash(final String value)
	{
		return value.chars() //
				.reduce(0, (hash, c) -> ((hash + c) * 17) % 256);
	}

	public long sumHashes()
	{
		return instructions.stream() //
				.mapToLong(LavaFactoryInitializer::hash) //
				.sum();
	}

	public long computeFocusingPower()
	{
		final List<Instructions> parsedInstructions = instructions.stream() //
				.map(Instructions::from) //
				.toList();

		final int totalBoxes = 256;
		final var boxes = new ArrayList<LinkedHashMap<String, Integer>>(totalBoxes);
		for (int i = 0; i < totalBoxes; i++) {
			boxes.add(new LinkedHashMap<>());
		}

		for (final Instructions instruction : parsedInstructions) {
			final var box = boxes.get(instruction.hash());
			switch (instruction.action()) {
				case REMOVE -> box.remove(instruction.label());
				case ADD -> box.put(instruction.label(), instruction.focalLength());
			}
		}

		long focusingPower = 0L;
		for (int i = 0; i < totalBoxes; i++) {
			final var box = boxes.get(i);
			final int[] focalLengths = box.values().stream() //
					.mapToInt(Integer::intValue) //
					.toArray();
			for (int j = 0; j < focalLengths.length; j++) {
				focusingPower += (i + 1) * (j + 1) * (long) focalLengths[j];
			}
		}
		return focusingPower;
	}

	private record Instructions(int hash, String label, Action action, int focalLength)
	{
		public static Instructions from(final String line)
		{
			final var pattern = Pattern.compile("^([a-z]+)([=-])(\\d*)$");
			final var matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid instruction: " + line);
			}
			final String label = matcher.group(1);
			final Action action = Action.from(matcher.group(2).charAt(0));
			final int focalLength = switch (action) {
				case ADD -> Integer.parseInt(matcher.group(3));
				case REMOVE -> -1;
			};
			return new Instructions(LavaFactoryInitializer.hash(label), label, action, focalLength);
		}
	}

	private enum Action
	{
		ADD, REMOVE;

		public static Action from(final char c)
		{
			return switch (c) {
				case '=' -> ADD;
				case '-' -> REMOVE;
				default -> throw new IllegalArgumentException("Unsupported operation type '" + c + "'");
			};
		}
	}
}
