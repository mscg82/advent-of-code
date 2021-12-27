package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.jooq.lambda.tuple.Tuple.tuple;

@SuppressWarnings("java:S4276")
public record ScannersCluster(List<Scanner> scanners)
{
	private static final List<UnaryOperator<Position>> DELTA_TRANSFORMATIONS = List.of( //
			p -> p, //
			p -> p.with(pos -> pos.x(p.x()).y(p.z()).z(p.y())), //
			p -> p.with(pos -> pos.x(p.y()).y(p.x()).z(p.z())), //
			p -> p.with(pos -> pos.x(p.y()).y(p.z()).z(p.x())), //
			p -> p.with(pos -> pos.x(p.z()).y(p.x()).z(p.y())), //
			p -> p.with(pos -> pos.x(p.z()).y(p.y()).z(p.x())) //
	);

	private static final List<UnaryOperator<Position>> TRANSFORMATIONS = List.of( //
			// x1 -> x0
			p -> p, //
			p -> p.with(pos -> pos.x(p.x()).y(-p.z()).z(p.y())), //
			p -> p.with(pos -> pos.x(p.x()).y(-p.y()).z(-p.z())), //
			p -> p.with(pos -> pos.x(p.x()).y(p.z()).z(-p.y())), //

			// x1 -> -x0
			p -> p.with(pos -> pos.x(-p.x()).y(-p.y()).z(p.z())), //
			p -> p.with(pos -> pos.x(-p.x()).y(p.z()).z(p.y())), //
			p -> p.with(pos -> pos.x(-p.x()).y(p.y()).z(-p.z())), //
			p -> p.with(pos -> pos.x(-p.x()).y(-p.z()).z(-p.y())), //

			// y1 -> x0
			p -> p.with(pos -> pos.x(-p.y()).y(p.x()).z(p.z())), //
			p -> p.with(pos -> pos.x(p.z()).y(p.x()).z(p.y())), //
			p -> p.with(pos -> pos.x(p.y()).y(p.x()).z(-p.z())), //
			p -> p.with(pos -> pos.x(-p.z()).y(p.x()).z(-p.y())), //

			// y1 -> -x0
			p -> p.with(pos -> pos.x(p.y()).y(-p.x()).z(p.z())), //
			p -> p.with(pos -> pos.x(-p.z()).y(-p.x()).z(p.y())), //
			p -> p.with(pos -> pos.x(-p.y()).y(-p.x()).z(-p.z())), //
			p -> p.with(pos -> pos.x(p.z()).y(-p.x()).z(-p.y())), //

			// z1 -> x0
			p -> p.with(pos -> pos.x(-p.z()).y(p.y()).z(p.x())), //
			p -> p.with(pos -> pos.x(-p.y()).y(-p.z()).z(p.x())), //
			p -> p.with(pos -> pos.x(p.z()).y(-p.y()).z(p.x())), //
			p -> p.with(pos -> pos.x(p.y()).y(p.z()).z(p.x())), //

			// z1 -> -x0
			p -> p.with(pos -> pos.x(p.z()).y(p.y()).z(-p.x())), //
			p -> p.with(pos -> pos.x(p.y()).y(-p.z()).z(-p.x())), //
			p -> p.with(pos -> pos.x(-p.z()).y(-p.y()).z(-p.x())), //
			p -> p.with(pos -> pos.x(-p.y()).y(p.z()).z(-p.x())) //
	);

	public static ScannersCluster parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines() //
					.filter(line -> !line.isEmpty()) //
					.toList();
			final Map<Integer, List<String>> idToBeacons = new TreeMap<>();
			List<String> lastList = new ArrayList<>();
			for (final String line : allLines) {
				if (line.startsWith("--- scanner ")) {
					final int lastSpaceIndex = line.lastIndexOf(' ');
					final String idStr = line.substring("--- scanner ".length(), lastSpaceIndex);
					lastList = idToBeacons.computeIfAbsent(Integer.parseInt(idStr), ignore -> new ArrayList<>());
					continue;
				}
				lastList.add(line);
			}

