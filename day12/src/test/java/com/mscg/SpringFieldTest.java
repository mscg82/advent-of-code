package com.mscg;

import com.msg.SpringField;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static com.msg.SpringField.expandSpringRow;
import static com.msg.SpringField.parseSpringRow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringFieldTest
{

	@Test
	void testCountArrangements1() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				.???.### 1,1,3""")));
		assertEquals(1, springField.sumAllArrangements());
	}

	@Test
	void testCountArrangements2() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				.??..??...?##. 1,1,3""")));
		assertEquals(4, springField.sumAllArrangements());
	}

	@Test
	void testCountArrangements3() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				?###???????? 3,2,1""")));
		assertEquals(10, springField.sumAllArrangements());
	}

	@Test
	void testSumArrangements() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				???.### 1,1,3
				.??..??...?##. 1,1,3
				?#?#?#?#?#?#?#? 1,3,1,6
				????.#...#... 4,1,1
				????.######..#####. 1,6,5
				?###???????? 3,2,1""")));
		assertEquals(21, springField.sumAllArrangements());
	}

	@Test
	void testRowExpansion()
	{
		assertEquals(".#?.#?.#?.#?.# 1,1,1,1,1", expandSpringRow(parseSpringRow(".# 1")).toString());
		assertEquals("???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3",
				expandSpringRow(parseSpringRow("???.### 1,1,3")).toString());
	}

	@Test
	void testCountUnfoldedArrangements1() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				.???.### 1,1,3""")));
		assertEquals(1, springField.sumAllUnfoldedArrangements());
	}

	@Test
	void testCountUnfoldedArrangements2() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				.??..??...?##. 1,1,3""")));
		assertEquals(16384, springField.sumAllUnfoldedArrangements());
	}

	@Test
	void testCountUnfoldedArrangements3() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				?###???????? 3,2,1""")));
		assertEquals(506250, springField.sumAllUnfoldedArrangements());
	}

	@Test
	void testSumUnfoldedArrangements() throws Exception
	{
		final var springField = SpringField.parseInput(new BufferedReader(new StringReader("""
				???.### 1,1,3
				.??..??...?##. 1,1,3
				?#?#?#?#?#?#?#? 1,3,1,6
				????.#...#... 4,1,1
				????.######..#####. 1,6,5
				?###???????? 3,2,1""")));
		assertEquals(525152, springField.sumAllUnfoldedArrangements());
	}
}
