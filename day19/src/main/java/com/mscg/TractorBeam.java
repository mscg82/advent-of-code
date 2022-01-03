package com.mscg;

import com.mscg.IntcodeV6.InputGenerator;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.LongStream;

public record TractorBeam(IntcodeV6 computer)
{

	public static TractorBeam parseInput(final BufferedReader in) throws IOException
	{
		return new TractorBeam(IntcodeV6.parseInput(in));
	}

	public long countAffectedPosition()
	{
		final List<Position> positions = LongStream.range(0, 50) //
				.mapToObj(x -> LongStream.range(0, 50).mapToObj(y -> new Position(x, y))) //
				.flatMap(s -> s) //
				.toList();

		return positions.stream().parallel() //
				.mapToLong(pos -> {
					final long[] inputs = { pos.x(), pos.y() };
					final var run = computer.execute(InputGenerator.forArray(inputs));
					return run.outputs()[0];
				}) //
				.filter(v -> v == 1) //
				.count();
	}

	public Position findPosition()
	{
		long lastFirstCol = 0;
		final long candidateRow = binarySearchForCandidateRow();

		for (long y = candidateRow; y < 20_000; y++) {
			final var rowResult = countElementsInRow(lastFirstCol, y);
			lastFirstCol = rowResult.position().x();
			if (rowResult.count() >= 100) {
				final long maxX = lastFirstCol + rowResult.count();
				final long curY = y;
				final Optional<Position> candidate = LongStream.range(lastFirstCol, maxX).parallel() //
						.mapToObj(x -> {
							final var colResult = countElementsInColumn(x, curY);
							if (colResult.count() >= 100 && x + 99 < maxX) {
								final var colResult2 = countElementsInColumn(x + 99, curY);
								if (colResult2.count() >= 100) {
									return new Position(x, curY);
								}
							}
							return null;
						}) //
						.filter(Objects::nonNull) //
						.min(Comparator.comparingLong(Position::x));
				if (candidate.isPresent()) {
					return candidate.get();
				}
			}
		}

		throw new IllegalStateException("Can't find beam position");
	}

	private long binarySearchForCandidateRow()
	{
		long minY = 5;
		long maxY = -1;
		while (maxY != minY + 1) {
			final long y = maxY < 0 ? minY * 2 : (minY + maxY) / 2;
			final var res = countElementsInRow(0, y);
			if (res.count() > 150) {
				maxY = y;
			} else {
				minY = y;
			}
		}
		return maxY;
	}

	private PositionAndCount countElementsInColumn(final long x, final long firstRow)
	{
		final long[] position = { x, firstRow };
		// skip empty positions
		while (true) {
			final var run = computer.execute(InputGenerator.forArray(position));
			if (run.outputs()[0] == 1) {
				break;
			}
			position[1]++;
		}
		final var firstBeamPosition = new Position(x, position[1]);
		long count = 0;
		while (true) {
			final var run = computer.execute(InputGenerator.forArray(position));
			if (run.outputs()[0] == 0) {
				break;
			}
			position[1]++;
			count++;
		}
		return new PositionAndCount(firstBeamPosition, count);
	}

	private PositionAndCount countElementsInRow(final long firstCol, final long y)
	{
		final long[] position = { firstCol, y };
		// skip empty positions
		while (true) {
			final var run = computer.execute(InputGenerator.forArray(position));
			if (run.outputs()[0] == 1) {
				break;
			}
			position[0]++;
		}
		final var firstBeamPosition = new Position(position[0], y);
		long count = 0;
		while (true) {
			final var run = computer.execute(InputGenerator.forArray(position));
			if (run.outputs()[0] == 0) {
				break;
			}
			position[0]++;
			count++;
		}
		return new PositionAndCount(firstBeamPosition, count);
	}

	@RecordBuilder
	public record Position(long x, long y) implements TractorBeamPositionBuilder.With {}

	private record PositionAndCount(Position position, long count) {}

}
