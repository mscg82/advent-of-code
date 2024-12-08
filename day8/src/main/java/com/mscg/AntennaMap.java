package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record AntennaMap(List<Antenna> antennas, int rows, int cols)
{

	public static AntennaMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int rows = allLines.size();
			final int cols = allLines.getFirst().length();
			final List<Antenna> antennas = Seq.seq(allLines).zipWithIndex() //
					.flatMap(idxRow -> {
						final int y = idxRow.v2().intValue();
						return Seq.seq(idxRow.v1().chars()).zipWithIndex() //
								.flatMap(idxChar -> {
									final int x = idxChar.v2().intValue();
									return switch ((char) idxChar.v1().intValue()) {
										case '.' -> Stream.of();
										case final char name -> Stream.of(new Antenna(name, new Position(x, y)));
									};
								});
					}) //
					.toList();
			return new AntennaMap(antennas, rows, cols);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int countAntiNodeLocations()
	{
		final var antiNodes = new HashSet<Position>();

		final Map<Character, List<Position>> nameToAntennas = antennas.stream() //
				.collect(Collectors.groupingBy(Antenna::name, Collectors.mapping(Antenna::position, Collectors.toList())));

		for (final List<Position> namedAntennas : nameToAntennas.values()) {
			final int l = namedAntennas.size();
			for (int i = 0; i < l; i++) {
				final var first = namedAntennas.get(i);
				for (int j = i + 1; j < l; j++) {
					final var second = namedAntennas.get(j);
					Stream.of( //
									new Position(2 * second.x() - first.x(), 2 * second.y() - first.y()), //
									new Position(2 * first.x() - second.x(), 2 * first.y() - second.y())) //
							.filter(pos -> pos.isValid(rows, cols)) //
							.forEach(antiNodes::add);
				}
			}
		}

		return antiNodes.size();
	}

	public int countAntiNodeLocationsWithHarmonics()
	{
		final var antiNodes = new HashSet<Position>();

		final Map<Character, List<Position>> nameToAntennas = antennas.stream() //
				.collect(Collectors.groupingBy(Antenna::name, Collectors.mapping(Antenna::position, Collectors.toList())));

		for (final List<Position> namedAntennas : nameToAntennas.values()) {
			final int l = namedAntennas.size();
			for (int i = 0; i < l; i++) {
				final var first = namedAntennas.get(i);
				for (int j = i + 1; j < l; j++) {
					final var second = namedAntennas.get(j);
					// positive harmonics
					generateHarmonics(first, second, 0, k -> k + 1) //
							.forEach(antiNodes::add);

					// negative harmonics
					generateHarmonics(first, second, -1, k -> k - 1) //
							.forEach(antiNodes::add);
				}
			}
		}

		return antiNodes.size();
	}

	private Stream<Position> generateHarmonics(final Position first, final Position second, final int k,
			final IntUnaryOperator frequencyChanger)
	{
		return IntStream.iterate(k, frequencyChanger) //
				.mapToObj(f -> new Position(f * second.x() - (f - 1) * first.x(), f * second.y() - (f - 1) * first.y())) //
				.takeWhile(pos -> pos.isValid(rows, cols));
	}

	public record Antenna(char name, Position position) {}

	public record Position(int x, int y)
	{

		public boolean isValid(final int row, final int col)
		{
			return x >= 0 && x < col && y >= 0 && y < row;
		}

	}

}
