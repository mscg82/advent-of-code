package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record SkyMap(List<List<CellType>> skyCells)
{

	public static SkyMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<CellType>> skyCells = in.lines() //
					.map(line -> line.codePoints() //
							.mapToObj(cp -> CellType.from((char) cp)) //
							.toList()) //
					.toList();

			return new SkyMap(skyCells);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumDistancesBetweenGalaxies()
	{
		final List<List<CellType>> expandedUniverse = expandUniverse();
		final List<Position> galaxies = Seq.zipWithIndex(expandedUniverse) //
				.flatMap(lineY -> {
					final int y = lineY.v2().intValue();
					return Seq.zipWithIndex(lineY.v1()) //
							.filter(cellX -> CellType.GALAXY.equals(cellX.v1)) //
							.map(cellX -> new Position(cellX.v2().intValue(), y));
				}) //
				.toList();
		long sum = 0L;
		for (int i = 0; i < galaxies.size(); i++) {
			final var firstGalaxy = galaxies.get(i);
			for (int j = i + 1; j < galaxies.size(); j++) {
				final var secondGalaxy = galaxies.get(j);
				sum += Math.abs(firstGalaxy.x() - secondGalaxy.x()) + Math.abs(firstGalaxy.y() - secondGalaxy.y());
			}
		}
		return sum;
	}

	private List<List<CellType>> expandUniverse()
	{
		// expand the rows
		final List<List<CellType>> withExpandedRows = skyCells.stream() //
				.flatMap(line -> {
					if (line.stream().allMatch(CellType.EMPTY::equals)) {
						return Stream.of(line, line);
					} else {
						return Stream.of(line);
					}
				}) //
				.toList();

		final int rows = withExpandedRows.size();
		final int columns = withExpandedRows.getFirst().size();
		
		// expand the columns
		final List<List<CellType>> expandedUniverse = IntStream.range(0, rows) //
				.mapToObj(__ -> new ArrayList<CellType>(columns)) //
				.collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < columns; i++) {
			final int currentI = i;
			final List<CellType> column = IntStream.range(0, rows) //
					.mapToObj(j -> withExpandedRows.get(j).get(currentI)) //
					.toList();
			Seq.zipWithIndex(column) //
					.forEach(cellJ -> expandedUniverse.get(cellJ.v2().intValue()).add(cellJ.v1()));
			if (column.stream().allMatch(CellType.EMPTY::equals)) {
				Seq.zipWithIndex(column) //
						.forEach(cellJ -> expandedUniverse.get(cellJ.v2().intValue()).add(cellJ.v1()));
			}
		}
		return expandedUniverse.stream() //
				.map(List::copyOf) //
				.toList();
	}

	record Position(int x, int y) {}

	public enum CellType
	{
		EMPTY, GALAXY;

		public static CellType from(final char c)
		{
			return switch (c) {
				case '.' -> EMPTY;
				case '#' -> GALAXY;
				default -> throw new IllegalArgumentException(STR."Unsupported cell type '\{c}'");
			};
		}
	}

}
