package com.mscg;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LavaFactoryInitializerTest
{

	@Test
	void testHash()
	{
		assertEquals(52, LavaFactoryInitializer.hash("HASH"));

		assertEquals(30, LavaFactoryInitializer.hash("rn=1"));
		assertEquals(253, LavaFactoryInitializer.hash("cm-"));
		assertEquals(97, LavaFactoryInitializer.hash("qp=3"));
		assertEquals(47, LavaFactoryInitializer.hash("cm=2"));
		assertEquals(14, LavaFactoryInitializer.hash("qp-"));
		assertEquals(180, LavaFactoryInitializer.hash("pc=4"));
		assertEquals(9, LavaFactoryInitializer.hash("ot=9"));
		assertEquals(197, LavaFactoryInitializer.hash("ab=5"));
		assertEquals(48, LavaFactoryInitializer.hash("pc-"));
		assertEquals(214, LavaFactoryInitializer.hash("pc=6"));
		assertEquals(231, LavaFactoryInitializer.hash("ot=7"));
	}

	@Test
	void testComputeFocusingPower() throws Exception
	{
		final var factory = LavaFactoryInitializer.parseInput(
				new BufferedReader(new StringReader("rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7")));
		assertEquals(145, factory.computeFocusingPower());
	}

}
