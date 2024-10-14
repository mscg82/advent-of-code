package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.mscg.utils.StreamUtils.unsupportedMerger;

public record CubeGame(List<Game> games)
{
	public static CubeGame parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var pattern = Pattern.compile("Game (\\d+):(.+)");
			final var setPattern = Pattern.compile("(\\d+) (red|blue|green)");
			final List<Game> games = in.lines() //
					.map(StreamUtils.matchOrFail(pattern, input -> "Unsupported game format " + input)) //
					.map(matcher -> {
						final int index = Integer.parseInt(matcher.group(1));
						final String set = matcher.group(2);
						final List<CubeSet> cubeSets = Arrays.stream(set.split(";")) //
								.map(String::trim) //
								.map(cubeSetStr -> Arrays.stream(cubeSetStr.split(",")) //
										.map(String::trim) //
										.map(StreamUtils.matchOrFail(setPattern, ball -> "Unsupported game set format " + ball)) //
										.reduce(new CubeSet(0, 0, 0), //
												(cubeSet, setMatcher) -> {
													final int value = Integer.parseInt(setMatcher.group(1));
													return switch (setMatcher.group(2)) {
														case "red" -> cubeSet.withReds(value);
														case "blue" -> cubeSet.withBlues(value);
														case "green" -> cubeSet.withGreens(value);
														default -> throw new IllegalArgumentException(
																"Unsupported ball color " + setMatcher.group(2));
													};
												}, //
												unsupportedMerger())) //
								.toList();
						return new Game(index, cubeSets);
					}) //
					.toList();
			return new CubeGame(games);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computePossibleGames()
	{
		final int maxReds = 12;
		final int maxGreens = 13;
		final int maxBlues = 14;
		return games.stream() //
				.filter(game -> game.cubeSets().stream().allMatch(
						cubeSet -> cubeSet.reds() <= maxReds && cubeSet.greens() <= maxGreens && cubeSet.blues() <= maxBlues)) //
				.mapToInt(Game::index) //
				.sum();
	}

	public long computeMinPowerSum()
	{
		return games.stream() //
				.map(game -> game.cubeSets().stream() //
						.reduce(new CubeSet(0, 0, 0), //
								(min, cubeSet) -> new CubeSet( //
										Math.max(min.reds(), cubeSet.reds()), //
										Math.max(min.blues(), cubeSet.blues()), //
										Math.max(min.greens(), cubeSet.greens())))) //
				.mapToLong(minSet -> (long) minSet.reds() * minSet.blues() * minSet.greens()) //
				.sum();
	}

	record Game(int index, List<CubeSet> cubeSets) {}

	@RecordBuilder
	record CubeSet(int reds, int blues, int greens) implements CubeGameCubeSetBuilder.With {}
}
