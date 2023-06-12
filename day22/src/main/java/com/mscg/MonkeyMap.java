package com.mscg;

import com.mscg.utils.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record MonkeyMap(Map<Position, Tile> tiles, List<Instruction> instructions, Status initialStatus)
{

	public static MonkeyMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> parts = in.lines().collect(StreamUtils.splitAt(String::isBlank));
			final List<String> mapPart = parts.get(0);
			final String instructionsPart = parts.get(1).get(1);
			Position initialPosition = null;

			final Map<Position, Tile> tiles = new LinkedHashMap<>();
			for (int y = 0, rows = mapPart.size(); y < rows; y++) {
				final String row = mapPart.get(y);
				for (int x = 0; x < row.length(); x++) {
					final Tile tile = Tile.from(row.charAt(x));
					final Position position = new Position(x + 1, y + 1);
					if (initialPosition == null && y == 0 && tile == Tile.OPEN) {
						initialPosition = position;
					}
					tiles.put(position, tile);
				}
			}

			final var pattern = Pattern.compile("([RL])");
			final var matcher = pattern.matcher(instructionsPart);
			final List<Instruction> instructions = new ArrayList<>();
			int lastMatch = 0;
			while (matcher.find()) {
				final int matchStart = matcher.start();
				final String value = instructionsPart.substring(lastMatch, matchStart);
				if (!value.isBlank()) {
					instructions.add(Instruction.from(value));
				}
				instructions.add(Instruction.from(matcher.group()));
				lastMatch = matchStart + 1;
			}
			if (lastMatch < instructionsPart.length()) {
				instructions.add(Instruction.from(instructionsPart.substring(lastMatch)));
			}

			return new MonkeyMap(Collections.unmodifiableMap(tiles), List.copyOf(instructions),
					new Status(initialPosition, Facing.RIGHT));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	@Override
	public String toString()
	{
		return "MonkeyMap {#tiles=" + tiles.size() + ", #instructions=" + instructions.size() + ", initialStatus=" + initialStatus + '}';
	}

	public Tile getTile(final Position position)
	{
		return tiles.getOrDefault(position, Tile.EMPTY);
	}

	public long findPassword()
	{
		final List<Position> openPositions = tiles.entrySet().stream() //
				.flatMap(entry -> switch (entry.getValue()) {
					case OPEN -> Stream.of(entry.getKey());
					case EMPTY, WALL -> Stream.of();
				}) //
				.toList();

		final Map<Integer, MinMax> extremesPerRow = findExtremesPerRow();
		final Map<Integer, MinMax> extremesPerColumn = findExtremesPerColumn();

		final Map<Position, Map<Facing, Status>> adjacentMap = new HashMap<>();
		for (final Position openPosition : openPositions) {
			final Map<Facing, Status> fixedAdjacents = openPosition.adjacents().entrySet().stream() //
					.map(entry -> {
						final var facing = entry.getKey();
						final var adjacent = entry.getValue();
						if (getTile(adjacent) == Tile.EMPTY) {
							final var extremes = switch (facing) {
								case UP, DOWN -> extremesPerColumn.get(openPosition.x());
								case LEFT, RIGHT -> extremesPerRow.get(openPosition.y());
							};
							return switch (facing) {
								case UP, LEFT -> Map.entry(facing, new Status(extremes.max(), facing));
								case RIGHT, DOWN -> Map.entry(facing, new Status(extremes.min(), facing));
							};
						}
						return Map.entry(entry.getKey(), new Status(entry.getValue(), entry.getKey()));
					}) //
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			adjacentMap.put(openPosition, fixedAdjacents);
		}

		final Status status = executeInstuctions(adjacentMap);

		return status.computePassword();
	}

	public long findPasswordOnCube()
	{
		PositionedFace[] faces = findPositionedFaces();

		return 0;
	}

	private PositionedFace[] findPositionedFaces()
	{
		final Set<Position> openPositions = tiles.entrySet().stream() //
				.flatMap(entry -> switch (entry.getValue()) {
					case OPEN -> Stream.of(entry.getKey());
					case EMPTY, WALL -> Stream.of();
				}) //
				.collect(StreamUtils.toUnmodifiableLinkedHashSet());

		final long blockSize = findBlockSize();

		final int maxCol = openPositions.stream() //
				.mapToInt(Position::x) //
				.max() //
				.orElseThrow();

		PositionedFace[] faces = new PositionedFace[6];

		// locate the position of the first face
		for (int x = 1; x <= maxCol; x += blockSize) {
			var topLeft = new Position(x, 1);
			if (openPositions.contains(topLeft)) {
				faces[0] = new PositionedFace(new Face(1, Facing.UP), topLeft);
				break;
			}
		}

		// TODO: locate other faces

		if (Arrays.stream(faces).anyMatch(Objects::isNull)) {
			throw new IllegalStateException("Unable to locate all 6 faces of the cube");
		}

		return faces;
	}

	private long findBlockSize()
	{
		final Map<Integer, MinMax> extremesPerRow = findExtremesPerRow();
		final Map<Integer, MinMax> extremesPerColumn = findExtremesPerColumn();

		final LongSummaryStatistics rowStats = extremesPerRow.values().stream() //
				.mapToLong(mm -> mm.max().x() - mm.min().x() + 1) //
				.summaryStatistics();

		final LongSummaryStatistics columnStats = extremesPerColumn.values().stream() //
				.mapToLong(mm -> mm.max().y() - mm.min().y() + 1) //
				.summaryStatistics();

		return Math.min(rowStats.getMin(), columnStats.getMin());
	}

	private Status executeInstuctions(final Map<Position, Map<Facing, Status>> adjacentMap)
	{
		return instructions.stream() //
				.reduce(initialStatus, (status, instruction) -> switch (instruction) {
					case Rotation r -> status.withFacing(status.facing().rotate(r));
					case Movement(int amount) -> {
						var nextStatus = status;
						for (int i = 0; i < amount; i++) {
							final var next = adjacentMap.get(nextStatus.position()).get(status.facing());
							if (tiles.get(next.position()) == Tile.WALL) {
								break;
							}
							nextStatus = next;
						}
						yield nextStatus;
					}
				}, StreamUtils.unsupportedMerger());
	}

	private Map<Integer, MinMax> findExtremesPerRow()
	{
		final Map<Integer, List<Position>> filledPositionsByRow = tiles.entrySet().stream() //
				.filter(entry -> entry.getValue() != Tile.EMPTY) //
				.collect(Collectors.groupingBy(entry -> entry.getKey().y(), LinkedHashMap::new,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

		return findExtremesPerGroup(filledPositionsByRow, Position::x, (y, x) -> new Position(x, y));
	}

	private Map<Integer, MinMax> findExtremesPerColumn()
	{
		final Map<Integer, List<Position>> filledPositionsByColumn = tiles.entrySet().stream() //
				.filter(entry -> entry.getValue() != Tile.EMPTY) //
				.collect(Collectors.groupingBy(entry -> entry.getKey().x(), LinkedHashMap::new,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

		return findExtremesPerGroup(filledPositionsByColumn, Position::y, Position::new);
	}

	private Map<Integer, MinMax> findExtremesPerGroup(final Map<Integer, List<Position>> groupedPositions,
			final ToIntFunction<Position> coordinateExtractor, final BiFunction<Integer, Integer, Position> coordinateAggregator)
	{
		final Map<Integer, MinMax> extremes = new LinkedHashMap<>();
		groupedPositions.forEach((key, values) -> {
			final var stats = values.stream() //
					.mapToInt(coordinateExtractor) //
					.summaryStatistics();
			extremes.put(key,
					new MinMax(coordinateAggregator.apply(key, stats.getMin()), coordinateAggregator.apply(key, stats.getMax())));

		});
		return Collections.unmodifiableMap(extremes);
	}

	public sealed interface Instruction permits Movement, Rotation
	{
		static Instruction from(final String value)
		{
			return switch (value) {
				case "R" -> Rotation.RIGHT;
				case "L" -> Rotation.LEFT;
				default -> {
					try {
						yield new Movement(Integer.parseInt(value));
					} catch (final NumberFormatException e) {
						throw new IllegalArgumentException("Unsupported instruction value " + value);
					}
				}
			};
		}
	}

	@RecordBuilder
	public record Position(int x, int y) implements MonkeyMapPositionBuilder.With
	{

		public Map<Facing, Position> adjacents()
		{
			return Map.of( //
					Facing.UP, this.withY(y - 1), //
					Facing.RIGHT, this.withX(x + 1), //
					Facing.DOWN, this.withY(y + 1), //
					Facing.LEFT, this.withX(x - 1));
		}

	}

	@RecordBuilder
	public record Status(Position position, Facing facing) implements MonkeyMapStatusBuilder.With
	{
		public long computePassword()
		{
			return 1000L * position.y() + 4L * position.x() + facing.ordinal();
		}
	}

	public record Movement(int amount) implements Instruction {}

	public record Face(int id, Facing facing)
	{
		public static final Map<Integer, Map<Facing, Face>> NORMALIZED_ADJACENT_FACES = Map.of( //
				1, Map.of( //
						Facing.UP, new Face(4, Facing.DOWN), //
						Facing.RIGHT, new Face(6, Facing.UP), //
						Facing.DOWN, new Face(2, Facing.UP), //
						Facing.LEFT, new Face(5, Facing.UP)), //
				2, Map.of( //
						Facing.UP, new Face(1, Facing.DOWN), //
						Facing.RIGHT, new Face(6, Facing.LEFT), //
						Facing.DOWN, new Face(3, Facing.UP), //
						Facing.LEFT, new Face(5, Facing.RIGHT)), //
				3, Map.of( //
						Facing.UP, new Face(2, Facing.DOWN), //
						Facing.RIGHT, new Face(6, Facing.DOWN), //
						Facing.DOWN, new Face(4, Facing.UP), //
						Facing.LEFT, new Face(5, Facing.DOWN)), //
				4, Map.of( //
						Facing.UP, new Face(3, Facing.DOWN), //
						Facing.RIGHT, new Face(6, Facing.RIGHT), //
						Facing.DOWN, new Face(1, Facing.UP), //
						Facing.LEFT, new Face(5, Facing.LEFT)), //
				5, Map.of( //
						Facing.UP, new Face(1, Facing.LEFT), //
						Facing.RIGHT, new Face(2, Facing.LEFT), //
						Facing.DOWN, new Face(3, Facing.LEFT), //
						Facing.LEFT, new Face(4, Facing.LEFT)), //
				6, Map.of( //
						Facing.UP, new Face(1, Facing.RIGHT), //
						Facing.RIGHT, new Face(4, Facing.RIGHT), //
						Facing.DOWN, new Face(3, Facing.RIGHT), //
						Facing.LEFT, new Face(2, Facing.RIGHT)));

		public Face
		{
			if (id < 0 || id > 6) {
				throw new IllegalArgumentException("Invalid face id " + id);
			}
		}
	}

	public record PositionedFace(Face face, Position topLeft) {}

	private record MinMax(Position min, Position max) {}

	public enum Tile
	{
		EMPTY, OPEN, WALL;

		public static Tile from(final char c)
		{
			return switch (c) {
				case ' ' -> EMPTY;
				case '.' -> OPEN;
				case '#' -> WALL;
				default -> throw new IllegalArgumentException("Invalid tile type " + c);
			};
		}
	}

	public enum Rotation implements Instruction
	{
		RIGHT, LEFT
	}

	public enum Facing
	{
		RIGHT, DOWN, LEFT, UP;

		public Facing rotate(final Rotation rotation)
		{
			return switch (this) {
				case RIGHT -> switch (rotation) {
					case RIGHT -> DOWN;
					case LEFT -> UP;
				};

				case DOWN -> switch (rotation) {
					case RIGHT -> LEFT;
					case LEFT -> RIGHT;
				};

				case LEFT -> switch (rotation) {
					case RIGHT -> UP;
					case LEFT -> DOWN;
				};

				case UP -> switch (rotation) {
					case RIGHT -> RIGHT;
					case LEFT -> LEFT;
				};
			};
		}
	}

}
