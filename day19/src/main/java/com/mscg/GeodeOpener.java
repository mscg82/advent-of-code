package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record GeodeOpener(List<Blueprint> blueprints)
{
	public static GeodeOpener parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Blueprint> blueprints = in.lines() //
					.map(Blueprint::from) //
					.toList();
			return new GeodeOpener(blueprints);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumAllQualityLevels()
	{
		record Result(Blueprint blueprint, Future<Long> maxGeodes) {}

		try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
			final List<Result> results = blueprints.stream()
					.map(blueprint -> new Result(blueprint, executorService.submit(() -> blueprint.computeMaxGeodesOpened(24))))
					.toList();
			return results.stream() //
					.mapToLong(result -> {
						try {
							return result.blueprint.id() * result.maxGeodes.get();
						} catch (final InterruptedException e) {
							Thread.currentThread().interrupt();
							throw new IllegalStateException(e);
						} catch (final Exception e) {
							throw new IllegalStateException(e);
						}
					}) //
					.sum();
		}
	}

	public long computeFirstThree()
	{
		try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
			final List<Future<Long>> results = IntStream.rangeClosed(0, 2) //
					.mapToObj(i -> executorService.submit(() -> blueprints.get(i).computeMaxGeodesOpened(32))) //
					.toList();
			return results.stream() //
					.mapToLong(result -> {
						try {
							return result.get();
						} catch (final InterruptedException e) {
							Thread.currentThread().interrupt();
							throw new IllegalStateException(e);
						} catch (final Exception e) {
							throw new IllegalStateException(e);
						}
					}) //
					.reduce(1, (p, v) -> p * v);
		}
	}

	public record Blueprint(int id, Map<Material, Robot> robots)
	{
		public static Blueprint from(final String value)
		{
			final var pattern = Pattern.compile("Blueprint (\\d+):(.+)");
			final var matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid expression for blueprint " + value);
			}
			final Map<Material, Robot> robots = Arrays.stream(matcher.group(2).split("\\.")) //
					.map(Robot::from) //
					.collect(Collectors.toMap(Robot::product, r -> r, StreamUtils.unsupportedMerger(),
							() -> new EnumMap<>(Material.class)));
			return new Blueprint(Integer.parseInt(matcher.group(1)), Collections.unmodifiableMap(robots));
		}

		public long computeMaxGeodesOpened(final int maxTime)
		{
			final Status initialStatus = new Status(0, 0, 0, 0, 1, 0, 0, 0, 0);
			final StatusWithParent statusWithParent = new StatusWithParent(null, initialStatus);
			final StatusWithParent maxGeodesStatus = findStatusWithMaxGeodesOpenedRecursive(maxTime, statusWithParent,
					statusWithParent, new HashMap<>());
			return maxGeodesStatus.status().producedGeodes();
		}

		private StatusWithParent findStatusWithMaxGeodesOpenedRecursive(final int maxTime, final StatusWithParent currentWithParent,
				final StatusWithParent maxWithParent, final Map<Status, StatusWithParent> knowStatuses)
		{
			final Status current = currentWithParent.status();
			final Status max = maxWithParent.status();

			final int remainingTime = maxTime - current.elapsedTime();

			if (remainingTime <= 1) {
				return StatusWithParent.max(currentWithParent, maxWithParent);
			}

			final StatusWithParent cachedMax = knowStatuses.get(current);
			if (cachedMax != null) {
				return cachedMax;
			}

			final long maxGeodesBuildable = (remainingTime * (remainingTime + 1L)) / 2 + current.producedGeodes();
			if (maxGeodesBuildable < max.producedGeodes()) {
				return maxWithParent;
			}

			StatusWithParent newMax = StatusWithParent.max(maxWithParent, currentWithParent);
			if (robots.get(Material.GEODE).isBuildable(current)) {
				final StatusWithParent candidateMax = findStatusWithMaxGeodesOpenedRecursive(maxTime,
						currentWithParent.buildAndProduce(maxTime, robots.get(Material.GEODE)), newMax, knowStatuses);
				newMax = StatusWithParent.max(maxWithParent, candidateMax);
			} else {
				int buildsCount = 0;

				if (robots.get(Material.OBSIDIAN).isBuildable(current)) {
					final StatusWithParent candidateMax = findStatusWithMaxGeodesOpenedRecursive(maxTime,
							currentWithParent.buildAndProduce(maxTime, robots.get(Material.OBSIDIAN)), newMax, knowStatuses);
					newMax = StatusWithParent.max(maxWithParent, candidateMax);
					buildsCount++;
				}

				if (remainingTime >= 3 && robots.get(Material.CLAY).isBuildable(current)) {
					final StatusWithParent candidateMax = findStatusWithMaxGeodesOpenedRecursive(maxTime,
							currentWithParent.buildAndProduce(maxTime, robots.get(Material.CLAY)), newMax, knowStatuses);
					newMax = StatusWithParent.max(maxWithParent, candidateMax);
					buildsCount++;
				}

				if (remainingTime >= 4 && robots.get(Material.ORE).isBuildable(current)) {
					final StatusWithParent candidateMax = findStatusWithMaxGeodesOpenedRecursive(maxTime,
							currentWithParent.buildAndProduce(maxTime, robots.get(Material.ORE)), newMax, knowStatuses);
					newMax = StatusWithParent.max(maxWithParent, candidateMax);
					buildsCount++;
				}

				if (remainingTime >= 4 && buildsCount != 3) {
					final StatusWithParent candidateMax = findStatusWithMaxGeodesOpenedRecursive(maxTime,
							currentWithParent.produce(), newMax, knowStatuses);
					newMax = StatusWithParent.max(maxWithParent, candidateMax);
				}
			}
			knowStatuses.put(current, newMax);
			return newMax;
		}
	}

	public record Robot(Material product, List<Cost> costs)
	{
		public static Robot from(final String value)
		{
			final var pattern = Pattern.compile("\\s*Each (.+) robot costs (.+)");
			final var matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid expression for robot " + value);
			}
			final List<Cost> costs = Arrays.stream(matcher.group(2).split(" and ")) //
					.map(Cost::from) //
					.toList();
			return new Robot(Material.from(matcher.group(1)), costs);
		}

		private boolean isBuildable(final Status status)
		{
			for (final Cost cost : costs) {
				final int availableQuantity = switch (cost.material()) {
					case ORE -> status.ore();
					case CLAY -> status.clay();
					case OBSIDIAN -> status.obsidian();
					case GEODE -> throw new IllegalArgumentException("No robots use geodes as build material");
				};
				if (availableQuantity < cost.quantity()) {
					return false;
				}
			}
			return true;
		}
	}

	public record Cost(int quantity, Material material)
	{
		public static Cost from(final String value)
		{
			final var pattern = Pattern.compile("(\\d+) (.+)");
			final var matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid expression for cost " + value);
			}

			return new Cost(Integer.parseInt(matcher.group(1)), Material.from(matcher.group(2)));
		}

		@Override
		public String toString()
		{
			return quantity + " " + material;
		}
	}

	private record StatusWithParent(StatusWithParent parent, Status status)
	{
		public static StatusWithParent max(final StatusWithParent s1, final StatusWithParent s2)
		{
			final Status max = Status.max(s1.status(), s2.status());
			return s1.status() == max ? s1 : s2;
		}

		public StatusWithParent buildAndProduce(final int maxTime, final Robot robot)
		{
			return new StatusWithParent(this, status.buildAndProduce(maxTime, robot));
		}

		public StatusWithParent produce()
		{
			return new StatusWithParent(this, status.produce());
		}

		@SuppressWarnings({ "java:S106", "unused" })
		public void printHistory()
		{
			final List<Status> history = Stream.iterate(this, Objects::nonNull, StatusWithParent::parent) //
					.reduce(new ArrayList<>(), (list, status) -> {
						list.add(0, status.status());
						return list;
					}, StreamUtils.unsupportedMerger());
			history.forEach(System.out::println);
		}

		@Override
		public String toString()
		{
			return "{root=" + (parent == null) + "; " + status + '}';
		}
	}

	private record Status(int elapsedTime, int ore, int clay, int obsidian, int oreRobots, int clayRobots, int obsidianRobots,
						  int geodeRobots, long producedGeodes)
	{
		private static final Comparator<Status> COMPARATOR = Comparator.comparingLong(Status::producedGeodes) //
				.thenComparingInt(Status::elapsedTime);

		public static Status max(final Status s1, final Status s2)
		{
			return COMPARATOR.compare(s1, s2) >= 0 ? s1 : s2;
		}

		public Status buildAndProduce(final int maxTime, final Robot robot)
		{
			int newOre = ore;
			int newClay = clay;
			int newObsidian = obsidian;
			for (final Cost cost : robot.costs()) {
				switch (cost.material()) {
					case ORE -> newOre -= cost.quantity();
					case CLAY -> newClay -= cost.quantity();
					case OBSIDIAN -> newObsidian -= cost.quantity();
					case GEODE -> throw new IllegalArgumentException("No robots use geodes as build material");
				}
			}

			int newOreRobots = oreRobots;
			int newClayRobots = clayRobots;
			int newObsidianRobots = obsidianRobots;
			int newGeodeRobots = geodeRobots;
			switch (robot.product()) {
				case ORE -> newOreRobots++;
				case CLAY -> newClayRobots++;
				case OBSIDIAN -> newObsidianRobots++;
				case GEODE -> newGeodeRobots++;
			}

			final long newProducedGeodes;
			if (robot.product() == Material.GEODE) {
				newProducedGeodes = producedGeodes + (maxTime - (elapsedTime + 1));
			} else {
				newProducedGeodes = producedGeodes;
			}

			return new Status(elapsedTime + 1, newOre + oreRobots, newClay + clayRobots, newObsidian + obsidianRobots, //
					newOreRobots, newClayRobots, newObsidianRobots, newGeodeRobots, newProducedGeodes);
		}

		public Status produce()
		{
			return new Status(elapsedTime + 1, ore + oreRobots, clay + clayRobots, obsidian + obsidianRobots, //
					oreRobots, clayRobots, obsidianRobots, geodeRobots, producedGeodes);
		}

		@Override
		public String toString()
		{
			return "{t=" + elapsedTime + ", materials={ore=" + ore + ", clay=" + clay + ", obsidian=" + obsidian + //
					"}, robots={ore=" + oreRobots + ", clay=" + clayRobots + ", obsidian=" + obsidianRobots + //
					", geode=" + geodeRobots + "}, producedGeodes=" + producedGeodes + '}';
		}
	}

	public enum Material
	{
		ORE, CLAY, OBSIDIAN, GEODE;

		public static Material from(final String value)
		{
			return switch (value) {
				case "ore" -> ORE;
				case "clay" -> CLAY;
				case "obsidian" -> OBSIDIAN;
				case "geode" -> GEODE;
				default -> throw new IllegalArgumentException("Unsupported material " + value);
			};
		}

		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
	}

}
