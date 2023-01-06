package com.mscg;

import com.mscg.utils.StreamUtils;
import com.mscg.utils.bfs.BfsVisitor;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record Cubes(List<Position> positions)
{

	public static Cubes parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Position> positions = in.lines() //
					.map(Position::from) //
					.toList();
			return new Cubes(positions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeSurfaceArea()
	{
		final List<Cube> cubes = positions.stream() //
				.map(Cube::from) //
				.toList();
		final long uniqueFacesCount = cubes.stream() //
				.flatMap(cube -> cube.faces().stream()) //
				.distinct() //
				.count();
		final long totalFaces = cubes.size() * 6L;
		return 2 * uniqueFacesCount - totalFaces;
	}

	public long computeExteriorSurfaceArea()
	{
		class Bounds
		{
			long minX = Long.MAX_VALUE;

			long maxX = Long.MIN_VALUE;

			long minY = Long.MAX_VALUE;

			long maxY = Long.MIN_VALUE;

			long minZ = Long.MAX_VALUE;

			long maxZ = Long.MIN_VALUE;
		}

		final var bounds = positions.stream() //
				.reduce(new Bounds(), (b, position) -> {
					b.minX = Math.min(b.minX, position.x());
					b.maxX = Math.max(b.maxX, position.x());
					b.minY = Math.min(b.minY, position.y());
					b.maxY = Math.max(b.maxY, position.y());
					b.minZ = Math.min(b.minZ, position.z());
					b.maxZ = Math.max(b.maxZ, position.z());
					return b;
				}, StreamUtils.unsupportedMerger());

		final var cubes = new HashSet<>(positions);

		final long[] surfaceArea = new long[] { 0L };

		final var visitor = BfsVisitor.<Position, Position, Position>builder() //
				.withDefaultVisitedNodesAllocator() //
				.withoutVisitedNodeAccumulatorAllocator() //
				.withDefaultQueueAllocator() //
				.withNodeIdExtractor(Function.identity()) //
				.withSimpleAdjacentMapper( //
						position -> Stream.of( //
										position.withX(position.x() + 1), //
										position.withX(position.x() - 1), //
										position.withY(position.y() + 1), //
										position.withY(position.y() - 1), //
										position.withZ(position.z() + 1), //
										position.withZ(position.z() - 1)) //
								.filter(pos -> pos.x() >= bounds.minX - 1 && pos.x() <= bounds.maxX + 1 && //
										pos.y() >= bounds.minY - 1 && pos.y() <= bounds.maxY + 1 && //
										pos.z() >= bounds.minZ - 1 && pos.z() <= bounds.maxZ + 1), //
						Function.identity()) //
				.withoutIntermediateResultBuilder() //
				.withNextNodeMapper((cur, adj) -> {
					if (cubes.contains(adj)) {
						surfaceArea[0]++;
						return Optional.empty();
					}
					return Optional.of(adj);
				}) //
				.build();
		visitor.visitFrom(new Position(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1));

		return surfaceArea[0];
	}

	@RecordBuilder
	public record Position(long x, long y, long z) implements CubesPositionBuilder.With
	{
		public static Position from(final String line)
		{
			final String[] parts = line.split(",");
			return new Position(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
		}
	}

	private record Face(Position bottomLeft, Position topRight)
	{
		public static Face from(final Position bottomLeft, final Plane plane)
		{
			final Position topRight = switch (plane) {
				case XY -> bottomLeft.with(bl -> {
					bl.x(bl.x() + 1);
					bl.y(bl.y() + 1);
				});

				case YZ -> bottomLeft.with(bl -> {
					bl.y(bl.y() + 1);
					bl.z(bl.z() + 1);
				});

				case XZ -> bottomLeft.with(bl -> {
					bl.x(bl.x() + 1);
					bl.z(bl.z() + 1);
				});
			};
			return new Face(bottomLeft, topRight);
		}
	}

	private record Cube(List<Face> faces)
	{
		public static Cube from(final Position position)
		{
			final var faces = List.of( //
					Face.from(position, Plane.XY), //
					Face.from(position.withZ(position.z() + 1), Plane.XY), //
					Face.from(position, Plane.YZ), //
					Face.from(position.withX(position.x() + 1), Plane.YZ), //
					Face.from(position, Plane.XZ), //
					Face.from(position.withY(position.y() + 1), Plane.XZ));
			return new Cube(faces);
		}
	}

	private enum Plane
	{
		XY, YZ, XZ
	}

}
