package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

public record JumpDroid(IntcodeV6 computer)
{
	public static JumpDroid parseInput(final BufferedReader in) throws IOException
	{
		return new JumpDroid(IntcodeV6.parseInput(in));
	}

	public long computeDamage()
	{
		final var programStr = """
				OR A J
				AND B J
				AND C J
				NOT J J
				AND D J
				WALK
				""";
		return runProgram(programStr, false);
	}

	public long runAndComputeDamage()
	{
		final var programStr = """
				OR E J
				OR F T
				OR I T
				AND T J
				OR H J
				AND D J
				NOT A T
				NOT T T
				AND B T
				AND C T
				NOT T T
				AND T J
				RUN
				""";
		return runProgram(programStr, false);
	}

	@SuppressWarnings("SameParameterValue")
	private long runProgram(final String programStr, final boolean print)
	{
		final var program = programStr.chars().mapToLong(v -> v).toArray();
		final var run = computer.execute(IntcodeV6.InputGenerator.forArray(program));
		final var outputs = run.outputs();

		if (print) {
			printOutput(outputs);
		}

		return outputs[outputs.length - 1];
	}

	@SuppressWarnings({ "unused", "java:S106" })
	private void printOutput(final long[] outputs)
	{
		for (final long output : outputs) {
			if (output <= Character.MAX_VALUE) {
				System.out.print((char) output);
			}
		}
		System.out.println();
	}
}
