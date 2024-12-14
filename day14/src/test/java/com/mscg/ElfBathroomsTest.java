package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElfBathroomsTest
{

	@Test
	void testParse() throws Exception
	{
		final var bathrooms = ElfBathrooms.parseInput(InputUtils.readInput("""
				p=0,4 v=3,-3
				p=6,3 v=-1,-3
				p=10,3 v=-1,2
				p=2,0 v=2,-1
				p=0,0 v=1,3
				p=3,0 v=-2,-2
				p=7,6 v=-1,-3
				p=3,0 v=-1,-2
				p=9,3 v=2,3
				p=7,3 v=-1,2
				p=2,4 v=2,-3
				p=9,5 v=-3,-3"""));

		assertEquals("""
				1.12.......
				...........
				...........
				......11.11
				1.1........
				.........1.
				.......1...""", bathrooms.toVisualizationString(7, 11));
	}

	@Test
	void testEvolve100Steps() throws Exception
	{
		final var bathrooms = ElfBathrooms.parseInput(InputUtils.readInput("""
				p=0,4 v=3,-3
				p=6,3 v=-1,-3
				p=10,3 v=-1,2
				p=2,0 v=2,-1
				p=0,0 v=1,3
				p=3,0 v=-2,-2
				p=7,6 v=-1,-3
				p=3,0 v=-1,-2
				p=9,3 v=2,3
				p=7,3 v=-1,2
				p=2,4 v=2,-3
				p=9,5 v=-3,-3"""));

		final var evolvedBathrooms = bathrooms.evolve(100, 7, 11);

		assertEquals("""
				......2..1.
				...........
				1..........
				.11........
				.....1.....
				...12......
				.1....1....""", evolvedBathrooms.toVisualizationString(7, 11));
	}

	@Test
	void testSecurityFactor() throws Exception
	{
		final var bathrooms = ElfBathrooms.parseInput(InputUtils.readInput("""
				p=0,4 v=3,-3
				p=6,3 v=-1,-3
				p=10,3 v=-1,2
				p=2,0 v=2,-1
				p=0,0 v=1,3
				p=3,0 v=-2,-2
				p=7,6 v=-1,-3
				p=3,0 v=-1,-2
				p=9,3 v=2,3
				p=7,3 v=-1,2
				p=2,4 v=2,-3
				p=9,5 v=-3,-3"""));

		assertEquals(12, bathrooms.computeSecurityFactorAfter100Steps(7, 11));
	}

}
