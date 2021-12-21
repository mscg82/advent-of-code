package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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

	public long quantumPlay()
	{
		long p1Wins = 0;
		long p2Wins = 0;

		final Deque<State> queue = new ArrayDeque<>(2_000_000);
		queue.add(new State(player1Start, player2Start, 0, 0, 1, true));
		while (!queue.isEmpty()) {
			final var curState = queue.pop();
			for (final var outcome : DiceOutcome.ALL) {
				int p1Pos;
				final int p1Sum;
				int p2Pos;
				final int p2Sum;
				if (curState.p1Turn()) {
					p2Pos = curState.p2Pos();
					p2Sum = curState.p2Sum();
					p1Pos = curState.p1Pos() + outcome.sum();
					if (p1Pos >= 10) {
						p1Pos -= 10;
					}
					p1Sum = curState.p1Sum() + p1Pos + 1;
				} else {
					p1Pos = curState.p1Pos();
					p1Sum = curState.p1Sum();
					p2Pos = curState.p2Pos() + outcome.sum();
					if (p2Pos >= 10) {
						p2Pos -= 10;
					}
					p2Sum = curState.p2Sum() + p2Pos + 1;
				}
				if (p1Sum >= 21) {
					p1Wins += curState.frequency() * outcome.frequency();
				} else if (p2Sum >= 21) {
					p2Wins += curState.frequency() * outcome.frequency();
				} else {
					queue.add(
							new State(p1Pos, p2Pos, p1Sum, p2Sum, curState.frequency() * outcome.frequency(), !curState.p1Turn()));
				}
			}
		}
		return Math.max(p1Wins, p2Wins);
	}

	private record State(int p1Pos, int p2Pos, int p1Sum, int p2Sum, long frequency, boolean p1Turn) {}

	private record DiceOutcome(int sum, int frequency)
	{
		public static final List<DiceOutcome> ALL = List.of( //
				new DiceOutcome(3, 1), //
				new DiceOutcome(4, 3), //
				new DiceOutcome(5, 6), //
				new DiceOutcome(6, 7), //
				new DiceOutcome(7, 6), //
				new DiceOutcome(8, 3), //
				new DiceOutcome(9, 1) //
		);
	}

}
