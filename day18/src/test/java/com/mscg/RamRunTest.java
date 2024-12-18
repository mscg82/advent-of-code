package com.mscg;

import com.mscg.utils.InputUtils;
import com.mscg.utils.Position8Bits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RamRunTest
{

	@Test
	void testFindMinPathToExit() throws Exception
	{
		final var ramRun = RamRun.parseInput(InputUtils.readInput("""
				5,4
				4,2
				4,5
				3,0
				2,1
				6,3
				2,4
				1,5
				0,6
				3,3
				2,6
				5,1
				1,2
				5,5
				2,5
				6,5
				1,4
				0,4
				6,4
				1,1
				6,1
				1,0
				0,5
				1,6
				2,0"""));
		assertEquals(22, ramRun.findMinPathToExit(12, 6, 6));
	}

	@Test
	void testFindFirstBlockingByte() throws Exception
	{
		final var ramRun = RamRun.parseInput(InputUtils.readInput("""
				5,4
				4,2
				4,5
				3,0
				2,1
				6,3
				2,4
				1,5
				0,6
				3,3
				2,6
				5,1
				1,2
				5,5
				2,5
				6,5
				1,4
				0,4
				6,4
				1,1
				6,1
				1,0
				0,5
				1,6
				2,0"""));
		assertEquals(new Position8Bits(6, 1), ramRun.findFirstBlockingByte(6, 6));
	}

}
