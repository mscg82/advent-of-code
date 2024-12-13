package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record ArcadeRoom(List<Arcade> arcades)
{

	public static ArcadeRoom parseInput(final BufferedReader in) throws IOException
	{
		final var buttonPattern = Pattern.compile("Button ([AB]): X([+-]\\d+), Y([+-]\\d+)");
		final var prizePattern = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
		try {
			final List<List<String>> blocks = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.toList();
			final List<Arcade> arcades = blocks.stream() //
					.map(block -> {
						Button buttonA = null;
						Button buttonB = null;
						for (int i = 0; i < 2; i++) {
							final var matcher = buttonPattern.matcher(block.get(i));
							if (!matcher.matches()) {
								throw new IllegalArgumentException("Invalid arcade block " + block);
							}
							final var button = new Button(Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)));
							if ("A".equals(matcher.group(1))) {
								buttonA = button;
							} else {
								buttonB = button;
							}
						}
						if (buttonA == null || buttonB == null) {
							throw new IllegalArgumentException("Missing buttons in arcade block " + block);
						}

						final var matcher = prizePattern.matcher(block.get(2));
						if (!matcher.matches()) {
							throw new IllegalArgumentException("Invalid arcade block " + block);
						}
						final long prizeX = Long.parseLong(matcher.group(1));
						final long prizeY = Long.parseLong(matcher.group(2));

						return new Arcade(buttonA, buttonB, prizeX, prizeY);
					}) //
					.toList();
			return new ArcadeRoom(arcades);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findMinTokens()
	{
		return findMinTokens(arcades, 100);
	}

	public long findMinTokensFixed()
	{
		final List<Arcade> fixedArcades = arcades.stream() //
				.map(arcade -> arcade.with(a -> a.prizeX(a.prizeX() + 10_000_000_000_000L) //
						.prizeY(a.prizeY() + 10_000_000_000_000L))) //
				.toList();
		return findMinTokens(fixedArcades, Long.MAX_VALUE);
	}

	private static long findMinTokens(final List<Arcade> arcades, final long maxPresses)
	{
		long tokens = 0;
		for (final Arcade arcade : arcades) {
			final long d = arcade.buttonA().dx() * arcade.buttonB().dy() - arcade.buttonA().dy() * arcade.buttonB().dx();
			if (d == 0) {
				continue;
			}
			final long num1 = arcade.buttonB().dy() * arcade.prizeX() - arcade.buttonB().dx() * arcade.prizeY();
			final long num2 = arcade.buttonA().dx() * arcade.prizeY() - arcade.buttonA().dy() * arcade.prizeX();
			if (num1 % d != 0 || num2 % d != 0) {
				continue;
			}

			final long ta = num1 / d;
			final long tb = num2 / d;

			if (ta > maxPresses || tb > maxPresses) {
				continue;
			}

			tokens += ta * 3 + tb;
		}
		return tokens;
	}

	public record Button(long dx, long dy) {}

	@RecordBuilder
	public record Arcade(Button buttonA, Button buttonB, long prizeX, long prizeY) implements ArcadeRoomArcadeBuilder.With {}
}
