package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record SafeDial(List<Instruction> instructions)
{

	public static SafeDial parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.map(Instruction::from) //
					.toList();

			return new SafeDial(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findPassword()
	{
		long zeroCrossing = 0;
		long position = 50;
		final long maxPositions = 100;
		for (final Instruction instruction : instructions) {
			final long delta = instruction.direction() == Direction.LEFT ? -instruction.amount() : instruction.amount();
			position = Math.floorMod(position + delta, maxPositions);

			if (position == 0) {
				zeroCrossing++;
			}
		}
		return zeroCrossing;
	}

	public long findAdvancedPassword()
	{
		long zeroCrossing = 0;
		long position = 50;
		final long maxPositions = 100;
		for (final Instruction instruction : instructions) {
			final long delta = instruction.direction() == Direction.LEFT ? -instruction.amount() : instruction.amount();
			final long newPosition = position + delta;

			if (position == 0) {
				if (instruction.amount() >= maxPositions) {
					zeroCrossing += Math.floorDiv(instruction.amount(), maxPositions);
				}
			} else {
				if (newPosition >= maxPositions) {
					final long remainder = delta - (maxPositions - position);
					zeroCrossing += 1 + Math.floorDiv(remainder, maxPositions);
				}
				if (newPosition <= 0) {
					final long remainder = -delta - position;
					zeroCrossing += 1 + Math.floorDiv(remainder, maxPositions);
				}
			}

			position = Math.floorMod(newPosition, maxPositions);

		}
		return zeroCrossing;
	}

	public record Instruction(Direction direction, long amount)
	{
		public static Instruction from(final String line)
		{
			return new Instruction(Direction.from(line.charAt(0)), Long.parseLong(line.substring(1)));
		}
	}

	public enum Direction
	{
		LEFT, RIGHT;

		public static Direction from(final char c)
		{
			return switch (c) {
				case 'L', 'l' -> LEFT;
				case 'R', 'r' -> RIGHT;
				default -> throw new IllegalArgumentException("Unsupported direction '" + c + "'");
			};
		}
	}

}
