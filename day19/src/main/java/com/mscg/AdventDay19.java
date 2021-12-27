package com.mscg;

import com.mscg.ScannersCluster.Position;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay19
{
	public static void main(final String[] args) throws Exception
	{
		part1();
		part2();
	}

	private static void part1() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cluster = ScannersCluster.parseInput(in);
			System.out.println("Part 1 - Answer %d".formatted(cluster.decodeScanners().beacons().size()));
		}
	}

	private static void part2() throws IOException
	{
		try (BufferedReader in = readInput()) {
			final var cluster = ScannersCluster.parseInput(in);
			final List<Position> scanners = cluster.decodeScanners().scannersPositions();
			final long maxDistance = Seq.seq(scanners.stream()) //
					.crossSelfJoin() //
					.mapToLong(t -> t.v1().manhattanDistance(t.v2())) //
					.max() //
					.orElseThrow();
			System.out.println("Part 2 - Answer %d".formatted(maxDistance));
		}
	}

	private static BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(AdventDay19.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
	}
}
