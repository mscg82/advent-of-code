package com.mscg;

import com.mscg.utils.CollectionUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
		final var loopParts = findLoopParts();

		return Math.max(loopParts.v1().size(), loopParts.v2().size()) - 1;
	}

	public long computeAreaInsideLoop()
	{
		final var loopParts = findLoopParts();
		final boolean sameEnd = loopParts.v1().getLast().equals(loopParts.v2().getLast());
		final List<Position> loop = Stream.concat( //
						loopParts.v1().stream(), //
						loopParts.v2().reversed().stream() //
								.skip(sameEnd ? 1 : 0) //
								.filter(not(startPosition::equals))) //
				.toList();
		final var loopAsSet = new HashSet<>(loop);

		long area = 0;
		boolean inside = false;

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				var position = new Position(x, y);
				if (loopAsSet.contains(position)) {
					// check if we have to switch the status
					final Tile currentTile = tileAtReplacingStart(position);
					inside = switch (currentTile) {
						case NS -> !inside;
						case SE -> {
							do {
								x++;
								position = new Position(x, y);
								if (loopAsSet.contains(position)) {
									final Tile tile = tileAtReplacingStart(position);
									if (tile == Tile.NW) {
										yield !inside;
									} else if (tile == Tile.SW) {
										yield inside;
									}
								}
							} while (x < columns);
							yield inside;
						}
						case NE -> {
							do {
								x++;
								position = new Position(x, y);
								if (loopAsSet.contains(position)) {
									final Tile tile = tileAtReplacingStart(position);
									if (tile == Tile.SW) {
										yield !inside;
									} else if (tile == Tile.NW) {
										yield inside;
									}
								}
							} while (x < columns);
							yield inside;
						}
						case SW -> throw new IllegalStateException(STR."Unexpected SW tube in position \{position}");
						case NW -> throw new IllegalStateException(STR."Unexpected NW tube in position \{position}");
						case EW -> throw new IllegalStateException(STR."Unexpected horizontal tube in position \{position}");
						case EMPTY -> throw new IllegalStateException(STR."Unexpected empty in loop in position \{position}");
						case START -> throw new IllegalStateException(STR."Unexpected start in loop in position \{position}");
					};
				} else {
					if (inside) {
						area++;
					}
				}
			}
		}

		return area;
	}

	private Tile tileAtReplacingStart(final Position position)
	{
		final Tile currentTile = tileAt(position);
		if (currentTile == Tile.START) {
			final Set<Position> startNeighbours = startPosition.neighbours() //
					.filter(pos -> {
						final Tile tile = tileAt(pos);
						return tile != Tile.EMPTY && tile.connections(pos).contains(startPosition);
					}) //
					.collect(Collectors.toSet());

			final boolean isEast = startNeighbours.contains(position.withX(position.x() + 1));
			final boolean isWest = startNeighbours.contains(position.withX(position.x() - 1));
			if (isEast) {
				if (isWest) {
					return Tile.EW;
				}
				final boolean isNorth = startNeighbours.contains(position.withY(position.y() - 1));
				return isNorth ? Tile.NE : Tile.SE;
			}
			if (isWest) {
				final boolean isNorth = startNeighbours.contains(position.withY(position.y() - 1));
				return isNorth ? Tile.NW : Tile.SW;
			}
			return Tile.NS;
		}
		return currentTile;
	}

	private Tuple2<List<Position>, List<Position>> findLoopParts()
	{
		final List<Position> startNeighbours = startPosition.neighbours() //
				.filter(pos -> {
					final Tile tile = tileAt(pos);
					return tile != Tile.EMPTY && tile.connections(pos).contains(startPosition);
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

		return loopTraversal //
				.dropWhile(paths -> !paths.v1().getLast().equals(paths.v2().getLast())) //
				.findFirst() //
				.orElseThrow();
	}

	private List<Position> evolvePath(final List<Position> path)
	{
		final List<Position> reversed = path.reversed();
		final var last = reversed.get(0);
		final var secondFromLast = reversed.get(1);
		final List<Position> connections = tileAt(last).connections(last);
		final Position newPosition = connections.stream() //
				.filter(not(secondFromLast::equals)) //
				.findFirst() //
				.orElseThrow(() -> new IllegalStateException(
						STR."Cannot find connection for node \{last}. Connections: \{connections}"));
		return CollectionUtils.append(path, newPosition);
	}

	private Tile tileAt(final Position pos)
	{
		if (pos.x() < 0 || pos.x() >= columns || pos.y() < 0 || pos.y() >= rows) {
			return Tile.EMPTY;
		}
		return mapLines.get(pos.y()).get(pos.x());
	}

	@RecordBuilder
	protected record Position(int x, int y) implements PipesMapPositionBuilder.With
	{
		public Stream<Position> neighbours()
		{
			return Stream.of( //
					this.withY(y - 1), //
					this.withX(x + 1), //
					this.withY(y + 1), //
					this.withX(x - 1));
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

		public List<Position> connections(final Position position)
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
			return positions.toList();
		}
	}
}
