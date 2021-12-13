package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.jooq.lambda.Seq;

import io.soabase.recordbuilder.core.RecordBuilder;

public record Instructions(Set<Position> dotPositions, List<Fold> foldInstructions)
{

	public static Set<Position> fold(final Set<Position> dotPositions, final Fold foldInstruction)
	{
		return dotPositions.stream() //
				.map(pos -> switch (foldInstruction.direction()) {
				case HOR -> {
				if (pos.y() <= foldInstruction.position()) {
				yield pos;
				}
				yield pos.withY(2 * foldInstruction.position() - pos.y());
				}
				case VER -> {
				if (pos.x() <= foldInstruction.position()) {
				yield pos;
				}
				yield pos.withX(2 * foldInstruction.position() - pos.x());
				}
				}) //
				.collect(Collectors.toUnmodifiableSet());
	}

	public static Instructions parseInput(final BufferedReader in) throws IOException
	{
		final List<String> positions = new ArrayList<>();
		final List<String> folds = new ArrayList<>();
		String line;

		enum Mode
		{
			POSITIONS, FOLDS
		}

		var mode = Mode.POSITIONS;
		while ((line = in.readLine()) != null) {
			if (line.length() == 0) {
				mode = Mode.FOLDS;
				continue;
			}

			switch (mode) {
				case POSITIONS -> positions.add(line);
				case FOLDS -> folds.add(line);
			}
		}

		final Set<Position> dotPositions = positions.stream() //
				.map(l -> l.split(",")) //
				.map(parts -> new Position(Long.parseLong(parts[0]), Long.parseLong(parts[1]))) //
				.collect(Collectors.toUnmodifiableSet());

		final var pattern = Pattern.compile("fold along ([xy])=(\\d+)");
		final List<Fold> foldInstructions = folds.stream() //
				.map(pattern::matcher) //
				.filter(Matcher::matches) //
				.map(matcher -> {
					final var direction = "y".equals(matcher.group(1)) ? Direction.HOR : Direction.VER;
					final long position = Long.parseLong(matcher.group(2));
					return new Fold(direction, position);
				}) //
				.toList();

		return new Instructions(dotPositions, foldInstructions);
	}

	public long doAFoldAndCount()
	{
		final var folded = fold(dotPositions, foldInstructions.get(0));
		return folded.size();
	}

	public String completeFold()
	{
		final var positions = Seq.seq(foldInstructions.stream()) //
				.foldLeft(dotPositions, Instructions::fold);

		final LongSummaryStatistics xStats = positions.stream() //
				.mapToLong(Position::x) //
				.summaryStatistics();

		final LongSummaryStatistics yStats = positions.stream() //
				.mapToLong(Position::y) //
				.summaryStatistics();

		return LongStream.rangeClosed(yStats.getMin(), yStats.getMax()) //
				.mapToObj(y -> LongStream.rangeClosed(xStats.getMin(), xStats.getMax()) //
						.mapToObj(x -> {
							final var position = new Position(x, y);
							return positions.contains(position) ? "#" : " ";
						}) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));

	}

	@RecordBuilder
	public record Position(long x, long y) implements InstructionsPositionBuilder.With
	{

	}

	public enum Direction
	{
		HOR, VER
	}

	public record Fold(Direction direction, long position)
	{

	}

}
