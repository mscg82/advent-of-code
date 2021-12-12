package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;

import io.soabase.recordbuilder.core.RecordBuilder;

public record NBody(List<Moon> moons)
{

	public static NBody parseInput(final BufferedReader in) throws IOException
	{
		final var pattern = Pattern.compile("<x=(.+), y=(.+), z=(.+)>");
		try {
			final List<Moon> moons = in.lines() //
					.map(pattern::matcher) //
					.filter(Matcher::matches) //
					.map(matcher -> new Moon( //
							new Vector3D(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)),
									Long.parseLong(matcher.group(3))), //
							Vector3D.ZERO)) //
					.toList();
			return new NBody(moons);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static long signum(final long value)
	{
		return value > 0 ? 1 : value == 0 ? 0 : -1;
	}

	private static List<Moon> executeOneStep(final List<Moon> moons)
	{
		final List<Vector3D> gravities = new ArrayList<>(moons.size());
		for (final var moon1 : moons) {
			Vector3D gravity = Vector3D.ZERO;
			for (final var moon2 : moons) {
				if (moon1 == moon2) {
					continue;
				}
				gravity = gravity.add(moon1.gravityWith(moon2));
			}
			gravities.add(gravity);
		}

		return Seq.seq(moons.stream()).zipWithIndex() //
				.map(idx -> idx.v1().with(m -> {
					m.velocity(m.velocity().add(gravities.get(idx.v2().intValue())));
					m.position(m.position().add(m.velocity()));
				})) //
				.toList();
	}

	record Vector3D(long x, long y, long z)
	{

		public static final Vector3D ZERO = new Vector3D(0, 0, 0);

		public Vector3D add(final Vector3D other)
		{
			return new Vector3D(x + other.x, y + other.y, z + other.z);
		}

		public LongStream stream()
		{
			return LongStream.of(x, y, z);
		}

	}

	@RecordBuilder
	record Moon(Vector3D position, Vector3D velocity) implements NBodyMoonBuilder.With
	{

		public Vector3D gravityWith(final Moon other)
		{
			return new Vector3D(signum(other.position.x() - position().x()), //
					signum(other.position.y() - position().y()), //
					signum(other.position.z() - position().z()));
		}

		public Moon move()
		{
			return new Moon(position.add(velocity), velocity);
		}

	}

	public long simulateAndComputeEnergy(final int steps)
	{
		final List<Moon> status = Stream.iterate(moons, NBody::executeOneStep) //
				.limit(steps + 1L) //
				.reduce((prev, next) -> next) //
				.orElseThrow();

		return status.stream() //
				.mapToLong(moon -> moon.position().stream().map(Math::abs).sum() * moon.velocity().stream().map(Math::abs).sum()) //
				.sum();
	}

	public long findLoopLength()
	{
		final List<Moon> xCoord = this.moons.stream() //
				.map(moon -> moon.withPosition(new Vector3D(moon.position().x(), 0, 0))) //
				.toList();
		final long xLoop = findLoopLength(xCoord);

		final List<Moon> yCoord = this.moons.stream() //
				.map(moon -> moon.withPosition(new Vector3D(0, moon.position().y(), 0))) //
				.toList();
		final long yLoop = findLoopLength(yCoord);

		final List<Moon> zCoord = this.moons.stream() //
				.map(moon -> moon.withPosition(new Vector3D(0, 0, moon.position().z()))) //
				.toList();
		final long zLoop = findLoopLength(zCoord);

		return lcm3(xLoop, yLoop, zLoop);
	}

	private long findLoopLength(List<Moon> status)
	{
		final Set<List<Moon>> seenStatuses = new HashSet<>();
		seenStatuses.add(status);
		long loop = 1;
		while (true) {
			status = executeOneStep(status);
			if (seenStatuses.contains(status)) {
				break;
			}
			seenStatuses.add(status);
			loop++;
		}
		return loop;
	}

	private long gcd(final long a, final long b)
	{
		long x = a;
		long y = b;
		while (y != 0) {
			final long t = y;
			y = x % y;
			x = t;
		}
		return x;
	}

	private long lcm(final long a, final long b)
	{
		return a * b / gcd(a, b);
	}

	private long lcm3(final long a, final long b, final long c)
	{
		return lcm(a, lcm(b, c));
	}
}
