package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record PyroclasticFlow(List<Direction> directions)
{

	private static final BitSet EMPTY_BITSET = new BitSet(8);

	public static PyroclasticFlow parseInput(final BufferedReader in) throws IOException
	{
		final String line = in.readLine();
		final List<Direction> directions = line.codePoints() //
				.mapToObj(c -> Direction.from((char) c)) //
				.toList();
		return new PyroclasticFlow(directions);
	}

	@SuppressWarnings("java:S117")
	public long getHeightAfterRocks(final long numberOfRocks)
	{
		var currentIndex = new Index(-1, -1);

		final RockType[] types = RockType.values();
		final Map<Long, BitSet> occupiedPositions = new HashMap<>();

		currentIndex = currentIndex.withTypeIndex((currentIndex.typeIndex() + 1) % types.length);
		Rock currentRock = Rock.spawn(0, types[currentIndex.typeIndex()]);

		long rocksGenerated = 1L;
		long height = 0;
		long loopHeight = 0;
		long loopRepetitions = 0;
		final Map<Index, List<SimulationState>> indexToStates = new HashMap<>();
		while (rocksGenerated <= numberOfRocks) {
			// move
			currentIndex = currentIndex.withDirectionIndex((currentIndex.directionIndex() + 1) % directions.size());
			final var direction = directions.get(currentIndex.directionIndex());
			final Rock movedRock = currentRock.move(direction, occupiedPositions).orElse(currentRock);
			// fall
			final Optional<Rock> fallenRock = movedRock.move(Direction.DOWN, occupiedPositions);
			if (fallenRock.isPresent()) {
				currentRock = fallenRock.get();
				continue;
			}

			// rock can't fall, so it settles down and another one generates
			final Map<Long, BitSet> heightToPositions = movedRock.positions().stream() //
					.collect(Collectors.groupingBy(Position::y, //
							Collectors.mapping(Position::x, StreamUtils.toBitSet())));
			heightToPositions.forEach((y, xs) -> occupiedPositions.compute(y, StreamUtils.mapValuesMerger(xs, //
					(BitSet newSet) -> (BitSet) newSet.clone(), //
					BitSet::or)));

			height = Math.max(height, movedRock.positions().stream() //
					.mapToLong(Position::y) //
					.max() //
					.orElseThrow() + 1L);
			if (indexToStates.containsKey(currentIndex)) {
				final var simulationStates = indexToStates.get(currentIndex);
				for (final var it = simulationStates.listIterator(simulationStates.size()); it.hasPrevious(); ) {
					final var previousState = it.previous();
					if (isDuplicatedState(occupiedPositions, rocksGenerated, height, previousState)) {
						loopHeight = height - previousState.height();
						final long loopLength = rocksGenerated - previousState.rockGenerated();
						final long remainder = numberOfRocks - rocksGenerated;
						loopRepetitions = remainder / loopLength;
						rocksGenerated = numberOfRocks - (remainder % loopLength);
						break;
					}
				}
			}

			indexToStates.computeIfAbsent(currentIndex, __ -> new ArrayList<>()) //
					.add(new SimulationState(height, rocksGenerated));
			currentIndex = currentIndex.withTypeIndex((currentIndex.typeIndex() + 1) % types.length);
			currentRock = Rock.spawn(height, types[currentIndex.typeIndex()]);
			rocksGenerated++;
		}

		return height + (loopHeight * loopRepetitions);
	}

	private static boolean isDuplicatedState(final Map<Long, BitSet> occupiedPositions, final long rocksGenerated,
			final long height, final SimulationState previousState)
	{
		long currentHeightIndex = height - 1;
		long previousHeightIndex = previousState.height() - 1;
		boolean isDuplicate = true;
		for (long r = 0, diff = rocksGenerated - previousState.rockGenerated(); r < diff && isDuplicate; r++) {
			final BitSet currentPositions = occupiedPositions.get(currentHeightIndex);
			final BitSet previousPositions = occupiedPositions.get(previousHeightIndex);
			if (currentPositions == null || !currentPositions.equals(previousPositions)) {
				isDuplicate = false;
			}
			currentHeightIndex--;
			previousHeightIndex--;
		}
		return isDuplicate;
	}

	@RecordBuilder
	record Position(long x, long y) implements PyroclasticFlowPositionBuilder.With
	{
		public Position move(final Direction direction)
		{
			return switch (direction) {
				case RIGHT -> this.withX(x + 1);
				case LEFT -> this.withX(x - 1);
				case DOWN -> this.withY(y - 1);
			};
		}
	}

	record Rock(Set<Position> positions)
	{
		public static Rock spawn(final long maxHeight, final RockType type)
		{
			final Position bottomLeft = new Position(2, maxHeight + 3);
			final Stream<Position> rockPositions = switch (type) {
				case HOR -> Stream.of(bottomLeft, //
						bottomLeft.withX(bottomLeft.x() + 1), //
						bottomLeft.withX(bottomLeft.x() + 2), //
						bottomLeft.withX(bottomLeft.x() + 3));

				case CROSS -> {
					final Position leftEdge = bottomLeft.withY(bottomLeft.y() + 1);
					yield Stream.of(bottomLeft.withX(bottomLeft.x() + 1), //
							leftEdge, //
							leftEdge.withX(leftEdge.x() + 1), //
							leftEdge.withX(leftEdge.x() + 2), //
							bottomLeft.with(p -> {
								p.x(p.x() + 1);
								p.y(p.y() + 2);
							}));
				}

				case L -> {
					final Position rightEdge = bottomLeft.withX(bottomLeft.x() + 2);
					yield Stream.of(bottomLeft, //
							bottomLeft.withX(bottomLeft.x() + 1), //
							rightEdge, //
							rightEdge.withY(rightEdge.y() + 1), //
							rightEdge.withY(rightEdge.y() + 2));
				}

				case VER -> Stream.of(bottomLeft, //
						bottomLeft.withY(bottomLeft.y() + 1), //
						bottomLeft.withY(bottomLeft.y() + 2), //
						bottomLeft.withY(bottomLeft.y() + 3));

				case SQUARE -> Stream.of(bottomLeft, //
						bottomLeft.withX(bottomLeft.x() + 1), //
						bottomLeft.withY(bottomLeft.y() + 1), //
						bottomLeft.with(p -> {
							p.x(p.x() + 1);
							p.y(p.y() + 1);
						}));
			};

			return new Rock(rockPositions.collect(StreamUtils.toUnmodifiableHashSet()));
		}

		public Optional<Rock> move(final Direction direction, final Map<Long, BitSet> occupiedPositions)
		{
			final Set<Position> newPositions = positions.stream() //
					.map(p -> p.move(direction)) //
					.collect(StreamUtils.toUnmodifiableHashSet());

			final boolean hasInvalidPosition = newPositions.stream() //
					.anyMatch(pos -> pos.x() < 0 || pos.x() > 6 || pos.y() < 0 || //
							occupiedPositions.getOrDefault(pos.y(), EMPTY_BITSET).get((int) pos.x()));

			return hasInvalidPosition ? Optional.empty() : Optional.of(new Rock(newPositions));
		}

		@Override
		public String toString()
		{
			final LongSummaryStatistics xSummary = positions.stream() //
					.mapToLong(Position::x) //
					.summaryStatistics();
			final LongSummaryStatistics ySummary = positions.stream() //
					.mapToLong(Position::y) //
					.summaryStatistics();

			final StringBuilder str = new StringBuilder();
			for (long y = ySummary.getMax(); y >= ySummary.getMin(); y--) {
				for (long x = xSummary.getMin(); x <= xSummary.getMax(); x++) {
					if (positions.contains(new Position(x, y))) {
						str.append('#');
					} else {
						str.append('.');
					}
				}
				if (y != ySummary.getMin()) {
					str.append('\n');
				}
			}

			return str.toString();
		}
	}

	@RecordBuilder
	record Index(int directionIndex, int typeIndex) implements PyroclasticFlowIndexBuilder.With {}

	private record SimulationState(long height, long rockGenerated) {}

	public enum Direction
	{
		LEFT, RIGHT, DOWN;

		public static Direction from(final char c)
		{
			return switch (c) {
				case '>' -> RIGHT;
				case '<' -> LEFT;
				default -> throw new IllegalArgumentException("Unsupported direction " + c);
			};
		}
	}

	enum RockType
	{
		HOR, CROSS, L, VER, SQUARE
	}

}
