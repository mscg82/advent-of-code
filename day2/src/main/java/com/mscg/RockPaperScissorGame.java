package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record RockPaperScissorGame(List<Round> rounds)
{
	public static RockPaperScissorGame parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Round> rounds = in.lines() //
					.map(line -> line.split(" ")) //
					.map(parts -> new Round( //
							Choice.parseForOpponent(parts[0].charAt(0)), //
							Choice.parseForPlayer(parts[1].charAt(0)))) //
					.toList();
			return new RockPaperScissorGame(rounds);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeScore()
	{
		return rounds.stream() //
				.mapToLong(Round::score) //
				.sum();
	}

	public long computeScore2()
	{
		return rounds.stream() //
				.map(round -> new Round2(round.opponent(), Outcome.fromChoice(round.player()))) //
				.mapToLong(Round2::score) //
				.sum();
	}

	record Round(Choice opponent, Choice player)
	{
		public long score()
		{
			final Outcome outcome = switch (player) {
				case ROCK -> switch (opponent) {
					case ROCK -> Outcome.DRAW;
					case PAPER -> Outcome.LOSE;
					case SCISSOR -> Outcome.WIN;
				};
				case PAPER -> switch (opponent) {
					case ROCK -> Outcome.WIN;
					case PAPER -> Outcome.DRAW;
					case SCISSOR -> Outcome.LOSE;
				};
				case SCISSOR -> switch (opponent) {
					case ROCK -> Outcome.LOSE;
					case PAPER -> Outcome.WIN;
					case SCISSOR -> Outcome.DRAW;
				};
			};
			return outcome.score() + player.score();
		}
	}

	record Round2(Choice opponent, Outcome outcome)
	{
		public long score()
		{
			final Choice player = switch (outcome) {
				case LOSE -> switch (opponent) {
					case ROCK -> Choice.SCISSOR;
					case PAPER -> Choice.ROCK;
					case SCISSOR -> Choice.PAPER;
				};
				case DRAW -> opponent;
				case WIN -> switch (opponent) {
					case ROCK -> Choice.PAPER;
					case PAPER -> Choice.SCISSOR;
					case SCISSOR -> Choice.ROCK;
				};
			};
			return outcome.score() + player.score();
		}
	}

	enum Choice
	{
		ROCK, PAPER, SCISSOR;

		public static Choice parseForOpponent(final char c)
		{
			return switch (c) {
				case 'A' -> ROCK;
				case 'B' -> PAPER;
				case 'C' -> SCISSOR;
				default -> throw new IllegalArgumentException("Unsupported result " + c);
			};
		}

		public static Choice parseForPlayer(final char c)
		{
			return switch (c) {
				case 'X' -> ROCK;
				case 'Y' -> PAPER;
				case 'Z' -> SCISSOR;
				default -> throw new IllegalArgumentException("Unsupported result " + c);
			};
		}

		public long score()
		{
			return switch (this) {
				case ROCK -> 1;
				case PAPER -> 2;
				case SCISSOR -> 3;
			};
		}
	}

	enum Outcome
	{
		LOSE, DRAW, WIN;

		public static Outcome fromChoice(final Choice choice)
		{
			return switch (choice) {
				case ROCK -> LOSE;
				case PAPER -> DRAW;
				case SCISSOR -> WIN;
			};
		}

		public long score()
		{
			return switch (this) {
				case LOSE -> 0;
				case DRAW -> 3;
				case WIN -> 6;
			};
		}
	}

}
