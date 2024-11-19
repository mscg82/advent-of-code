package com.mscg;

import com.mscg.PartSorter.Range;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartSorterTest
{

	@Test
	void testCountAllValidParts() throws Exception
	{
		final var partSorter = PartSorter.parseInput(new BufferedReader(new StringReader("""
				px{a<2006:qkq,m>2090:A,rfg}
				pv{a>1716:R,A}
				lnx{m>1548:A,A}
				rfg{s<537:gd,x>2440:R,A}
				qs{s>3448:A,lnx}
				qkq{x<1416:A,crn}
				crn{x>2662:A,R}
				in{s<1351:px,qqz}
				qqz{s>2770:qs,m<1801:hdj,R}
				gd{a>3333:R,R}
				hdj{m>838:A,pv}
				
				{x=787,m=2655,a=1222,s=2876}
				{x=1679,m=44,a=2067,s=496}
				{x=2036,m=264,a=79,s=2244}
				{x=2461,m=1339,a=466,s=291}
				{x=2127,m=1623,a=2188,s=1013}""")));

		assertEquals(167409079868000L, partSorter.countAllValidParts());
	}

	@Test
	void testSplitInNonIntersectingRanges()
	{
		// same range
		assertEquals(List.of(new Range(1, 10)), PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(1, 10)));

		// no intersection
		assertEquals(List.of(new Range(1, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(6, 10)));
		assertEquals(List.of(new Range(1, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(6, 10), new Range(1, 5)));

		// second contained in first, no touching
		assertEquals(List.of(new Range(1, 1), new Range(2, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(2, 5)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(3, 5)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 9), new Range(10, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(3, 9)));

		// second contained in first, touching
		assertEquals(List.of(new Range(1, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(1, 5)));
		assertEquals(List.of(new Range(1, 4), new Range(5, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 10), new Range(5, 10)));

		// first contained in second, no touching
		assertEquals(List.of(new Range(1, 1), new Range(2, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(2, 5), new Range(1, 10)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(3, 5), new Range(1, 10)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 9), new Range(10, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(3, 9), new Range(1, 10)));

		// first contained in second, touching
		assertEquals(List.of(new Range(1, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(1, 10)));
		assertEquals(List.of(new Range(1, 4), new Range(5, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(5, 10), new Range(1, 10)));

		// intersecting, first before second
		assertEquals(List.of(new Range(1, 1), new Range(2, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(2, 10)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(3, 10)));
		assertEquals(List.of(new Range(1, 3), new Range(4, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(4, 10)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 9), new Range(10, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 9), new Range(3, 10)));
		assertEquals(List.of(new Range(1, 4), new Range(5, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(1, 5), new Range(5, 10)));

		// intersecting, second before first
		assertEquals(List.of(new Range(1, 1), new Range(2, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(2, 10), new Range(1, 5)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(3, 10), new Range(1, 5)));
		assertEquals(List.of(new Range(1, 3), new Range(4, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(4, 10), new Range(1, 5)));
		assertEquals(List.of(new Range(1, 2), new Range(3, 9), new Range(10, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(3, 10), new Range(1, 9)));
		assertEquals(List.of(new Range(1, 4), new Range(5, 5), new Range(6, 10)),
				PartSorter.splitInNonIntersectingRanges(new Range(5, 10), new Range(1, 5)));
	}

}
