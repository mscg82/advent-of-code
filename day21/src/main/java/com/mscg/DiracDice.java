package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public record DiracDice(int player1Start, int player2Start)
{

	public static DiracDice parseInput(final BufferedReader in) throws IOException
	{
		try {
			final int[] startPositions = in.lines() //
					.mapToInt(line -> {
						final int index = line.lastIndexOf(':');
						return Integer.parseInt(line.substring(index + 1).trim()) - 1;
					}) //
					.toArray();
			return new DiracDice(startPositions[0], startPositions[1]);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long testPlay()
	{
		int p1Pos = player1Start;
		long p1Score = 0;
		int p2Pos = player2Start;
		long p2Score = 0;
		long i = 1;
		while (p1Score < 1000 && p2Score < 1000) {
			final long value = 3 * i + 3;
			if ((i & 1) != 0) {
				// odd value -> player 1
				p1Pos = (int) ((p1Pos + value) % 10);
				p1Score += p1Pos + 1;
			} else {
				// even value -> player 2
				p2Pos = (int) ((p2Pos + value) % 10);
				p2Score += p2Pos + 1;
			}

			i += 3;
		}

		if (p1Score >= 1000) {
			return p2Score * (i - 1);
		} else {
			return p1Score * (i - 1);
		}
	}

}
