package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

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
		List<Moon> status = moons;
		for (int it = 1; it <= steps; it++) {
			final List<Vector3D> gravities = new ArrayList<>(status.size());
			for (final var moon1 : status) {
				Vector3D gravity = Vector3D.ZERO;
				for (final var moon2 : status) {
					if (moon1 == moon2) {
						continue;
					}
					gravity = gravity.add(moon1.gravityWith(moon2));
				}
				gravities.add(gravity);
			}

			status = Seq.seq(status.stream()).zipWithIndex() //
					.map(idx -> idx.v1().with(m -> {
						m.velocity(m.velocity().add(gravities.get(idx.v2().intValue())));
						m.position(m.position().add(m.velocity()));
					})) //
					.toList();
		}

		return status.stream() //
				.mapToLong(moon -> moon.position().stream().map(Math::abs).sum() * moon.velocity().stream().map(Math::abs).sum()) //
				.sum();
	}

}
