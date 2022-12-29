package com.mscg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PyroclasticFlowTest
{

	@Test
	void testDraw1()
	{
		final PyroclasticFlow.Rock rock = PyroclasticFlow.Rock.spawn(0, PyroclasticFlow.RockType.HOR);
		assertEquals("####", rock.toString());
	}

	@Test
	void testDraw2()
	{
		final PyroclasticFlow.Rock rock = PyroclasticFlow.Rock.spawn(0, PyroclasticFlow.RockType.CROSS);
		assertEquals("""
				.#.
				###
				.#.""", rock.toString());
	}

	@Test
	void testDraw3()
	{
		final PyroclasticFlow.Rock rock = PyroclasticFlow.Rock.spawn(0, PyroclasticFlow.RockType.L);
		assertEquals("""
				..#
				..#
				###""", rock.toString());
	}

	@Test
	void testDraw4()
	{
		final PyroclasticFlow.Rock rock = PyroclasticFlow.Rock.spawn(0, PyroclasticFlow.RockType.VER);
		assertEquals("""
				#
				#
				#
				#""", rock.toString());
	}

	@Test
	void testDraw5()
	{
		final PyroclasticFlow.Rock rock = PyroclasticFlow.Rock.spawn(0, PyroclasticFlow.RockType.SQUARE);
		assertEquals("""
				##
				##""", rock.toString());
	}

}
