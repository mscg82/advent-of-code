package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public record AsteroidMap(Type[][] map, int rows, int cols)
{

	public static AsteroidMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Type[][] map = in.lines() //
					.map(line -> line.chars() //
							.mapToObj(c -> Type.from((char) c)) //
							.toArray(Type[]::new)) //
					.toArray(Type[][]::new);
			return new AsteroidMap(map, map.length, map[0].length);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public PositionWithVisibles findBestPosition()
	{
		final List<Position> asteroids = getAsteroids();

		return asteroids.stream() //
				.map(asteroid -> new PositionWithVisibles(asteroid, countVisibleFrom(asteroid, asteroids))) //
				.max(Comparator.comparingLong(PositionWithVisibles::visible)) //
				.orElseThrow();
	}

	public Position getNthVaporizedAsteroid(final int n)
	{
		final List<Position> asteroids = getAsteroids();

		final Position obsv = findBestPosition().position();

		final Map<Integer, List<Position>> thetaToPositions = asteroids.stream() //
				.filter(ast -> !ast.equals(obsv)) //
				.sorted(Comparator.comparingLong(ast -> ast.distanceSquared(obsv)))
				.collect(Collectors.groupingBy(
						ast -> (int) ((Math.PI - Math.atan2(ast.x() - obsv.x(), ast.y() - obsv.y())) * 100_000), //
						TreeMap::new, Collectors.toList()));

		final List<Iterator<Position>> positionsIterators = thetaToPositions.values().stream() //
				.map(List::iterator) //
				.toList();

		final List<Position> orderedPositions = new ArrayList<>(asteroids.size() - 1);
		while (true) {
			boolean added = false;

			for (final Iterator<Position> it : positionsIterators) {
				if (it.hasNext()) {
					orderedPositions.add(it.next());
					added = true;
				}
			}

			if (!added) {
				break;
			}
		}

		return orderedPositions.get(n - 1);
	}

	private List<Position> getAsteroids()
	{
		final List<Position> asteroids = new ArrayList<>();
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				if (map[y][x] == Type.ASTEROID) {
					asteroids.add(new Position(x, y));
				}
			}
		}
		return asteroids;
	}

	private long countVisibleFrom(final Position obsv, final List<Position> asteroids)
	{
		final Map<Integer, List<Position>> thetaToPositions = asteroids.stream() //
				.filter(ast -> !ast.equals(obsv)) //
				.collect(Collectors.groupingBy(ast -> (int) ((Math.atan2(ast.y() - obsv.y(), ast.x() - obsv.x())) * 100_000)));

		return thetaToPositions.size();
	}

	public enum Type
	{
		EMPTY, ASTEROID;

		public static Type from(final char c)
		{
			return switch (c) {
				case '.' -> EMPTY;
				case '#' -> ASTEROID;
				default -> throw new IllegalArgumentException("Unsupported char " + c);
			};
		}

		@Override
		public String toString()
		{
			return switch (this) {
				case EMPTY -> ".";
				case ASTEROID -> "#";
			};
		}
	}

	public record Position(int x, int y)
	{

		public long distanceSquared(final Position other)
		{
			final long dy = other.y() - y;
			final long dx = other.x() - x;
			return dy * dy + dx * dx;
		}

	}

	public record PositionWithVisibles(Position position, long visible)
	{

	}

}
