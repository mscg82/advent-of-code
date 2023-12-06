package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record BoatGame(List<TargetGame> targetGames)
{
	public static BoatGame parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final String timeLine = allLines.get(0).substring(allLines.get(0).indexOf(':') + 1);
			final String distanceLine = allLines.get(1).substring(allLines.get(1).indexOf(':') + 1);
			final Stream<Long> times = Arrays.stream(timeLine.split(" ")) //
					.map(String::trim) //
					.filter(not(String::isBlank)) //
					.map(Long::parseLong);
			final Stream<Long> distances = Arrays.stream(distanceLine.split(" ")) //
					.map(String::trim) //
					.filter(not(String::isBlank)) //
					.map(Long::parseLong);
			final List<TargetGame> targetGames = Seq.zip(times, distances) //
					.map(pair -> new TargetGame(pair.v1(), pair.v2())) //
					.toList();
			return new BoatGame(targetGames);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeWinningCombinations()
	{
		return computeWinningCombinationsInternal(targetGames);
	}

	public long computeWinningCombinationsInOneGame()
	{
		final String timeStr = targetGames.stream() //
				.map(targetGame -> Long.toString(targetGame.time())) //
				.collect(Collectors.joining());
		final String distanceStr = targetGames.stream() //
				.map(targetGame -> Long.toString(targetGame.distance())) //
				.collect(Collectors.joining());
		final var realTargetGame = new TargetGame(Long.parseLong(timeStr), Long.parseLong(distanceStr));
		return computeWinningCombinationsInternal(List.of(realTargetGame));
	}

	private long computeWinningCombinationsInternal(final List<TargetGame> targetGames)
	{
		return targetGames.stream() //
				.mapToLong(targetGame -> {
					final double d = Math.sqrt(targetGame.time() * targetGame.time() - 4.0 * targetGame.distance());
					final double vMin = (targetGame.time() - d) / 2.0;
					final double vMax = (targetGame.time() + d) / 2.0;
					final long winningVMin = (long) Math.ceil(vMin) + ((long) Math.ceil(vMin) == vMin ? 1 : 0);
					final long winningVMax = (long) Math.floor(vMax) - ((long) Math.floor(vMax) == vMax ? 1 : 0);
					return winningVMax - winningVMin + 1;
				}) //
				.boxed() //
				.reduce(1L, (acc, v) -> acc * v);
	}

	record TargetGame(long time, long distance) {}

}
