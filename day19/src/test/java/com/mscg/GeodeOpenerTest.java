package com.mscg;

import com.mscg.GeodeOpener.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeodeOpenerTest
{

	@Test
	void testOpenedGeodes1()
	{
		final GeodeOpener.Blueprint blueprint = GeodeOpener.Blueprint.from(
				"Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.");

		final Map<Material, List<GeodeOpener.Cost>> productToCosts = blueprint.robots().values().stream() //
				.collect(Collectors.toMap(GeodeOpener.Robot::product, GeodeOpener.Robot::costs));

		assertEquals(Map.of( //
						Material.ORE, List.of(new GeodeOpener.Cost(4, Material.ORE)), //
						Material.CLAY, List.of(new GeodeOpener.Cost(2, Material.ORE)), //
						Material.OBSIDIAN, List.of(new GeodeOpener.Cost(3, Material.ORE), new GeodeOpener.Cost(14, Material.CLAY)), //
						Material.GEODE, List.of(new GeodeOpener.Cost(2, Material.ORE), new GeodeOpener.Cost(7, Material.OBSIDIAN))), //
				productToCosts);

		assertEquals(9, blueprint.computeMaxGeodesOpened(24));
		// assertEquals(56, blueprint.computeMaxGeodesOpened(32));
	}

	@Test
	void testOpenedGeodes2()
	{
		final GeodeOpener.Blueprint blueprint = GeodeOpener.Blueprint.from(
				"Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");

		final Map<Material, List<GeodeOpener.Cost>> productToCosts = blueprint.robots().values().stream() //
				.collect(Collectors.toMap(GeodeOpener.Robot::product, GeodeOpener.Robot::costs));

		assertEquals(Map.of( //
						Material.ORE, List.of(new GeodeOpener.Cost(2, Material.ORE)), //
						Material.CLAY, List.of(new GeodeOpener.Cost(3, Material.ORE)), //
						Material.OBSIDIAN, List.of(new GeodeOpener.Cost(3, Material.ORE), new GeodeOpener.Cost(8, Material.CLAY)), //
						Material.GEODE, List.of(new GeodeOpener.Cost(3, Material.ORE), new GeodeOpener.Cost(12, Material.OBSIDIAN))), //
				productToCosts);

		assertEquals(12, blueprint.computeMaxGeodesOpened(24));
		// assertEquals(62, blueprint.computeMaxGeodesOpened(32));
	}

	@Test
	void testOpenedGeodes3()
	{
		final GeodeOpener.Blueprint blueprint = GeodeOpener.Blueprint.from(
				"Blueprint 3: Each ore robot costs 2 ore. Each clay robot costs 2 ore. Each obsidian robot costs 2 ore and 17 clay. Each geode robot costs 2 ore and 10 obsidian.");

		final Map<Material, List<GeodeOpener.Cost>> productToCosts = blueprint.robots().values().stream() //
				.collect(Collectors.toMap(GeodeOpener.Robot::product, GeodeOpener.Robot::costs));

		assertEquals(Map.of( //
						Material.ORE, List.of(new GeodeOpener.Cost(2, Material.ORE)), //
						Material.CLAY, List.of(new GeodeOpener.Cost(2, Material.ORE)), //
						Material.OBSIDIAN, List.of(new GeodeOpener.Cost(2, Material.ORE), new GeodeOpener.Cost(17, Material.CLAY)), //
						Material.GEODE, List.of(new GeodeOpener.Cost(2, Material.ORE), new GeodeOpener.Cost(10, Material.OBSIDIAN))), //
				productToCosts);

		assertEquals(9, blueprint.computeMaxGeodesOpened(24));
		// assertEquals(54, blueprint.computeMaxGeodesOpened(32));
	}

	@Test
	void testOpenedGeodes4()
	{
		final GeodeOpener.Blueprint blueprint = GeodeOpener.Blueprint.from(
				"Blueprint 4: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 15 clay. Each geode robot costs 3 ore and 8 obsidian.");

		final Map<Material, List<GeodeOpener.Cost>> productToCosts = blueprint.robots().values().stream() //
				.collect(Collectors.toMap(GeodeOpener.Robot::product, GeodeOpener.Robot::costs));

		assertEquals(Map.of( //
						Material.ORE, List.of(new GeodeOpener.Cost(4, Material.ORE)), //
						Material.CLAY, List.of(new GeodeOpener.Cost(4, Material.ORE)), //
						Material.OBSIDIAN, List.of(new GeodeOpener.Cost(4, Material.ORE), new GeodeOpener.Cost(15, Material.CLAY)), //
						Material.GEODE, List.of(new GeodeOpener.Cost(3, Material.ORE), new GeodeOpener.Cost(8, Material.OBSIDIAN))), //
				productToCosts);

		final long maxGeodesOpened = blueprint.computeMaxGeodesOpened(24);
		assertEquals(1, maxGeodesOpened);
	}

}
