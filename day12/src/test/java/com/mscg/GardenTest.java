package com.mscg;

import com.mscg.utils.InputUtils;
import com.msg.Garden;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GardenTest
{

	@Test
	void testComputeFenceCost1() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				AAAA
				BBCD
				BBCC
				EEEC"""));
		assertEquals(140, garden.computeFenceCost());
	}

	@Test
	void testComputeFenceCost2() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				OOOOO
				OXOXO
				OOOOO
				OXOXO
				OOOOO"""));
		assertEquals(772, garden.computeFenceCost());
	}

	@Test
	void testComputeFenceCost3() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				RRRRIICCFF
				RRRRIICCCF
				VVRRRCCFFF
				VVRCCCJFFF
				VVVVCJJCFE
				VVIVCCJJEE
				VVIIICJJEE
				MIIIIIJJEE
				MIIISIJEEE
				MMMISSJEEE"""));
		assertEquals(1930, garden.computeFenceCost());
	}

	@Test
	void testComputeFenceDiscountedCost1() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				AAAA
				BBCD
				BBCC
				EEEC"""));
		assertEquals(80, garden.computeFenceDiscountedCost());
	}

	@Test
	void testComputeFenceDiscountedCost2() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				EEEEE
				EXXXX
				EEEEE
				EXXXX
				EEEEE"""));
		assertEquals(236, garden.computeFenceDiscountedCost());
	}

	@Test
	void testComputeFenceDiscountedCost3() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				AAAAAA
				AAABBA
				AAABBA
				ABBAAA
				ABBAAA
				AAAAAA"""));
		assertEquals(368, garden.computeFenceDiscountedCost());
	}

	@Test
	void testComputeFenceDiscountedCost4() throws Exception
	{
		final var garden = Garden.parseInput(InputUtils.readInput("""
				RRRRIICCFF
				RRRRIICCCF
				VVRRRCCFFF
				VVRCCCJFFF
				VVVVCJJCFE
				VVIVCCJJEE
				VVIIICJJEE
				MIIIIIJJEE
				MIIISIJEEE
				MMMISSJEEE"""));
		assertEquals(1206, garden.computeFenceDiscountedCost());
	}

}
