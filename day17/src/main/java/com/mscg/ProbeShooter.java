package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.soabase.recordbuilder.core.RecordBuilder;

public record ProbeShooter(Area targetArea)
{

	public static ProbeShooter parseInput(final BufferedReader in) throws IOException
	{
		final String line = in.readLine();
		final var pattern = Pattern.compile("x=(.+)\\.\\.(.+), y=(.+)\\.\\.(.+)");
		final var matcher = pattern.matcher(line);
		if (!matcher.find()) {
			throw new IllegalArgumentException("Unable to parse input target area");
		}

		return new ProbeShooter(new Area( //
				new Range(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2))), //
				new Range(Long.parseLong(matcher.group(3)), Long.parseLong(matcher.group(4)))));
	}

	public long findMaxHeight()
	{
		final long[] possibleSpeedX = getPossibleSpeedX();

		long maxY = 0;
		for (final long speedX : possibleSpeedX) {
			for (long speedY = 0; speedY < 1000; speedY++) {
				final Optional<Probe> hit = computeTrajectory(speedX, speedY);

				if (hit.isPresent()) {
					final var probe = hit.get();
					if (maxY < probe.maxY()) {
						maxY = probe.maxY();
					}
				}

			}
		}

		return maxY;
	}

	public long countValidInitialVelocities()
	{
		final long[] possibleSpeedX = getPossibleSpeedX();

		long count = 0;
		for (final long speedX : possibleSpeedX) {
			for (long speedY = -1000; speedY < 1000; speedY++) {
				final Optional<Probe> hit = computeTrajectory(speedX, speedY);
				if (hit.isPresent()) {
					count++;
				}
			}
		}

		return count;
	}

	private Optional<Probe> computeTrajectory(final long speedX, final long speedY)
	{
		final Optional<Probe> hit = Stream.iterate(new Probe(Position.ORIGIN, speedX, speedY, 0), //
				probe -> probe.canReachTarget(targetArea), //
				Probe::move) //
				.filter(probe -> targetArea.contains(probe.position())) //
				.findFirst();
		return hit;
	}

	private long[] getPossibleSpeedX()
	{
		// find the possible values for speed x that allows the probe to reach the target zone
		final long[] possibleSpeedX = LongStream.rangeClosed(1L, targetArea.xs().max()) //
				.filter(vx -> Stream.iterate(new Probe(Position.ORIGIN, vx, 0, 0), //
						probe -> probe.speedX() != 0L && probe.position().x() <= targetArea.xs().max(), //
						Probe::move) //
						.anyMatch(probe -> targetArea.xs().contains(probe.position().x())))
				.toArray();
		return possibleSpeedX;
	}

	@RecordBuilder
	public record Probe(Position position, long speedX, long speedY, long maxY) implements ProbeShooterProbeBuilder.With
	{

		public boolean canReachTarget(final Area targetArea)
		{
			if (speedX <= 0 && position.x() < targetArea.xs().min()) {
				return false;
			}
			if (speedX >= 0 && position.x() > targetArea.xs().max()) {
				return false;
			}
			if (speedY < 0 && position.y() < targetArea.ys().min()) {
				return false;
			}
			return true;
		}

		public Probe move()
		{
			return this.with(probe -> {
				probe.position(probe.position().with(pos -> {
					pos.x(pos.x() + speedX);
					pos.y(pos.y() + speedY);
				}));
				if (probe.position().y() > probe.maxY()) {
					probe.maxY(probe.position().y());
				}
				final long dx = Long.signum(-speedX);
				probe.speedX(probe.speedX() + dx);
				probe.speedY(probe.speedY() - 1);
			});
		}

	}

	@RecordBuilder
	public record Position(long x, long y) implements ProbeShooterPositionBuilder.With
	{

		public static final Position ORIGIN = new Position(0, 0);

	}

	public record Range(long min, long max)
	{

		public boolean contains(final long value)
		{
			return value >= min && value <= max;
		}

	}

	public record Area(Range xs, Range ys)
	{

		public boolean contains(final Position position)
		{
			return xs.contains(position.x()) && ys.contains(position.y());
		}

	}

}
