package com.mscg;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record Drone(IntcodeV8 computer)
{
	public static Drone parseInput(final BufferedReader in) throws IOException
	{
		return new Drone(IntcodeV8.parseInput(in));
	}

	@SneakyThrows
	public String findPassword()
	{
		final long[] inputs;
		try (var program = new BufferedReader(
				new InputStreamReader(Objects.requireNonNull(AdventDay25.class.getResourceAsStream("/program.txt")),
						StandardCharsets.UTF_8))) {
			inputs = program.lines() //
					.map(l -> l + "\n") //
					.flatMapToInt(String::chars) //
					.mapToLong(v -> v) //
					.toArray();
		}

		var computer = this.computer.execute(IntcodeV8.InputGenerator.forArray(inputs));
		computer = computer.withHalted(false);

		final String output = printOutput(computer);
		final int index = output.lastIndexOf("Items here:");
		final var items = output.substring(index).lines() //
				.skip(1) //
				.takeWhile(s -> s.startsWith("- ")) //
				.map(s -> s.substring(2)) //
				.toList();

		final List<List<String>> itemsCombinations = LongStream.range(1, 256) //
				.mapToObj(v -> BitSet.valueOf(new long[] { v })) //
				.map(bits -> bits.stream().mapToObj(items::get).toList()) //
				.toList();

		for (final List<String> combination : itemsCombinations) {
			final long[] combinationInputs = Stream.concat( //
							combination.stream().map(s -> "take " + s + "\n"), //
							Stream.of("east\n")) //
					.flatMapToInt(String::chars) //
					.mapToLong(v -> v) //
					.toArray();
			final var testCombination = computer.execute(IntcodeV8.InputGenerator.forArray(combinationInputs));

			final String combinationOutput = printOutput(testCombination);

			if (!combinationOutput.contains("heavier") && !combinationOutput.contains("lighter")) {
				final var pattern = Pattern.compile("by typing ([^ ]+) ");
				final var matcher = pattern.matcher(combinationOutput);
				if (!matcher.find()) {
					return null;
				}
				return matcher.group(1);
			}
		}

		return null;
	}

	private static String printOutput(final IntcodeV8 computer)
	{
		return Arrays.stream(computer.outputs()) //
				.collect(StringBuilder::new, (str, v) -> str.append((char) v), (str1, str2) -> str1.append(str2.toString())) //
				.toString();
	}

}
