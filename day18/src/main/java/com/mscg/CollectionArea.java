package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RecordBuilder
public record CollectionArea(List<List<Block>> blocks, int rows, int cols) implements CollectionAreaBuilder.With
{
	public static CollectionArea parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<Block>> blocks = in.lines() //
					.map(line -> line.chars() //
							.mapToObj(c -> Block.from((char) c)) //
							.toList()) //
					.toList();

			return new CollectionArea(blocks, blocks.size(), blocks.get(0).size());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeResourceValue()
	{
		final CollectionArea lastArea = Seq.iterate(this, CollectionArea::evolve) //
				.limit(11) //
				.findLast() //
				.orElseThrow();
		final Map<Block, Long> countByType = lastArea.blocks.stream() //
				.flatMap(List::stream) //
				.collect(Collectors.groupingBy(b -> b, Collectors.counting()));

		return countByType.getOrDefault(Block.LUMBER, 0L) * countByType.getOrDefault(Block.TREES, 0L);
	}

	public CollectionArea evolve()
	{
		final List<List<Block>> newBlocks = Seq.seq(blocks.stream()) //
				.zipWithIndex() //
				.map(idxLine -> {
					final int y = idxLine.v2().intValue();
					final var line = idxLine.v1();
					return Seq.seq(line.stream()) //
							.zipWithIndex() //
							.map(idxBlock -> {
								final int x = idxBlock.v2().intValue();
								final var block = idxBlock.v1();
								final var position = new Position(x, y);
								final List<Block> adjacentBlocks = position.neighbours(rows, cols) //
										.map(pos -> blocks.get(pos.y()).get(pos.x())) //
										.toList();
								return block.next(adjacentBlocks);
							}) //
							.toList();
				}) //
				.toList();
		return this.withBlocks(newBlocks);
	}

	@Override
	public String toString()
	{
		return blocks.stream() //
				.map(line -> line.stream() //
						.map(block -> switch (block) {
							case OPEN -> ".";
							case TREES -> "|";
							case LUMBER -> "#";
						}) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	@RecordBuilder
	public record Position(int x, int y) implements CollectionAreaPositionBuilder.With
	{

		public Stream<Position> neighbours(final int rows, final int cols)
		{
			return IntStream.rangeClosed(y - 1, y + 1) //
					.mapToObj(yy -> IntStream.rangeClosed(x - 1, x + 1) //
							.mapToObj(xx -> new Position(xx, yy))) //
					.flatMap(s -> s) //
					.filter(pos -> !this.equals(pos)) //
					.filter(pos -> pos.x >= 0 && pos.y >= 0 && pos.x < cols && pos.y < rows);
		}

	}

	public enum Block
	{
		TREES, LUMBER, OPEN;

		public static Block from(final char c)
		{
			return switch (c) {
				case '|' -> TREES;
				case '#' -> LUMBER;
				case '.' -> OPEN;
				default -> throw new IllegalArgumentException("Unsupported block character " + c);
			};
		}

		public Block next(final List<Block> adjacentBlocks)
		{
			return switch (this) {
				case OPEN -> {
					final long trees = adjacentBlocks.stream() //
							.filter(b -> b == TREES) //
							.count();
					yield trees >= 3 ? TREES : OPEN;
				}

				case TREES -> {
					final long lumbers = adjacentBlocks.stream() //
							.filter(b -> b == LUMBER) //
							.count();
					yield lumbers >= 3 ? LUMBER : TREES;
				}

				case LUMBER -> {
					final long trees = adjacentBlocks.stream() //
							.filter(b -> b == TREES) //
							.count();
					final long lumbers = adjacentBlocks.stream() //
							.filter(b -> b == LUMBER) //
							.count();
					yield trees >= 1 && lumbers >= 1 ? LUMBER : OPEN;
				}
			};
		}
	}
}
