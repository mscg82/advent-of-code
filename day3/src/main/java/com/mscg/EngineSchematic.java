package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record EngineSchematic(List<PartNumber> partNumbers, List<Part> parts)
{
	@SuppressWarnings("java:S127")
	public static EngineSchematic parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var partNumbers = new ArrayList<PartNumber>();
			final var parts = new ArrayList<Part>();
			final List<String> allLines = in.lines().toList();
			for (int y = 0, l = allLines.size(); y < l; y++) {
				final String line = allLines.get(y);
				for (int x = 0, ll = line.length(); x < ll; x++) {
					final var start = new Position(x, y);
					final char c = line.charAt(x);
					if (Character.isDigit(c)) {
						// we found a number
						final int xs = x;
						while (x < ll && Character.isDigit(line.charAt(x))) {
							// scan ahead until we find the end of the number
							x++;
						}
						final int number = Integer.parseInt(line.substring(xs, x));
						// go back to the last character with a digit
						x--;
						final var end = new Position(x, y);
						partNumbers.add(new PartNumber(number, new Box(start, end)));
					} else if (c != '.') {
						// we found a part
						parts.add(new Part(c, start));
					}
				}
			}

			return new EngineSchematic(List.copyOf(partNumbers), List.copyOf(parts));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumPartNumbers()
	{
		return partNumbers.stream() //
				.filter(partNumber -> {
					final var expandedBox = partNumber.box().expand();
					return parts.stream() //
							.map(Part::position) //
							.anyMatch(expandedBox::contains);
				}) //
				.mapToInt(PartNumber::number) //
				.sum();
	}

	public long sumGearRatios()
	{
		final Map<Box, PartNumber> boxToPartNumber = partNumbers.stream() //
				.collect(Collectors.toMap(partNumber -> partNumber.box().expand(), Function.identity()));
		return parts.stream() //
				.filter(part -> part.type() == '*') //
				.map(part -> boxToPartNumber.entrySet().stream() //
						.filter(entry -> entry.getKey().contains(part.position())) //
						.map(Map.Entry::getValue) //
						.toList()) //
				.filter(partNumbers -> partNumbers.size() == 2) //
				.mapToLong(partNumbers -> (long) partNumbers.get(0).number() * partNumbers.get(1).number()) //
				.sum();
	}

	@RecordBuilder
	record Position(int x, int y) implements EngineSchematicPositionBuilder.With {}

	record Box(Position upLeft, Position downRight)
	{

		public Box expand()
		{
			return new Box( //
					upLeft.with(pos -> {
						pos.x(pos.x() - 1);
						pos.y(pos.y() - 1);
					}), //
					downRight.with(pos -> {
						pos.x(pos.x() + 1);
						pos.y(pos.y() + 1);
					}));
		}

		public boolean contains(final Position p)
		{
			return upLeft.x() <= p.x() && downRight.x() >= p.x() && //
					upLeft.y() <= p.y() && downRight.y() >= p.y();
		}

	}

	record PartNumber(int number, Box box) {}

	record Part(char type, Position position) {}

}