			final List<Scanner> scanners = idToBeacons.entrySet().stream() //
					.map(entry -> {
						final List<Position> beacons = entry.getValue().stream() //
								.map(coord -> {
									final var parts = coord.split(",");
									return new Position(Long.parseLong(parts[0]), Long.parseLong(parts[1]),
											Long.parseLong(parts[2]));
								}) //
								.toList();
						return new Scanner(entry.getKey(), beacons);
					}) //
					.toList();

			return new ScannersCluster(scanners);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public static List<Tuple2<UnaryOperator<Position>, Position>> generateRotations(final Position beacon)
	{
		return TRANSFORMATIONS.stream() //
				.map(t -> tuple(t, t.apply(beacon))) //
				.toList();
	}

	public static List<Position> generateDeltas(final Position p1, final Position p2)
	{
		final var delta = new Position(Math.abs(p1.x() - p2.x()), Math.abs(p1.y() - p2.y()), Math.abs(p1.z() - p2.z()));
		return DELTA_TRANSFORMATIONS.stream() //
				.map(t -> t.apply(delta)) //
				.sorted(Comparator.comparingLong(Position::x).thenComparingLong(Position::y).thenComparingLong(Position::z)) //
				.toList();
	}

	public DecodedScanners decodeScanners()
	{
		final Map<Integer, Scanner> idToScanner = scanners.stream() //
				.collect(Collectors.toMap(Scanner::id, s -> s));

		final Map<Integer, List<Adjacent>> adjacencyMap = buildAdjacencyMap();

		@SuppressWarnings("java:S1481")
		record Status(int scannerId, Function<Position, Position> transformationToBase) {}

		final Set<Integer> visitedScanners = new HashSet<>();
		final Deque<Status> queue = new ArrayDeque<>();
		queue.add(new Status(0, UnaryOperator.identity()));

		final Set<Position> decodedPositions = new HashSet<>(idToScanner.get(0).beacons());
		final Map<Integer, Position> scannerPositions = new TreeMap<>();
		scannerPositions.put(0, new Position(0, 0, 0));

		while (!queue.isEmpty()) {
			final var status = queue.pop();
			visitedScanners.add(status.scannerId());
			for (final var adjacent : adjacencyMap.get(status.scannerId())) {
				if (visitedScanners.contains(adjacent.scannerId())) {
					continue;
				}

				final var firstSelfMatchingBeaconsEntry = adjacent.selfMatchingBeacons().entrySet().iterator().next();
				final Tuple2<Position, Position> fistSelfMatchingBeacons = firstSelfMatchingBeaconsEntry.getValue().get(0);
				final List<Tuple2<Position, Position>> matchingBeacons = adjacent.adjacentMatchingBeacons()
						.get(firstSelfMatchingBeaconsEntry.getKey());

				final Position referenceBeacon = fistSelfMatchingBeacons.v1();
				final Position testBeacon = fistSelfMatchingBeacons.v2();
				final List<Position> candidateMatchingBeacons = Seq.seq(matchingBeacons.stream()) //
						.flatMap(Tuple::toSeq) //
						.map(Position.class::cast) //
						.toList();

				final var rototranslationAndScanner = findRototranslation(referenceBeacon, testBeacon, candidateMatchingBeacons) //
						.orElseThrow(() -> new IllegalStateException("Can't find transformation"));

				final Function<Position, Position> transformationToBase = rototranslationAndScanner.rotation()
						.andThen(status.transformationToBase());

				scannerPositions.put(adjacent.scannerId(),
						status.transformationToBase().apply(rototranslationAndScanner.scannerPosition()));
				idToScanner.get(adjacent.scannerId()).beacons().stream() //
						.map(transformationToBase) //
						.forEach(decodedPositions::add);

				queue.add(new Status(adjacent.scannerId(), transformationToBase));
			}
		}

		return new DecodedScanners(List.copyOf(scannerPositions.values()), Set.copyOf(decodedPositions));
	}

