package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TowelsPatternsTest
{

	@Test
	void testCountPossiblePatterns() throws Exception
	{
		final var towels = TowelsPatterns.parseInput(InputUtils.readInput("""
				r, wr, b, g, bwu, rb, gb, br
				
				brwrr
				bggr
				gbbr
				rrbgbr
				ubwu
				bwurrg
				brgr
				bbrgwb"""));
		assertEquals(6, towels.countPossiblePatterns());
	}

	@Test
	void testCountAllPossiblePatterns() throws Exception
	{
		final var towels = TowelsPatterns.parseInput(InputUtils.readInput("""
				r, wr, b, g, bwu, rb, gb, br
				
				brwrr
				bggr
				gbbr
				rrbgbr
				ubwu
				bwurrg
				brgr
				bbrgwb"""));
		assertEquals(16, towels.countAllPossiblePatterns());
	}

}
