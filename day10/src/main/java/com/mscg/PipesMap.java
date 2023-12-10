package com.mscg;

import com.mscg.utils.CollectionUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record PipesMap(List<List<Tile>> mapLines, Position startPosition, int rows, int columns)
{
	public static PipesMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<Tile>> mapLines = in.lines() //
					.map(line -> line.codePoints() //
							.mapToObj(cp -> Tile.from((char) cp)) //
							.toList()) //
					.toList();
			final Position startPosition = Seq.zipWithIndex(mapLines) //
					.flatMap(lineY -> {
						final int y = lineY.v2().intValue();
						return Seq.zipWithIndex(lineY.v1()) //
								.map(charIdx -> charIdx.concat(y));
					}) //
					.findFirst(tileXY -> tileXY.v1() == Tile.START) //
					.map(tileXY -> new Position(tileXY.v2().intValue(), tileXY.v3())) //
					.orElseThrow();
			return new PipesMap(mapLines, startPosition, mapLines.size(), mapLines.getFirst().size());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findLoopMaxDistance()
	{
		final List<Position> startNeighbours = startPosition.neighbours(rows, columns) //
				.filter(pos -> {
					final Tile tile = tileAt(pos);
					return tile != Tile.EMPTY && tile.connections(pos, rows, columns).contains(startPosition);
				}) //
				.toList();
		if (startNeighbours.size() != 2) {
			throw new UnsupportedOperationException(
					STR."Unsupported map type, start has \{startNeighbours.size()} neighbours, expected 2.");
		}
		final var initialPaths = new Tuple2<>( //
				List.of(startPosition, startNeighbours.get(0)), //
				List.of(startPosition, startNeighbours.get(1)));

		final var loopTraversal = Stream.iterate( //
				initialPaths, //
				paths -> new Tuple2<>(evolvePath(paths.v1()), evolvePath(paths.v2())));

		final var loopParts = loopTraversal //
				.dropWhile(paths -> !paths.v1().getLast().equals(paths.v2().getLast())) //
				.findFirst() //
				.orElseThrow();

		return Math.max(loopParts.v1().size(), loopParts.v2().size()) - 1;
	}

	private List<Position> evolvePath(final List<Position> path)
	{
		final List<Position> reversed = path.reversed();
		final var last = reversed.get(0);
		final var secondFromLast = reversed.get(1);
		final Position newPosition = tileAt(last).connections(last, rows, columns).stream() //
				.filter(not(secondFromLast::equals)) //
				.findFirst() //
				.orElseThrow();
		return CollectionUtils.append(path, newPosition);
	}

	private Tile tileAt(final Position pos)
	{
		return mapLines.get(pos.y()).get(pos.x());
	}

	@RecordBuilder
	protected record Position(int x, int y) implements PipesMapPositionBuilder.With
	{
		public Stream<Position> neighbours(final int rows, final int columns)
		{
			return Stream.of( //
							this.withY(y - 1), //
							this.withX(x + 1), //
							this.withY(y + 1), //
							this.withX(x - 1)) //
					.filter(pos -> pos.isValid(rows, columns));
		}

		public boolean isValid(final int rows, final int columns)
		{
			return x >= 0 && x < columns && y >= 0 && y < rows;
		}
	}

	private enum Tile
	{
		START, NS, EW, NE, NW, SW, SE, EMPTY;

		public static Tile from(final char c)
		{
			return switch (c) {
				case 'S' -> START;
				case '|' -> NS;
				case '-' -> EW;
				case 'L' -> NE;
				case 'J' -> NW;
				case '7' -> SW;
				case 'F' -> SE;
				case '.' -> EMPTY;
				default -> throw new IllegalArgumentException(STR."Invalid tile '\{c}'}");
			};
		}

		public List<Position> connections(final Position position, final int rows, final int columns)
		{
			final Stream<Position> positions = switch (this) {
				case NS -> Stream.of( //
						position.withY(position.y() - 1), //
						position.withY(position.y() + 1));

				case EW -> Stream.of( //
						position.withX(position.x() - 1), //
						position.withX(position.x() + 1));

				case NE -> Stream.of( //
						position.withY(position.y() - 1), //
						position.withX(position.x() + 1));

				case NW -> Stream.of( //
						position.withY(position.y() - 1), //
						position.withX(position.x() - 1));

				case SE -> Stream.of( //
						position.withY(position.y() + 1), //
						position.withX(position.x() + 1));

				case SW -> Stream.of( //
						position.withY(position.y() + 1), //
						position.withX(position.x() - 1));

				case START, EMPTY -> throw new IllegalArgumentException(STR."Cannot connect to tile of type \{this}");
			};
			return positions.filter(pos -> pos.isValid(rows, columns)).toList();
		}
	}
}