	private Optional<RototranslationAndScannerPosition> findRototranslation(final Position referenceBeacon,
			final Position testBeacon, final List<Position> candidateMatchingBeacons)
	{
		for (final Position candidateMatchingBeacon : candidateMatchingBeacons) {
			final List<Tuple2<UnaryOperator<Position>, Position>> rotatedCandidateBeacons = generateRotations(
					candidateMatchingBeacon);
			for (final var rotatedCandidateBeacon : rotatedCandidateBeacons) {
				final Position candidateScannerPosition = referenceBeacon.minus(rotatedCandidateBeacon.v2());
				for (final Position otherBeacon : candidateMatchingBeacons) {
					if (otherBeacon == candidateMatchingBeacon) {
						continue;
					}
					final Position transformedOtherBeacon = rotatedCandidateBeacon.v1().apply(otherBeacon)
							.plus(candidateScannerPosition);
					if (testBeacon.equals(transformedOtherBeacon)) {
						final UnaryOperator<Position> rotation = rotatedCandidateBeacon.v1();
						final UnaryOperator<Position> transformation = p -> rotation.apply(p).plus(candidateScannerPosition);
						return Optional.of(new RototranslationAndScannerPosition(transformation, candidateScannerPosition));
					}
				}
			}
		}
		return Optional.empty();
	}

	private Map<Integer, List<Adjacent>> buildAdjacencyMap()
	{
		final Map<Integer, List<Adjacent>> adjacencyMap = new HashMap<>();

		for (int i = 0, l = scanners.size(); i < l - 1; i++) {
			final Scanner scanner0 = scanners.get(i);
			for (int j = i + 1; j < l; j++) {
				final Scanner scanner1 = scanners.get(j);

				final Map<List<Position>, List<Tuple2<Position, Position>>> deltaToCouples0 = generateDeltasForBeacons(
						scanner0.beacons());

				final Map<List<Position>, List<Tuple2<Position, Position>>> deltaToCouples1 = generateDeltasForBeacons(
						scanner1.beacons());

				final Set<List<Position>> intersection = new HashSet<>(deltaToCouples0.keySet());
				intersection.retainAll(deltaToCouples1.keySet());

				// if intersection has >= 12 beacons, we'll have >= 66 connections
				if (intersection.size() >= 66) {
					final var matchingBeacons0 = new HashMap<>(deltaToCouples0);
					matchingBeacons0.keySet().retainAll(intersection);
					final var matchingBeacons1 = new HashMap<>(deltaToCouples1);
					matchingBeacons1.keySet().retainAll(intersection);
					adjacencyMap.computeIfAbsent(scanner0.id(), ignore -> new ArrayList<>())
							.add(new Adjacent(scanner1.id(), matchingBeacons0, matchingBeacons1));
					adjacencyMap.computeIfAbsent(scanner1.id(), ignore -> new ArrayList<>())
							.add(new Adjacent(scanner0.id(), matchingBeacons1, matchingBeacons0));
				}
			}
		}
		return adjacencyMap;
	}

	private Map<List<Position>, List<Tuple2<Position, Position>>> generateDeltasForBeacons(final List<Position> beacons0)
	{
		return Seq.seq(beacons0.stream()) //
				.zipWithIndex() //
				.crossSelfJoin() //
				.filter(idxCouple -> idxCouple.v1().v2() < idxCouple.v2().v2()) //
				.map(idxCouple -> tuple(idxCouple.v1().v1(), idxCouple.v2().v1())) //
				.collect(Collectors.groupingBy(t -> generateDeltas(t.v1(), t.v2())));
	}

	@RecordBuilder
	public record Position(long x, long y, long z) implements ScannersClusterPositionBuilder.With
	{

		public Position plus(final Position other)
		{
			return new Position(x + other.x, y + other.y, z + other.z);
		}

		public Position minus(final Position other)
		{
			return new Position(x - other.x, y - other.y, z - other.z);
		}

		public long manhattanDistance(final Position other)
		{
			return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
		}

	}

	public record Scanner(int id, List<Position> beacons) {}

	public record DecodedScanners(List<Position> scannersPositions, Set<Position> beacons) {}

	private record Adjacent(int scannerId, Map<List<Position>, List<Tuple2<Position, Position>>> selfMatchingBeacons,
							Map<List<Position>, List<Tuple2<Position, Position>>> adjacentMatchingBeacons) {}

	private record RototranslationAndScannerPosition(UnaryOperator<Position> rotation, Position scannerPosition) {}

}
