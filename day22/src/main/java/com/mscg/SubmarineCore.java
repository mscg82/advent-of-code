package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.LongToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record SubmarineCore(List<Instruction> rebootInstructions)
{

	public static SubmarineCore parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> rebootInstructions = in.lines() //
					.map(Instruction::parse) //
					.toList();
			return new SubmarineCore(rebootInstructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countActiveCellsStep1()
	{
		final List<Instruction> validInstructions = rebootInstructions.stream() //
				.filter(ins -> Stream.of(ins.cuboid().xRange(), ins.cuboid().yRange(), ins.cuboid().zRange()) //
						.allMatch(range -> range.start() >= -50 && range.end() <= 50)).toList();

		final boolean[][][] cells = new boolean[100][100][100];
		for (final Instruction instruction : validInstructions) {
			final LongToIntFunction fixOffset = l -> (int) l + 50;
			instruction.cuboid().xRange().stream().mapToInt(fixOffset) //
					.forEach(x -> instruction.cuboid().yRange().stream().mapToInt(fixOffset) //
							.forEach(y -> instruction.cuboid().zRange().stream().mapToInt(fixOffset) //
									.forEach(z -> cells[x][y][z] = instruction.on())));
		}

		long count = 0;
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				for (int z = 0; z < 100; z++) {
					if (cells[x][y][z]) {
						count++;
					}
				}
			}
		}

		return count;
	}

	public long countActiveCellsStep2()
	{
		record WeightedCuboid(Cuboid cuboid, long weight) {}

		final List<WeightedCuboid> litCuboids = new ArrayList<>();

		for (final var instruction : rebootInstructions) {
			final var commonCuboids = new ArrayList<WeightedCuboid>();
			for (final var lit : litCuboids) {
				instruction.cuboid().splitOverlapping(lit.cuboid()).ifPresent(mergeResult -> {
					mergeResult.common().stream() //
							.map(c -> new WeightedCuboid(c, lit.weight() * -1)) //
							.forEach(commonCuboids::add);
				});
			}
			litCuboids.addAll(commonCuboids);
			if (instruction.on()) {
				litCuboids.add(new WeightedCuboid(instruction.cuboid(), 1));
			}
		}

		return litCuboids.stream() //
				.mapToLong(wc -> wc.weight() * wc.cuboid().volume()) //
				.sum();
	}

	public record Range(long start, long end)
	{

		public static Range parse(final String value)
		{
			final var parts = value.split("\\.\\.");
			return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
		}

		public Range
		{
			if (start > end) {
				throw new IllegalArgumentException("start can't be greater then end (" + start + ", " + end + ")");
			}
		}

		public boolean contains(final Range other)
		{
			return other.start >= start && other.end <= end;
		}

		public long length()
		{
			return end - start + 1;
		}

		public boolean overlaps(final Range other)
		{
			return !(end < other.start || other.end < start);
		}

		public Collection<Range> splitOverlapping(final Range other)
		{
			if (this.equals(other)) {
				return List.of(this);
			}

			if (this.contains(other)) {
				if (start == other.start) {
					return List.of( //
							other, //
							new Range(other.end + 1, end));
				}
				if (end == other.end) {
					return List.of( //
							new Range(start, other.start - 1), //
							other);
				}
				return List.of( //
						new Range(start, other.start - 1), //
						other, //
						new Range(other.end + 1, end));
			}

			if (other.contains(this)) {
				if (start == other.start) {
					return List.of( //
							this, //
							new Range(end + 1, other.end));
				}
				if (end == other.end) {
					return List.of( //
							new Range(other.start, start - 1), //
							this);
				}
				return List.of( //
						new Range(other.start, start - 1), //
						this, //
						new Range(end + 1, other.end));
			}

			final var left = start < other.start ? this : other;
			final var rigth = start < other.start ? other : this;

			return List.of( //
					new Range(left.start, rigth.start - 1), //
					new Range(rigth.start, left.end), //
					new Range(left.end + 1, rigth.end));
		}

		public LongStream stream()
		{
			return LongStream.rangeClosed(start, end);
		}

	}

	public record Cuboid(Range xRange, Range yRange, Range zRange)
	{

		public boolean contains(final Cuboid other)
		{
			return xRange.contains(other.xRange) && yRange.contains(other.yRange) && zRange.contains(other.zRange);
		}

		public boolean overlaps(final Cuboid other)
		{
			return xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange) && zRange.overlaps(other.zRange);
		}

		public Optional<MergedCuboids> splitOverlapping(final Cuboid other)
		{
			if (!this.overlaps(other)) {
				return Optional.empty();
			}

			final Collection<Range> xSplits = xRange.splitOverlapping(other.xRange);
			final Collection<Range> ySplits = yRange.splitOverlapping(other.yRange);
			final Collection<Range> zSplits = zRange.splitOverlapping(other.zRange);

			final int maxSize = xSplits.size() * ySplits.size() * zSplits.size();
			final var firstParts = new ArrayList<Cuboid>(maxSize);
			final var secondParts = new ArrayList<Cuboid>(maxSize);
			final var commonParts = new ArrayList<Cuboid>(maxSize);
			for (final var x : xSplits) {
				for (final var y : ySplits) {
					for (final var z : zSplits) {
						final var cuboid = new Cuboid(x, y, z);
						final boolean firstContains = this.contains(cuboid);
						final boolean secondContains = other.contains(cuboid);
						if (firstContains && secondContains) {
							commonParts.add(cuboid);
						} else if (firstContains) {
							firstParts.add(cuboid);
						} else if (secondContains) {
							secondParts.add(cuboid);
						}
					}
				}
			}

			return Optional.of(new MergedCuboids( //
					List.copyOf(firstParts), //
					List.copyOf(secondParts), //
					List.copyOf(commonParts)));
		}

		public long volume()
		{
			return xRange.length() * yRange.length() * zRange.length();
		}

	}

	public record Instruction(boolean on, Cuboid cuboid)
	{
		private static final Pattern PATTERN = Pattern.compile("(on|off) x=(.+),y=(.+),z=(.+)");

		public static Instruction parse(final String line)
		{
			final var matcher = PATTERN.matcher(line);
			if (!matcher.find()) {
				throw new IllegalArgumentException("Invalid format for instructions");
			}
			return new Instruction( //
					"on".equals(matcher.group(1)), //
					new Cuboid(Range.parse(matcher.group(2)), //
							Range.parse(matcher.group(3)), //
							Range.parse(matcher.group(4))));
		}

	}

	public record MergedCuboids(Collection<Cuboid> remainderFirst, Collection<Cuboid> remainderSecond, Collection<Cuboid> common) {}

}


