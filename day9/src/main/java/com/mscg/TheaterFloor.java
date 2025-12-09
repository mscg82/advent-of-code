package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public record TheaterFloor(List<Position> redTiles)
{

	public static TheaterFloor parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Position> redTiles = in.lines() //
					.filter(StreamUtils.nonEmptyString()).map(line -> {
						final var parts = line.split(",");
						return new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
					}) //
					.toList();

			return new TheaterFloor(redTiles);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findMaxRedArea()
	{
		final int totalRedTiles = redTiles().size();
		long currentMaxArea = Long.MIN_VALUE;
		for (int i = 0; i < totalRedTiles; i++) {
			final var corner1 = redTiles().get(i);
			for (int j = i + 1; j < totalRedTiles; j++) {
				final var corner2 = redTiles.get(j);
				final Rectangle rectangle = new Rectangle(corner1, corner2);
				currentMaxArea = Math.max(currentMaxArea, rectangle.area());
			}
		}

		return currentMaxArea;
	}

	public record Rectangle(Position corner1, Position corner2)
	{
		public long area()
		{
			return (Math.abs(corner1.x() - corner2().x()) + 1L) * (Math.abs(corner1.y() - corner2().y()) + 1L);
		}
	}

	public record Position(int x, int y) {}

}
