package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record MirrorMaze(List<List<Tile>> tiles)
{
	public static MirrorMaze parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<Tile>> tiles = in.lines() //
					.map(line -> line.chars() //
							.mapToObj(c -> Tile.from((char) c)) //
							.toList()) //
					.toList();
			return new MirrorMaze(tiles);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public int computeEnergizedCells()
	{
		final Beam startingBeam = new Beam(new Position(-1, 0), Direction.RIGHT);
		return energizeFrom(startingBeam);
	}

	@SuppressWarnings("preview")
	public int computeMaxEnergizedCells()
	{
		final int yMax = tiles.size();
		final int xMax = tiles.getFirst().size();
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			final StructuredTaskScope.Subtask<Integer> fromTop = scope.fork(
					() -> computeMaxEnergizedCellsForDirection(IntStream.range(0, xMax) //
							.mapToObj(x -> new Beam(new Position(x, -1), Direction.DOWN))));

			final StructuredTaskScope.Subtask<Integer> fromRight = scope.fork(
					() -> computeMaxEnergizedCellsForDirection(IntStream.range(0, yMax) //
							.mapToObj(y -> new Beam(new Position(xMax, y), Direction.LEFT))));

			final StructuredTaskScope.Subtask<Integer> fromBottom = scope.fork(
					() -> computeMaxEnergizedCellsForDirection(IntStream.range(0, xMax) //
							.mapToObj(x -> new Beam(new Position(x, yMax), Direction.UP))));

			final StructuredTaskScope.Subtask<Integer> fromLeft = scope.fork(
					() -> computeMaxEnergizedCellsForDirection(IntStream.range(0, yMax) //
							.mapToObj(y -> new Beam(new Position(-1, y), Direction.RIGHT))));

			try {
				scope.join();
				scope.throwIfFailed();
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("An error occurred", e);
			} catch (final Exception e) {
				throw new IllegalStateException("An error occurred", e);
			}

			return Math.max(fromTop.get(), Math.max(fromRight.get(), Math.max(fromBottom.get(), fromLeft.get())));
		}
	}

	private int computeMaxEnergizedCellsForDirection(final Stream<Beam> startingBeams)
	{
		return startingBeams.mapToInt(this::energizeFrom) //
				.max() //
				.orElseThrow(() -> new IllegalStateException("Unable to compute max energized cells"));
	}

	private int energizeFrom(final Beam startingBeam)
	{
		final int yMax = tiles.size();
		final int xMax = tiles.getFirst().size();

		final Map<Position, Set<Direction>> energizedCells = HashMap.newHashMap(10_000);

		List<Beam> beams = List.of(startingBeam);
		while (!beams.isEmpty()) {
			final var newBeams = new ArrayList<Beam>(beams.size());
			for (final Beam beam : beams) {
				if (beam.position().isInsideBounds(xMax, yMax)) {
					final Set<Direction> directions = energizedCells.computeIfAbsent(beam.position(),
							_ -> EnumSet.noneOf(Direction.class));
					directions.add(beam.direction);
				}
				final Beam movedBeam = beam.move();
				final Position movedPosition = movedBeam.position();
				if (!movedPosition.isInsideBounds(xMax, yMax)) {
					continue;
				}
				final var tile = tiles.get(movedPosition.y()).get(movedPosition.x());
				final var beamsAfterInteraction = movedBeam.interactWithTile(tile);
				for (final Beam newBeam : beamsAfterInteraction) {
					final Position newBeamPosition = newBeam.position();
					if (newBeamPosition.isInsideBounds(xMax, yMax)) {
						final Set<Direction> directions = energizedCells.get(newBeamPosition);
						if (directions == null || !directions.contains(newBeam.direction())) {
							newBeams.add(newBeam);
						}
					}
				}
			}
			beams = newBeams;
		}

		return energizedCells.size();
	}

	@RecordBuilder
	record Position(int x, int y) implements MirrorMazePositionBuilder.With
	{
		boolean isInsideBounds(final int xMax, final int yMax)
		{
			return x >= 0 && x < xMax && y >= 0 && y < yMax;
		}

		Position move(final Direction direction)
		{
			return switch (direction) {
				case UP -> this.withY(y - 1);
				case RIGHT -> this.withX(x + 1);
				case DOWN -> this.withY(y + 1);
				case LEFT -> this.withX(x - 1);
			};
		}
	}

	@RecordBuilder
	record Beam(Position position, Direction direction) implements MirrorMazeBeamBuilder.With
	{
		Beam move()
		{
			return this.withPosition(this.position.move(direction));
		}

		List<Beam> interactWithTile(final Tile tile)
		{
			return switch (tile) {
				case EMPTY -> List.of(this);

				case SPLIT_V -> switch (direction) {
					case UP, DOWN -> List.of(this);
					case LEFT, RIGHT -> List.of( //
							this.withDirection(Direction.UP), //
							this.withDirection(Direction.DOWN));
				};

				case SPLIT_H -> switch (direction) {
					case LEFT, RIGHT -> List.of(this);
					case UP, DOWN -> List.of( //
							this.withDirection(Direction.LEFT), //
							this.withDirection(Direction.RIGHT));
				};

				case MIRROR_TLBR -> switch (direction) {
					case UP -> List.of(this.withDirection(Direction.LEFT));
					case RIGHT -> List.of(this.withDirection(Direction.DOWN));
					case DOWN -> List.of(this.withDirection(Direction.RIGHT));
					case LEFT -> List.of(this.withDirection(Direction.UP));
				};

				case MIRROR_BLTR -> switch (direction) {
					case UP -> List.of(this.withDirection(Direction.RIGHT));
					case RIGHT -> List.of(this.withDirection(Direction.UP));
					case DOWN -> List.of(this.withDirection(Direction.LEFT));
					case LEFT -> List.of(this.withDirection(Direction.DOWN));
				};
			};
		}
	}

	public enum Tile
	{
		EMPTY, SPLIT_V, SPLIT_H, MIRROR_TLBR, MIRROR_BLTR;

		public static Tile from(final char c)
		{
			return switch (c) {
				case '.' -> EMPTY;
				case '|' -> SPLIT_V;
				case '-' -> SPLIT_H;
				case '\\' -> MIRROR_TLBR;
				case '/' -> MIRROR_BLTR;
				default -> throw new IllegalArgumentException("Unsupported tile '" + c + "'");
			};
		}
	}

	enum Direction
	{
		UP, RIGHT, DOWN, LEFT
	}

}
