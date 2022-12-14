package com.mscg;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketPairCorrectnessTest
{

	@Test
	void testPair1()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[1,1,3,1,1]", //
				"[1,1,5,1,1]"));
		assertTrue(packetPair.isCorrect());
	}

	@Test
	void testPair2()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[[1],[2,3,4]]", //
				"[[1],4]"));
		assertTrue(packetPair.isCorrect());
	}

	@Test
	void testPair3()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[9]", //
				"[[8,7,6]]"));
		assertFalse(packetPair.isCorrect());
	}

	@Test
	void testPair4()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[[4,4],4,4]", //
				"[[4,4],4,4,4]"));
		assertTrue(packetPair.isCorrect());
	}

	@Test
	void testPair5()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[]", //
				"[3]"));
		assertTrue(packetPair.isCorrect());
	}

	@Test
	void testPair6()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[[[]]]", //
				"[[]]"));
		assertFalse(packetPair.isCorrect());
	}

	@Test
	void testPair7()
	{
		final RadioPacketReader.PacketPair packetPair = RadioPacketReader.PacketPair.from(List.of( //
				"[1,[2,[3,[4,[5,6,7]]]],8,9]", //
				"[1,[2,[3,[4,[5,6,0]]]],8,9]"));
		assertFalse(packetPair.isCorrect());
	}

}
