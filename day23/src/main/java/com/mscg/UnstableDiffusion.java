package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record UnstableDiffusion(Set<Position> elfPositions, List<MovementRule> movementRules)
{

	public static UnstableDiffusion parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Set<Position> elfPositions = Seq.zipWithIndex(in.lines()) //
					.flatMap(idxLine -> {
						final long y = idxLine.v2();
						final String line = idxLine.v1();
						return Seq.zipWithIndex(line.chars().boxed()) //
								.flatMap(idxChar -> {
									final long x = idxChar.v2();
									final char c = (char) idxChar.v1().intValue();
									if (c == '#') {
										return Stream.of(new Position(x, y));
									} else {
										return Stream.of();
									}
								});
					}) //
					.collect(StreamUtils.toUnmodifiableLinkedHashSet());

			return new UnstableDiffusion(elfPositions,
					List.of(MovementRule.RULE_A, MovementRule.RULE_B, MovementRule.RULE_C, MovementRule.RULE_D));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countFreeSpaces(final int rounds)
	{
		UnstableDiffusion current = this;
		for (int i = 0; i < rounds; i++) {
			current = current.next();
		}

		final LongSummaryStatistics xStats = current.elfPositions.stream() //
				.mapToLong(Position::x) //
				.summaryStatistics();

		final LongSummaryStatistics yStats = current.elfPositions.stream() //
				.mapToLong(Position::y) //
				.summaryStatistics();

		return (xStats.getMax() - xStats.getMin() + 1) * (yStats.getMax() - yStats.getMin() + 1) - elfPositions.size();
	}

	public long findFirstRoundWithoutMovement()
	{
		UnstableDiffusion current = this;
		long round = 0;
		while (true) {
			final var next = current.next();
			round++;
			if (next.elfPositions.equals(current.elfPositions)) {
				break;
			}
			current = next;
		}
		return round;
	}

	public String print(final long minX, final long maxX, final long minY, final long maxY)
	{
		final StringBuilder str = new StringBuilder();
		for (long y = minY; y <= maxY; y++) {
			for (long x = minX; x <= maxX; x++) {
				final var pos = new Position(x, y);
				if (elfPositions.contains(pos)) {
					str.append('#');
				} else {
					str.append('.');
				}
			}
			str.append('\n');
		}
		return str.toString();
	}

	public UnstableDiffusion next()
	{
		record Movement(Position source, Position target) {}

		final List<Movement> proposedMovements = elfPositions.stream() //
				.map(currentPos -> {
					final Map<Direction, Position> adjacents = currentPos.adjacents();
					final boolean noAdjacents = adjacents.values().stream() //
							.noneMatch(elfPositions::contains);
					if (noAdjacents) {
						return new Movement(currentPos, currentPos);
					}

					return new Movement(currentPos, computeTarget(currentPos, adjacents));
				}) //
				.collect(Collectors.toCollection(ArrayList::new));

		final Map<Position, Long> targetFrequencies = proposedMovements.stream() //
				.map(Movement::target) //
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		final Set<Position> newElfPositions = proposedMovements.stream() //
				.map(movement -> {
					if (targetFrequencies.get(movement.target()) != 1L) {
						return movement.source();
					} else {
						return movement.target();
					}
				}) //
				.collect(StreamUtils.toUnmodifiableLinkedHashSet());

		final var newMovementRules = new ArrayList<>(movementRules);
		final MovementRule first = newMovementRules.remove(0);
		newMovementRules.add(first);

		return new UnstableDiffusion(newElfPositions, Collections.unmodifiableList(newMovementRules));
	}

	private Position computeTarget(final Position current, final Map<Direction, Position> adjacents)
	{
		return movementRules.get(0).move(current, adjacents, elfPositions) //
				.or(() -> movementRules.get(1).move(current, adjacents, elfPositions)) //
				.or(() -> movementRules.get(2).move(current, adjacents, elfPositions)) //
				.or(() -> movementRules.get(3).move(current, adjacents, elfPositions)) //
				.orElse(current);
	}

	@FunctionalInterface
	public interface MovementRule
	{

		MovementRule RULE_A = new MovementRule()
		{
			@Override
			public Optional<Position> move(final Position currentPosition, final Map<Direction, Position> adjacents,
					final Set<Position> occupiedPositions)
			{
				if (!occupiedPositions.contains(adjacents.get(Direction.NE)) && //
						!occupiedPositions.contains(adjacents.get(Direction.N)) && //
						!occupiedPositions.contains(adjacents.get(Direction.NW))) {
					return Optional.of(new Position(currentPosition.x(), currentPosition.y() - 1));
				}
				return Optional.empty();
			}

			@Override
			public String toString()
			{
				return "RULE A";
			}
		};

		MovementRule RULE_B = new MovementRule()
		{
			@Override
			public Optional<Position> move(final Position currentPosition, final Map<Direction, Position> adjacents,
					final Set<Position> occupiedPositions)
			{
				if (!occupiedPositions.contains(adjacents.get(Direction.SE)) && //
						!occupiedPositions.contains(adjacents.get(Direction.S)) && //
						!occupiedPositions.contains(adjacents.get(Direction.SW))) {
					return Optional.of(new Position(currentPosition.x(), currentPosition.y() + 1));
				}
				return Optional.empty();
			}

			@Override
			public String toString()
			{
				return "RULE B";
			}
		};

		MovementRule RULE_C = new MovementRule()
		{
			@Override
			public Optional<Position> move(final Position currentPosition, final Map<Direction, Position> adjacents,
					final Set<Position> occupiedPositions)
			{
				if (!occupiedPositions.contains(adjacents.get(Direction.NW)) && //
						!occupiedPositions.contains(adjacents.get(Direction.W)) && //
						!occupiedPositions.contains(adjacents.get(Direction.SW))) {
					return Optional.of(new Position(currentPosition.x() - 1, currentPosition.y()));
				}
				return Optional.empty();
			}

			@Override
			public String toString()
			{
				return "RULE C";
			}
		};

		MovementRule RULE_D = new MovementRule()
		{
			@Override
			public Optional<Position> move(final Position currentPosition, final Map<Direction, Position> adjacents,
					final Set<Position> occupiedPositions)
			{
				if (!occupiedPositions.contains(adjacents.get(Direction.NE)) && //
						!occupiedPositions.contains(adjacents.get(Direction.E)) && //
						!occupiedPositions.contains(adjacents.get(Direction.SE))) {
					return Optional.of(new Position(currentPosition.x() + 1, currentPosition.y()));
				}
				return Optional.empty();
			}

			@Override
			public String toString()
			{
				return "RULE D";
			}
		};

		Optional<Position> move(Position currentPosition, Map<Direction, Position> adjacents, Set<Position> occupiedPositions);

	}

	public record Position(long x, long y)
	{

		public Map<Direction, Position> adjacents()
		{
			return Direction.ALL_DIRECTIONS.stream() //
					.collect(Collectors.toMap( //
							Function.identity(), //
							direction -> switch (direction) {
								case NW -> new Position(x - 1, y - 1);
								case N -> new Position(x, y - 1);
								case NE -> new Position(x + 1, y - 1);
								case W -> new Position(x - 1, y);
								case E -> new Position(x + 1, y);
								case SW -> new Position(x - 1, y + 1);
								case S -> new Position(x, y + 1);
								case SE -> new Position(x + 1, y + 1);
							}, //
							StreamUtils.unsupportedMerger(), //
							() -> new EnumMap<>(Direction.class)));
		}

	}

	public enum Direction
	{
		NW, N, NE, //
		W, /* CENTER, */ E, //
		SW, S, SE;

		private static final Set<Direction> ALL_DIRECTIONS = EnumSet.allOf(Direction.class);
	}

}
