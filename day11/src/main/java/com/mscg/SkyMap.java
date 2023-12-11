package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mscg.utils.StringTemplates.ILLEGAL_ARGUMENT_EXC;

public record SkyMap(List<List<CellType>> skyCells)
{

	public static SkyMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<CellType>> skyCells = in.lines() //
					.map(line -> line.codePoints() //
							.mapToObj(cp -> BaseCellType.from((char) cp)) //
							.toList()) //
					.toList();

			return new SkyMap(skyCells);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumDistancesBetweenGalaxies()
	{
		final List<List<CellType>> expandedUniverse = expandUniverse(2);
		return sumDistancesInExpandedUniverse(expandedUniverse);
	}

	public long sumDistancesBetweenGalaxiesInOlderUniverse()
	{
		final List<List<CellType>> expandedUniverse = expandUniverse(1_000_000);
		return sumDistancesInExpandedUniverse(expandedUniverse);
	}

	private long sumDistancesInExpandedUniverse(final List<List<CellType>> expandedUniverse)
	{
		final List<Position> galaxies = Seq.zipWithIndex(expandedUniverse) //
				.flatMap(lineY -> {
					final int y = lineY.v2().intValue();
					return Seq.zipWithIndex(lineY.v1()) //
							.filter(cellX -> BaseCellType.GALAXY.equals(cellX.v1)) //
							.map(cellX -> new Position(cellX.v2().intValue(), y));
				}) //
				.toList();
		long sum = 0L;
		for (int i = 0; i < galaxies.size(); i++) {
			final var firstGalaxy = galaxies.get(i);
			for (int j = i + 1; j < galaxies.size(); j++) {
				final var secondGalaxy = galaxies.get(j);
				final int dx = firstGalaxy.x() < secondGalaxy.x() ? 1 : -1;
				for (int x = firstGalaxy.x() + dx; x != secondGalaxy.x() + dx; x += dx) {
					final CellType cell = expandedUniverse.get(firstGalaxy.y()).get(x);
					sum += cell.size();
				}
				final int dy = firstGalaxy.y() < secondGalaxy.y() ? 1 : -1;
				for (int y = firstGalaxy.y() + dy; y != secondGalaxy.y() + dy; y += dy) {
					final CellType cell = expandedUniverse.get(y).get(secondGalaxy.x());
					sum += cell.size();
				}
			}
		}
		return sum;
	}

	private List<List<CellType>> expandUniverse(final int expansionSize)
	{
		// expand the rows
		final List<List<? extends CellType>> withExpandedRows = skyCells.stream() //
				.map(line -> {
					if (line.stream().allMatch(CellType::isEmpty)) {
						return line.stream().map(__ -> new ExpandedEmpty(expansionSize)).toList();
					} else {
						return line;
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
			final List<? extends CellType> column = IntStream.range(0, rows) //
					.mapToObj(j -> withExpandedRows.get(j).get(currentI)) //
					.toList();
			if (column.stream().allMatch(CellType::isEmpty)) {
				Seq.zipWithIndex(column) //
						.forEach(cellJ -> expandedUniverse.get(cellJ.v2().intValue()).add(new ExpandedEmpty(expansionSize)));
			} else {
				Seq.zipWithIndex(column) //
						.forEach(cellJ -> expandedUniverse.get(cellJ.v2().intValue()).add(cellJ.v1()));
			}
		}
		return expandedUniverse.stream() //
				.map(List::copyOf) //
				.toList();
	}

	public sealed interface CellType
	{
		default boolean isEmpty()
		{
			return switch (this) {
				case final ExpandedEmpty _ -> true;
				case final BaseCellType b when b == BaseCellType.EMPTY -> true;
				case final BaseCellType _ -> false;
			};
		}

		default int size()
		{
			return switch (this) {
				case final BaseCellType _ -> 1;
				case final ExpandedEmpty ee -> ee.size();
			};
		}
	}

	public record ExpandedEmpty(int size) implements CellType {}

	record Position(int x, int y) {}

	public enum BaseCellType implements CellType
	{
		EMPTY, GALAXY;

		public static CellType from(final char c)
		{
			return switch (c) {
				case '.' -> EMPTY;
				case '#' -> GALAXY;
				default -> throw ILLEGAL_ARGUMENT_EXC."Unsupported cell type '\{c}'";
			};
		}
	}

}
