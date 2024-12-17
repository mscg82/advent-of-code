package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChronospatialComputerTest
{

	@Test
	void testExecutionOutputs() throws Exception
	{
		final var computer = ChronospatialComputer.parseInput(InputUtils.readInput("""
				Register A: 729
				Register B: 0
				Register C: 0
				
				Program: 0,1,5,4,3,0"""));
		assertEquals("4,6,3,5,6,3,5,2,1,0", computer.executionOutputs());
	}

}
