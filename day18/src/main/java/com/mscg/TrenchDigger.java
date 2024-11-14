package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record TrenchDigger(List<Instruction> instructions)
{

	public static TrenchDigger parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var instructions = in.lines() //
					.map(Instruction::from) //
					.toList();
			return new TrenchDigger(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeInnerArea()
	{
		final var dugTrench = digTrench();
		final var boundingBox = dugTrench.boundingBox();
		final var origin = boundingBox.topLeft();
		final List<Line> perimeter = reorderPerimeter(dugTrench, origin);

		long area = 0;
		Direction normal = null;
		for (int i = 0, l = perimeter.size(); i < l; i++) {
			final var line = perimeter.get(i);
			final var previousLine = perimeter.get((i + l - 1) % l);
			if (i == 0) {
				normal = Direction.UP;
			} else {
				final boolean clockwise = switch (previousLine.direction()) {
					case RIGHT -> line.direction() == Direction.DOWN;
					case LEFT -> line.direction() == Direction.UP;
					case UP -> line.direction() == Direction.RIGHT;
					case DOWN -> line.direction() == Direction.LEFT;
				};
				normal = normal.rotate(clockwise);
			}
			if (line.direction() == Direction.UP || line.direction() == Direction.DOWN) {
				continue;
			}
			final var nextLine = perimeter.get((i + 1) % l);
			final int additionalLength;
			if (previousLine.direction() == nextLine.direction()) {
				additionalLength = 0;
			} else {
				if (normal == Direction.UP) {
					if (previousLine.direction() == Direction.DOWN) {
						additionalLength = -1;
					} else {
						additionalLength = 1;
					}
				} else {
					if (previousLine.direction() == Direction.UP) {
						additionalLength = -1;
					} else {
						additionalLength = 1;
					}
				}
			}

			final int additionalHeight = normal == Direction.DOWN ? 1 : 0;
			final long areaUnderLine = (line.direction() == Direction.RIGHT ? -1 : 1) * //
					((long) line.length() + additionalLength) * (line.extremes().topLeft().y() - origin.y() + additionalHeight);
			area += areaUnderLine;
		}

		return Math.abs(area);
	}

	public long computeInnerAreaFixed()
	{
		final List<Instruction> fixedInstructions = instructions.stream() //
				.map(i -> i.color().asInstruction()) //
				.toList();
		final var fixedTrench = new TrenchDigger(fixedInstructions);
		return fixedTrench.computeInnerArea();
	}

	DugTrench digTrench()
	{
		final var perimeterLines = new ArrayList<Line>(instructions.size());
		var currentPosition = new Position(0, 0);
		for (final Instruction instruction : instructions) {
			final var newPosition = currentPosition.move(instruction.direction(), instruction.amount());
			final var beforeNewPosition = newPosition.move(instruction.direction(), -1);
			final var extremes = switch (instruction.direction()) {
				case UP, LEFT -> new BoundingBox(beforeNewPosition, currentPosition);
				case RIGHT, DOWN -> new BoundingBox(currentPosition, beforeNewPosition);
			};
			perimeterLines.add(new Line(extremes, instruction.direction()));
			currentPosition = newPosition;
		}
		return new DugTrench(List.copyOf(perimeterLines));
	}

	private static List<Line> reorderPerimeter(final DugTrench dugTrench, final Position origin)
	{
		final var perimeter = dugTrench.perimeter();
		int startIndex = -1;
		for (int i = 0, l = perimeter.size(); i < l; i++) {
			final var line = perimeter.get(i);
			if ((line.direction() == Direction.RIGHT || line.direction() == Direction.LEFT) && //
					line.extremes().topLeft().y() == origin.y()) {
				startIndex = i;
				break;
			}
		}
		if (startIndex == -1) {
			throw new IllegalStateException("Unable to find the top-most horizontal line");
		}
		final var reorderedPerimeter = new ArrayList<Line>(perimeter.size());
		reorderedPerimeter.addAll(perimeter.subList(startIndex, perimeter.size()));
		reorderedPerimeter.addAll(perimeter.subList(0, startIndex));
		return List.copyOf(reorderedPerimeter);
	}

	public record Color(int r, int g, int b)
	{
		public static Color from(final String rgb)
		{
			if (!rgb.startsWith("#")) {
				throw new IllegalArgumentException("RGB color has a wrong format");
			}
			return new Color(Integer.parseInt(rgb.substring(1, 3), 16), //
					Integer.parseInt(rgb.substring(3, 5), 16), //
					Integer.parseInt(rgb.substring(5, 7), 16));
		}

		public Instruction asInstruction()
		{
			final var rgbStr = toString();
			final int amount = Integer.parseInt(rgbStr.substring(1, 6), 16);
			final Direction direction = switch (Integer.parseInt(rgbStr.substring(6, 7), 16)) {
				case 0 -> Direction.RIGHT;
				case 1 -> Direction.DOWN;
				case 2 -> Direction.LEFT;
				case 3 -> Direction.UP;
				default -> throw new IllegalStateException("Unexpected direction value from string " + rgbStr);
			};
			return new Instruction(direction, amount, this);
		}

		@Override
		public String toString()
		{
			return "#%02x%02x%02x".formatted(r, g, b);
		}
	}

	public record Instruction(Direction direction, int amount, Color color)
	{
		private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^([URDL]) (\\d+) \\((#[0-9A-Fa-f]{6})\\)$");

		public static Instruction from(final String line)
		{
			final var matcher = INSTRUCTION_PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid instruction: " + line);
			}

			return new Instruction(Direction.from(matcher.group(1).charAt(0)), //
					Integer.parseInt(matcher.group(2)), //
					Color.from(matcher.group(3)));
		}
	}

	@RecordBuilder
	public record Position(int x, int y) implements TrenchDiggerPositionBuilder.With
	{
		public Position move(final Direction direction, final int amount)
		{
			return switch (direction) {
				case UP -> this.withY(y - amount);
				case RIGHT -> this.withX(x + amount);
				case DOWN -> this.withY(y + amount);
				case LEFT -> this.withX(x - amount);
			};
		}
	}

	record BoundingBox(Position topLeft, Position bottomRight)
	{
		public boolean contains(final Position position)
		{
			return position.x() >= topLeft().x() && position.x() <= bottomRight().x() && //
					position.y() >= topLeft().y() && position.y() <= bottomRight().y();
		}
	}

	record Line(BoundingBox extremes, Direction direction)
	{
		public int length()
		{
			return switch (direction) {
				case RIGHT, LEFT -> Math.abs(extremes.topLeft().x() - extremes.bottomRight().x()) + 1;
				case DOWN, UP -> Math.abs(extremes.topLeft().y() - extremes.bottomRight().y()) + 1;
			};
		}

		public boolean contains(final Position position)
		{
			return extremes.contains(position);
		}
	}

	record DugTrench(List<Line> perimeter)
	{
		public BoundingBox boundingBox()
		{
			final IntSummaryStatistics xStats = perimeter.stream() //
					.mapMultiToInt((line, downstream) -> {
						downstream.accept(line.extremes().topLeft().x());
						downstream.accept(line.extremes().bottomRight().x());
					}).summaryStatistics();
			final IntSummaryStatistics yStats = perimeter.stream() //
					.mapMultiToInt((line, downstream) -> {
						downstream.accept(line.extremes().topLeft().y());
						downstream.accept(line.extremes().bottomRight().y());
					}).summaryStatistics();

			return new BoundingBox(new Position(xStats.getMin(), yStats.getMin()), //
					new Position(xStats.getMax(), yStats.getMax()));
		}

		public boolean isOnAnEdge(final Position position)
		{
			return perimeter.stream().anyMatch(line -> line.contains(position));
		}

		public String toVisualizationString()
		{
			final var boundingBox = boundingBox();

			return IntStream.rangeClosed(boundingBox.topLeft().y(), boundingBox.bottomRight().y()) //
					.mapToObj(y -> IntStream.rangeClosed(boundingBox.topLeft().x(), boundingBox.bottomRight().x()) //
							.mapToObj(x -> {
								final var point = new Position(x, y);
								return isOnAnEdge(point) ? "#" : ".";
							}) //
							.collect(Collectors.joining())) //
					.collect(Collectors.joining("\n"));
		}
	}

	public enum Direction
	{
		UP, DOWN, LEFT, RIGHT;

		public static Direction from(final char c)
		{
			return switch (c) {
				case 'U' -> UP;
				case 'D' -> DOWN;
				case 'L' -> LEFT;
				case 'R' -> RIGHT;
				default -> throw new IllegalArgumentException("Unsupported direction '" + c + "'");
			};
		}

		public Direction rotate(final boolean clockwise)
		{
			return switch (this) {
				case UP -> clockwise ? RIGHT : LEFT;
				case DOWN -> clockwise ? LEFT : RIGHT;
				case RIGHT -> clockwise ? DOWN : UP;
				case LEFT -> clockwise ? UP : DOWN;
			};
		}
	}
}
