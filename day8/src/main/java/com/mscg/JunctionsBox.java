package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record JunctionsBox(List<Position3D> junctions)
{

	public static JunctionsBox parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Position3D> junctions = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.map(line -> {
						final var parts = line.split(",");
						return new Position3D(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
					}) //
					.toList();
			return new JunctionsBox(junctions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findLastConnection()
	{
		final List<JunctionPair> sortedJunctionPairs = sortJunctionPairs();
		JunctionPair lastConnection = null;

		final List<Set<Position3D>> circuits = new ArrayList<>();
		for (final JunctionPair junctionPair : sortedJunctionPairs) {
			connectJunctionPair(junctionPair, circuits);
			if (circuits.size() == 1 && circuits.getFirst().size() == junctions.size()) {
				lastConnection = junctionPair;
				break;
			}
		}

		if (lastConnection == null) {
			throw new IllegalStateException("No last connection found");
		}

		return lastConnection.junction1().x() * lastConnection.junction2().x();
	}

	public long findSizeOfBiggestThreeCircuits(final int iterations)
	{
		final List<JunctionPair> sortedJunctionPairs = sortJunctionPairs();

		final List<Set<Position3D>> circuits = new ArrayList<>();
		for (int i = 0; i < sortedJunctionPairs.size() && i < iterations; i++) {
			final JunctionPair junctionPair = sortedJunctionPairs.get(i);
			connectJunctionPair(junctionPair, circuits);
		}

		final Comparator<Set<Position3D>> bySizeDesc = Comparator //
				.<Set<Position3D>>comparingInt(Set::size) //
				.reversed();

		return circuits.stream() //
				.sorted(bySizeDesc) //
				.limit(3) //
				.mapToLong(Set::size) //
				.reduce(1, (v1, v2) -> v1 * v2);
	}

	private List<JunctionPair> sortJunctionPairs()
	{
		final int totalJunctions = junctions.size();
		final List<JunctionPair> sortedJunctionPairs = Arrays.asList(new JunctionPair[(totalJunctions * (totalJunctions - 1)) / 2]);
		int idx = 0;
		for (int i = 0; i < totalJunctions; i++) {
			for (int j = i + 1; j < totalJunctions; j++) {
				final Position3D first = junctions.get(i);
				final Position3D second = junctions.get(j);
				sortedJunctionPairs.set(idx++, new JunctionPair(first, second));
			}
		}
		final Comparator<JunctionPair> byDistanceAsc = Comparator.comparingLong(JunctionPair::distanceSquared);
		sortedJunctionPairs.sort(byDistanceAsc);
		return sortedJunctionPairs;
	}

	private static void connectJunctionPair(final JunctionPair junctionPair, final List<Set<Position3D>> circuits)
	{
		Set<Position3D> circuit1 = null;
		Set<Position3D> circuit2 = null;
		for (final Set<Position3D> circuit : circuits) {
			if (circuit.contains(junctionPair.junction1())) {
				circuit1 = circuit;
			}
			if (circuit.contains(junctionPair.junction2())) {
				circuit2 = circuit;
			}
		}

		mergeCircuits(circuit1, circuit2, junctionPair, circuits);
	}

	private static void mergeCircuits(final Set<Position3D> circuit1, final Set<Position3D> circuit2,
			final JunctionPair junctionPair, final List<Set<Position3D>> circuits)
	{
		if (circuit1 == null && circuit2 == null) {
			// none of the ends was connected to a circuit
			final HashSet<Position3D> circuit = new HashSet<>();
			circuit.add(junctionPair.junction1());
			circuit.add(junctionPair.junction2());
			circuits.add(circuit);
		} else if (circuit1 != null && circuit2 == null) {
			// only end 1 was connected
			circuit1.add(junctionPair.junction2());
		} else if (circuit1 == null /* && circuit2 != null */) {
			// only end 2 was connected
			circuit2.add(junctionPair.junction1());
		} else /* if (circuit1 != null && circuit2 != null) */ {
			if (circuit1 != circuit2) {
				// both ends were connected and are not the same circuit
				circuit1.addAll(circuit2);
				circuits.remove(circuit2);
			}
		}
	}

	private static boolean bothJunctionsAreConnected(final Position3D junction1, final Position3D junction2,
			final HashSet<Position3D> unconnectedJunctions)
	{
		return !(unconnectedJunctions.contains(junction1) || unconnectedJunctions.contains(junction2));
	}

	public record Position3D(long x, long y, long z)
	{

		public long distanceSquared(final Position3D other)
		{
			return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z);
		}

		@Override
		public String toString()
		{
			return "(" + x + "," + y + "," + z + ")";
		}

	}

	record JunctionPair(Position3D junction1, Position3D junction2, long distanceSquared)
	{
		JunctionPair(final Position3D junction1, final Position3D junction2)
		{
			this(junction1, junction2, junction1.distanceSquared(junction2));
		}
	}

}
