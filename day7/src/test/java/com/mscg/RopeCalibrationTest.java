package com.mscg;

import com.mscg.utils.InputUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RopeCalibrationTest
{

	@Test
	void testFindCalibrationResult() throws Exception
	{
		final var ropeCalibration = RopeCalibration.parseInput(InputUtils.readInput("""
				190: 10 19
				3267: 81 40 27
				83: 17 5
				156: 15 6
				7290: 6 8 6 15
				161011: 16 10 13
				192: 17 8 14
				21037: 9 7 18 13
				292: 11 6 16 20"""));
		assertEquals(3749, ropeCalibration.findCalibrationResult());
	}

	@Test
	void testFindExtendedCalibrationResult() throws Exception
	{
		final var ropeCalibration = RopeCalibration.parseInput(InputUtils.readInput("""
				190: 10 19
				3267: 81 40 27
				83: 17 5
				156: 15 6
				7290: 6 8 6 15
				161011: 16 10 13
				192: 17 8 14
				21037: 9 7 18 13
				292: 11 6 16 20"""));
		assertEquals(11387, ropeCalibration.findExtendedCalibrationResult());
	}

}
