package com.msg;

import com.mscg.utils.Position8Bits;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharImmutableList;
import it.unimi.dsi.fastutil.chars.CharList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.stream.Collectors;

public record Garden(List<CharList> plants)
{
	public static Garden parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<CharList> plants = in.lines() //
					.map(line -> line.chars() //
							.collect(CharArrayList::new, (list, c) -> list.add((char) c), CharList::addAll))
					.map(list -> CharImmutableList.of(list.toArray(new char[0]))) //
					.map(CharList.class::cast) //
					.toList();
			return new Garden(plants);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeFenceCost()
	{
		final Char2ObjectMap<List<PlantsInfo>> plantsInfo = computePlantsInfo();

		return plantsInfo.values().stream() //
				.flatMap(List::stream) //
				.mapToLong(plantInfo -> {
					final long area = plantInfo.area();
					final long perimeter = plantInfo.sides().values().stream() //
							.mapToLong(List::size) //
							.sum();
					return area * perimeter;
				}) //
				.sum();
	}

	public long computeFenceDiscountedCost()
	{
		final Char2ObjectMap<List<PlantsInfo>> plantsInfo = computePlantsInfo();

		return plantsInfo.values().stream() //
				.flatMap(List::stream) //
				.mapToLong(plantInfo -> {
					final long area = plantInfo.area();
					final long sides = plantInfo.sides().entrySet().stream() //
							.mapToLong(Garden::computeSidesFromPositionBlocks) //
							.sum();
					return area * sides;
				}) //
				.sum();
	}

	private Char2ObjectMap<List<PlantsInfo>> computePlantsInfo()
	{
		final Char2ObjectMap<List<PlantsInfo>> infos = new Char2ObjectOpenHashMap<>();

		final int rows = plants.size();
		final int cols = plants.getFirst().size();
		final SequencedSet<Position8Bits> nonVisitedPositions = LinkedHashSet.newLinkedHashSet(rows * cols);
		for (int x = 0; x < cols; ++x) {
			for (int y = 0; y < rows; ++y) {
				nonVisitedPositions.add(new Position8Bits(x, y));
			}
		}

		while (!nonVisitedPositions.isEmpty()) {
			final var startingPosition = nonVisitedPositions.getFirst();
			final char plant = plants.get(startingPosition.y()).getChar(startingPosition.x());

			long area = 0;
			final Map<Side, List<Position8Bits>> sides = new HashMap<>();

			final var queue = new ArrayDeque<Position8Bits>();
			queue.add(startingPosition);

			while (!queue.isEmpty()) {
				final var current = queue.pop();
				final char currentPlant = plants.get(current.y()).getChar(current.x());
				if (!nonVisitedPositions.remove(current) || currentPlant != plant) {
					continue;
				}

				area++;

				for (final var direction : Direction.values()) {
					final var neighbour = direction.move(current);
					if (!neighbour.isValid(rows, cols) || currentPlant != plants.get(neighbour.y()).getChar(neighbour.x())) {
						final Side sideToAdd = switch (direction) {
							case UP -> new Side(current.y(), true);
							case RIGHT -> new Side(current.x() + 1, false);
							case DOWN -> new Side(current.y() + 1, true);
							case LEFT -> new Side(current.x(), false);
						};
						sides.computeIfAbsent(sideToAdd, _ -> new ArrayList<>()).add(neighbour);
						continue;
					}
					if (nonVisitedPositions.contains(neighbour)) {
						queue.add(neighbour);
					}
				}
			}

			infos.computeIfAbsent(plant, _ -> new ArrayList<>()).add(new PlantsInfo(area, Map.copyOf(sides)));
		}

		return Char2ObjectMaps.unmodifiable(infos);
	}

	private static long computeSidesFromPositionBlocks(final Map.Entry<Side, List<Position8Bits>> entry)
	{
		final boolean hor = entry.getKey().horizontal();
		final Collection<List<Position8Bits>> groups = entry.getValue().stream() //
				.collect(Collectors.groupingBy( //
						p -> hor ? p.y() : p.x())) //
				.values();

		long blocks = 0;
		for (final List<Position8Bits> group : groups) {
			long localBlocks = 1;
			final int[] sortedPositions = group.stream() //
					.mapToInt(p -> hor ? p.x() : p.y()) //
					.sorted() //
					.toArray();

			for (int i = 1; i < sortedPositions.length; i++) {
				if (sortedPositions[i] != sortedPositions[i - 1] + 1) {
					localBlocks++;
				}
			}
			blocks += localBlocks;
		}
		return blocks;
	}

	record Side(int coordinate, boolean horizontal)
	{

		// Custom equals and hashCode are needed to optimize the performances
		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof final Side other)) {
				return false;
			}
			return hashCode() == other.hashCode();
		}

		@Override
		public int hashCode()
		{
			return (horizontal ? 1 : 0) << 16 | coordinate;
		}

	}

	record PlantsInfo(long area, Map<Side, List<Position8Bits>> sides) {}

	enum Direction
	{
		UP, RIGHT, DOWN, LEFT;

		public Position8Bits move(final Position8Bits from)
		{
			return switch (this) {
				case UP -> from.withY(from.y() - 1);
				case RIGHT -> from.withX(from.x() + 1);
				case DOWN -> from.withY(from.y() + 1);
				case LEFT -> from.withX(from.x() - 1);
			};
		}
	}
}
