package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;

public record SensorNetwork(List<Sensor> sensors)
{

	public static SensorNetwork parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Sensor> sensors = in.lines() //
					.map(Sensor::from) //
					.toList();
			return new SensorNetwork(sensors);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countPositionWithoutBeaconAtLine(final long y)
	{
		final List<Sensor> sensorsInRange = sensors.stream() //
				.filter(sensor -> Math.abs(sensor.position().y() - y) <= sensor.radius()) //
				.toList();

		final List<Range> coveredPositions = sensorsInRange.stream() //
				.map(sensor -> {
					final long diffY = Math.abs(sensor.position().y() - y);
					final long xSpan = sensor.radius() - diffY;
					return new Range(sensor.position().x() - xSpan, sensor.position().x() + xSpan);
				}) //
				.toList();

		final List<Range> mergedRanges = mergeRanges(coveredPositions);

		// count beacons in the ranges
		final long beaconsInRanges = sensors.stream() //
				.map(Sensor::beacon) //
				.filter(beacon -> beacon.y() == y) //
				.distinct() //
				.mapToLong(Position::x) //
				.filter(x -> mergedRanges.stream().anyMatch(range -> range.contains(x))) //
				.count();

		return mergedRanges.stream() //
				.mapToLong(Range::size) //
				.sum() - beaconsInRanges;
	}

	public long findBeaconFrequency(final long min, final long max)
	{
		record Side(LongUnaryOperator func, Range xRange) {}
		record Perimeter(Side side1, Side side2, Side side3, Side side4) {}

		final List<Perimeter> perimeters = sensors.stream() //
				.map(sensor -> new Perimeter( //
						new Side( //
								x -> (x - sensor.position().x()) + sensor.position().y() - sensor.radius() - 1, //
								new Range(sensor.position().x(), sensor.position.x() + sensor.radius() + 1)), //
						new Side( //
								x -> (sensor.position().x() - x) + sensor.position().y() + sensor.radius() + 1,
								new Range(sensor.position().x(), sensor.position.x() + sensor.radius() + 1)), //
						new Side( //
								x -> (x - sensor.position().x()) + sensor.position().y() + sensor.radius() + 1,
								new Range(sensor.position.x() - sensor.radius() - 1, sensor.position().x())), //
						new Side( //
								x -> (sensor.position().x() - x) + sensor.position().y() - sensor.radius() - 1, //
								new Range(sensor.position.x() - sensor.radius() - 1, sensor.position().x())))) //
				.toList();

		for (final var perimeter : perimeters) {
			final Side[] sides = new Side[] { perimeter.side1(), perimeter.side2(), perimeter.side3(), perimeter.side4() };
			for (final var side : sides) {
				final long xMin = Math.max(min, side.xRange().min());
				final long xMax = Math.min(max, side.xRange().max());
				for (long x = xMin; x <= xMax; x++) {
					final long y = side.func().applyAsLong(x);
					if (y < min || y > max) {
						continue;
					}
					final boolean inRange = isInRangeOfSensor(x, y);
					if (!inRange) {
						return x * 4_000_000 + y;
					}
				}
			}
		}

		throw new IllegalStateException("Unable to find beacon position");
	}

	private boolean isInRangeOfSensor(final long x, final long y)
	{
		final var pos = new Position(x, y);
		for (final var sensor : sensors) {
			if (pos.distance(sensor.position()) <= sensor.radius()) {
				return true;
			}
		}
		return false;
	}

	private static List<Range> mergeRanges(final List<Range> ranges)
	{
		final List<Range> mergedRanges = new ArrayList<>();
		for (final var range : ranges) {
			boolean merged = false;
			for (final var it = mergedRanges.listIterator(); it.hasNext(); ) {
				final var mergedRange = it.next();
				final Optional<Range> possibleMerge = mergedRange.merge(range);
				if (possibleMerge.isPresent()) {
					it.set(possibleMerge.get());
					merged = true;
					break;
				}
			}
			if (!merged) {
				mergedRanges.add(range);
			}
		}
		if (mergedRanges.size() > 1 && mergedRanges.size() < ranges.size()) {
			return mergeRanges(mergedRanges);
		}
		return mergedRanges;
	}

	@RecordBuilder
	public record Position(long x, long y) implements SensorNetworkPositionBuilder.With
	{
		public long distance(final Position other)
		{
			return Math.abs(x - other.x) + Math.abs(y - other.y);
		}
	}

	public record Sensor(Position position, Position beacon, long radius)
	{
		public static Sensor from(final String line)
		{
			final var pattern = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");
			final var matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid sensor line \"" + line + "\"");
			}
			final var position = new Position(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
			final var beacon = new Position(Long.parseLong(matcher.group(3)), Long.parseLong(matcher.group(4)));
			return new Sensor(position, beacon, beacon.distance(position));
		}
	}

	private record Range(long min, long max)
	{
		public long size()
		{
			return max - min + 1;
		}

		public boolean contains(final long value)
		{
			return value >= min && value <= max;
		}

		public Optional<Range> merge(final Range other)
		{
			if (this.contains(other.min)) {
				if (this.contains(other.max)) {
					// other is contained in this
					return Optional.of(this);
				}
				return Optional.of(new Range(min, other.max));
			}

			if (other.contains(min)) {
				if (other.contains(max)) {
					// this is contained in other
					return Optional.of(other);
				}
				return Optional.of(new Range(other.min, max));
			}

			// no overlap
			return Optional.empty();
		}
	}
}
