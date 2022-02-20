package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.primitive.ImmutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongSets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.BitSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Stream;

@RecordBuilder
public record BugArea(BitSet bugs, int rows, int cols) implements BugAreaBuilder.With
{
	public static BugArea parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final int[] chars = allLines.stream() //
					.flatMapToInt(String::chars) //
					.toArray();
			final var bugs = new BitSet();
			for (int i = 0; i < chars.length; i++) {
				if ((char) chars[i] == '#') {
					bugs.set(i);
				}
			}

			return new BugArea(bugs, allLines.size(), allLines.get(0).length());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long getBiodiversity()
	{
		return bugs.toLongArray()[0];
	}

	public BugArea evolveUntilFirstRepetition()
	{
		final var seenPatterns = LongSets.mutable.of(getBiodiversity());
		BugArea current = this;
		while (true) {
			final var newBugs = new BitSet();
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					final var currentArea = current;
					final long bugNeighbours = Position.neighbours(x, y) //
							.filter(pos -> currentArea.getBug(pos.x(), pos.y())) //
							.count();

					final boolean currentBugPresent = current.getBug(x, y);
					if (bugNeighbours == 1 || (!currentBugPresent && bugNeighbours == 2)) {
						newBugs.set(getIndex(x, y));
					}
				}
			}
			current = current.withBugs(newBugs);
			final long pattern = current.getBiodiversity();
			if (seenPatterns.contains(pattern)) {
				return current;
			}
			seenPatterns.add(pattern);
		}
	}

	public long evolveRecursively(final int runs)
	{
		final int maxIndex = rows * cols;
		final int midIndex = (rows * (cols / 2)) + (rows / 2);

		final ImmutableIntSet outerLayerIndexes = IntSets.immutable.of(0, 1, 2, 3, 4, 20, 21, 22, 23, 24, 5, 10, 15, 9, 14, 19);
		final ImmutableIntSet innerLayerIndexes = IntSets.immutable.of(6, 7, 8, 16, 17, 18, 11, 13);

		final IntObjectMap<ImmutableList<IndexWithLevelOffset>> adjacencyMap = buildAdjacencyMap();

		ImmutableIntObjectMap<BitSet> status = IntObjectMaps.immutable.of(0, bugs);

		for (int run = 1; run <= runs; run++) {
			final MutableIntObjectMap<BitSet> newStatus = IntObjectMaps.mutable.empty();

			final IntSummaryStatistics levelsStats = status.keySet().summaryStatistics();

			final BitSet outerLayer = status.get(levelsStats.getMin());
			final boolean hasBugInOuterLayer = outerLayer.stream().anyMatch(outerLayerIndexes::contains);
			final int minLayer = hasBugInOuterLayer ? levelsStats.getMin() - 1 : levelsStats.getMin();

			final BitSet innerLayer = status.get(levelsStats.getMax());
			final boolean hasBugInInnerLayer = innerLayer.stream().anyMatch(innerLayerIndexes::contains);
			final int maxLayer = hasBugInInnerLayer ? levelsStats.getMax() + 1 : levelsStats.getMax();

			for (int layerIdx = minLayer; layerIdx <= maxLayer; layerIdx++) {
				final BitSet currentLayer = status.getIfAbsent(layerIdx, BitSet::new);
				final BitSet newLayer = newStatus.getIfAbsentPut(layerIdx, BitSet::new);
				for (int i = 0; i < maxIndex; i++) {
					if (i == midIndex) {
						continue;
					}
					final var adjacentNodes = adjacencyMap.get(i);
					final ImmutableIntObjectMap<BitSet> currentStatus = status;
					final int currentLayerIdx = layerIdx;
					final long bugNeighbours = adjacentNodes.stream() //
							.filter(idxWithOffset -> {
								final BitSet layer = currentStatus.getIfAbsent(currentLayerIdx + idxWithOffset.dz(), BitSet::new);
								return layer.get(idxWithOffset.index());
							}) //
							.count();
					final boolean currentBugPresent = currentLayer.get(i);
					if (bugNeighbours == 1 || (!currentBugPresent && bugNeighbours == 2)) {
						newLayer.set(i);
					}
				}
			}

			status = newStatus.toImmutable();
		}

		return status.values().stream() //
				.mapToInt(BitSet::cardinality) //
				.sum();
	}

	private ImmutableIntObjectMap<ImmutableList<IndexWithLevelOffset>> buildAdjacencyMap()
	{
		final MutableIntObjectMap<ImmutableList<IndexWithLevelOffset>> adjacencyMap = IntObjectMaps.mutable.empty();

		adjacencyMap.put(0, Lists.immutable.of( //
				new IndexWithLevelOffset(1, 0), //
				new IndexWithLevelOffset(5, 0), //
				new IndexWithLevelOffset(7, -1), //
				new IndexWithLevelOffset(11, -1)));

		adjacencyMap.put(1, Lists.immutable.of( //
				new IndexWithLevelOffset(0, 0), //
				new IndexWithLevelOffset(2, 0), //
				new IndexWithLevelOffset(6, 0), //
				new IndexWithLevelOffset(7, -1)));

		adjacencyMap.put(2, Lists.immutable.of( //
				new IndexWithLevelOffset(1, 0), //
				new IndexWithLevelOffset(3, 0), //
				new IndexWithLevelOffset(7, 0), //
				new IndexWithLevelOffset(7, -1)));

		adjacencyMap.put(3, Lists.immutable.of( //
				new IndexWithLevelOffset(2, 0), //
				new IndexWithLevelOffset(4, 0), //
				new IndexWithLevelOffset(8, 0), //
				new IndexWithLevelOffset(7, -1)));

		adjacencyMap.put(4, Lists.immutable.of( //
				new IndexWithLevelOffset(3, 0), //
				new IndexWithLevelOffset(9, 0), //
				new IndexWithLevelOffset(7, -1), //
				new IndexWithLevelOffset(13, -1)));

		adjacencyMap.put(5, Lists.immutable.of( //
				new IndexWithLevelOffset(0, 0), //
				new IndexWithLevelOffset(6, 0), //
				new IndexWithLevelOffset(10, 0), //
				new IndexWithLevelOffset(11, -1)));

		adjacencyMap.put(6, Lists.immutable.of( //
				new IndexWithLevelOffset(1, 0), //
				new IndexWithLevelOffset(5, 0), //
				new IndexWithLevelOffset(7, 0), //
				new IndexWithLevelOffset(11, 0)));

		adjacencyMap.put(7, Lists.immutable.of( //
				new IndexWithLevelOffset(2, 0), //
				new IndexWithLevelOffset(6, 0), //
				new IndexWithLevelOffset(8, 0), //
				new IndexWithLevelOffset(0, 1), //
				new IndexWithLevelOffset(1, 1), //
				new IndexWithLevelOffset(2, 1), //
				new IndexWithLevelOffset(3, 1), //
				new IndexWithLevelOffset(4, 1)));

		adjacencyMap.put(8, Lists.immutable.of( //
				new IndexWithLevelOffset(3, 0), //
				new IndexWithLevelOffset(7, 0), //
				new IndexWithLevelOffset(9, 0), //
				new IndexWithLevelOffset(13, 0)));

		adjacencyMap.put(9, Lists.immutable.of( //
				new IndexWithLevelOffset(4, 0), //
				new IndexWithLevelOffset(8, 0), //
				new IndexWithLevelOffset(14, 0), //
				new IndexWithLevelOffset(13, -1)));

		adjacencyMap.put(10, Lists.immutable.of( //
				new IndexWithLevelOffset(5, 0), //
				new IndexWithLevelOffset(11, 0), //
				new IndexWithLevelOffset(15, 0), //
				new IndexWithLevelOffset(11, -1)));

		adjacencyMap.put(11, Lists.immutable.of( //
				new IndexWithLevelOffset(6, 0), //
				new IndexWithLevelOffset(10, 0), //
				new IndexWithLevelOffset(16, 0), //
				new IndexWithLevelOffset(0, 1), //
				new IndexWithLevelOffset(5, 1), //
				new IndexWithLevelOffset(10, 1), //
				new IndexWithLevelOffset(15, 1), //
				new IndexWithLevelOffset(20, 1)));

		adjacencyMap.put(13, Lists.immutable.of( //
				new IndexWithLevelOffset(8, 0), //
				new IndexWithLevelOffset(14, 0), //
				new IndexWithLevelOffset(18, 0), //
				new IndexWithLevelOffset(4, 1), //
				new IndexWithLevelOffset(9, 1), //
				new IndexWithLevelOffset(14, 1), //
				new IndexWithLevelOffset(19, 1), //
				new IndexWithLevelOffset(24, 1)));

		adjacencyMap.put(14, Lists.immutable.of( //
				new IndexWithLevelOffset(9, 0), //
				new IndexWithLevelOffset(13, 0), //
				new IndexWithLevelOffset(19, 0), //
				new IndexWithLevelOffset(13, -1)));

		adjacencyMap.put(15, Lists.immutable.of( //
				new IndexWithLevelOffset(10, 0), //
				new IndexWithLevelOffset(16, 0), //
				new IndexWithLevelOffset(20, 0), //
				new IndexWithLevelOffset(11, -1)));

		adjacencyMap.put(16, Lists.immutable.of( //
				new IndexWithLevelOffset(11, 0), //
				new IndexWithLevelOffset(15, 0), //
				new IndexWithLevelOffset(17, 0), //
				new IndexWithLevelOffset(21, 0)));

		adjacencyMap.put(17, Lists.immutable.of( //
				new IndexWithLevelOffset(16, 0), //
				new IndexWithLevelOffset(18, 0), //
				new IndexWithLevelOffset(22, 0), //
				new IndexWithLevelOffset(20, 1), //
				new IndexWithLevelOffset(21, 1), //
				new IndexWithLevelOffset(22, 1), //
				new IndexWithLevelOffset(23, 1), //
				new IndexWithLevelOffset(24, 1)));

		adjacencyMap.put(18, Lists.immutable.of( //
				new IndexWithLevelOffset(13, 0), //
				new IndexWithLevelOffset(17, 0), //
				new IndexWithLevelOffset(19, 0), //
				new IndexWithLevelOffset(23, 0)));

		adjacencyMap.put(19, Lists.immutable.of( //
				new IndexWithLevelOffset(14, 0), //
				new IndexWithLevelOffset(18, 0), //
				new IndexWithLevelOffset(24, 0), //
				new IndexWithLevelOffset(13, -1)));

		adjacencyMap.put(20, Lists.immutable.of( //
				new IndexWithLevelOffset(15, 0), //
				new IndexWithLevelOffset(21, 0), //
				new IndexWithLevelOffset(11, -1), //
				new IndexWithLevelOffset(17, -1)));

		adjacencyMap.put(21, Lists.immutable.of( //
				new IndexWithLevelOffset(16, 0), //
				new IndexWithLevelOffset(20, 0), //
				new IndexWithLevelOffset(22, 0), //
				new IndexWithLevelOffset(17, -1)));

		adjacencyMap.put(22, Lists.immutable.of( //
				new IndexWithLevelOffset(17, 0), //
				new IndexWithLevelOffset(21, 0), //
				new IndexWithLevelOffset(23, 0), //
				new IndexWithLevelOffset(17, -1)));

		adjacencyMap.put(23, Lists.immutable.of( //
				new IndexWithLevelOffset(18, 0), //
				new IndexWithLevelOffset(22, 0), //
				new IndexWithLevelOffset(24, 0), //
				new IndexWithLevelOffset(17, -1)));

		adjacencyMap.put(24, Lists.immutable.of( //
				new IndexWithLevelOffset(19, 0), //
				new IndexWithLevelOffset(23, 0), //
				new IndexWithLevelOffset(13, -1), //
				new IndexWithLevelOffset(17, -1)));

		return adjacencyMap.toImmutable();
	}

	private boolean getBug(final int x, final int y)
	{
		if (x < 0 || x >= cols || y < 0 || y >= rows) {
			return false;
		}
		return bugs.get(getIndex(x, y));
	}

	private int getIndex(final int x, final int y)
	{
		return y * cols + x;
	}

	private record Position(int x, int y)
	{
		public static Stream<Position> neighbours(final int x, final int y)
		{
			return Stream.of( //
					new Position(x - 1, y), //
					new Position(x, y - 1), //
					new Position(x + 1, y), //
					new Position(x, y + 1));
		}
	}

	private record IndexWithLevelOffset(int index, int dz) {}
}
