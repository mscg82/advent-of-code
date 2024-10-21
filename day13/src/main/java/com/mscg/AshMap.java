package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record AshMap(List<MirroredMap> maps)
{

	public static AshMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<MirroredMap> maps = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(MirroredMap::from) //
					.toList();

			return new AshMap(maps);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long summarize()
	{
		return summarizeMap(MirroredMap::findMirrorPoint);
	}

	public long summarizeSmudged()
	{
		return summarizeMap(MirroredMap::findSmudgedMirrorPoint);
	}

	private int summarizeMap(final Function<MirroredMap, Optional<MirrorPoint>> mirrorPointFinder)
	{
		return Seq.seq(maps.stream()) //
				.map(mirrorPointFinder) //
				.zipWithIndex() //
				.mapToInt(indexPoint -> {
					final var mirrorPoint = indexPoint.v1()
							.orElseThrow(() -> new IllegalStateException("Unable to find mirror point for map " + indexPoint.v2()));
					return switch (mirrorPoint.direction()) {
						case VERTICAL -> mirrorPoint.position();
						case HORIZONTAL -> 100 * mirrorPoint.position();
					};
				}) //
				.sum();
	}

	public record MirroredMap(List<List<CellType>> map, int rows, int cols)
	{

		public Optional<MirrorPoint> findMirrorPoint()
		{
			return innerFindMirrorPoint(false);
		}

		public Optional<MirrorPoint> findSmudgedMirrorPoint()
		{
			return innerFindMirrorPoint(true);
		}

		private Optional<MirrorPoint> innerFindMirrorPoint(final boolean withSmudge)
		{
			record IndexPair(int first, int second, int max)
			{
				Stream<IndexPair> pairs()
				{
					return Stream.iterate(this, curr -> curr.first < max && curr.second >= 0,
							curr -> new IndexPair(curr.first + 1, curr.second - 1, curr.max));
				}
			}

			// try horizontal mirror point
			for (int i = 1; i < rows; i++) {
				final var indexPair = new IndexPair(i, i - 1, rows);
				final long rowDifferences = indexPair.pairs() //
						.mapToLong(currPair -> {
							final var row1 = map.get(currPair.first());
							final var row2 = map.get(currPair.second());
							// compare the rows
							int differences = 0;
							for (int j = 0; j < cols; j++) {
								if (row1.get(j) != row2.get(j)) {
									differences++;
								}
							}
							return differences;
						}) //
						.sum();

				if (withSmudge ? rowDifferences == 1 : rowDifferences == 0) {
					return Optional.of(new MirrorPoint(Direction.HORIZONTAL, i));
				}
			}

			// try vertical mirror point
			for (int j = 1; j < cols; j++) {
				final var indexPair = new IndexPair(j, j - 1, cols);
				final long colDifferences = indexPair.pairs() //
						.mapToLong(currPair -> {
							final int col1 = currPair.first();
							final int col2 = currPair.second();
							// compare the columns
							int differences = 0;
							for (int i = 0; i < rows; i++) {
								if (map.get(i).get(col1) != map.get(i).get(col2)) {
									differences++;
								}
							}
							return differences;
						}) //
						.sum();
				if (withSmudge ? colDifferences == 1 : colDifferences == 0) {
					return Optional.of(new MirrorPoint(Direction.VERTICAL, j));
				}
			}

			return Optional.empty();
		}

		private static MirroredMap from(final Stream<String> lines)
		{
			final List<List<CellType>> map = lines //
					.map(line -> line.chars().mapToObj(c -> CellType.from((char) c)).toList()) //
					.toList();
			return new MirroredMap(map, map.size(), map.getFirst().size());
		}

	}

	public record MirrorPoint(Direction direction, int position) {}

	public enum CellType
	{
		ASH, ROCK;

		private static CellType from(final char c)
		{
			return switch (c) {
				case '.' -> ASH;
				case '#' -> ROCK;
				default -> throw new IllegalArgumentException("invalid cell type " + c);
			};
		}
	}

	public enum Direction
	{
		HORIZONTAL, VERTICAL;
	}

}
