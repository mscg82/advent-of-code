package com.mscg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PacketParserTest
{

	@Test
	void parseValue1()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("3");
		final RadioPacketReader.ValuePacket valuePacket = assertInstanceOf(RadioPacketReader.ValuePacket.class, packet);
		assertNotNull(valuePacket);
		assertEquals(3, valuePacket.value());
	}

	@Test
	void parseValue2()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("123");
		final RadioPacketReader.ValuePacket valuePacket = assertInstanceOf(RadioPacketReader.ValuePacket.class, packet);
		assertNotNull(valuePacket);
		assertEquals(123, valuePacket.value());
	}

	@Test
	void parseEmptyList()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[]", listPacket.toString());
	}

	@Test
	void parseNestedEmptyList()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[[]]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[[]]", listPacket.toString());
	}

	@Test
	void parseNestedEmptyList2()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[[[]]]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[[[]]]", listPacket.toString());
	}

	@Test
	void parseList()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[1,2,3]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[1,2,3]", listPacket.toString());
	}

	@Test
	void parseNestedList()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[[1],[2,3,4]]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[[1],[2,3,4]]", listPacket.toString());
	}

	@Test
	void parseNestedList2()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[[4,4],4,4,4]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[[4,4],4,4,4]", listPacket.toString());
	}

	@Test
	void parseNestedList3()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from("[1,[2,[3,[4,[5,6,7]]]],8,9]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals("[1,[2,[3,[4,[5,6,7]]]],8,9]", listPacket.toString());
	}

	@Test
	void parseNestedList4()
	{
		final RadioPacketReader.Packet packet = RadioPacketReader.Packet.from(
				"[[[7,[6,4,3],4,[5,5,0]],[9,2],5],[[0,[3]],[[2,3,2,10,8],10,8,[2,7,6]],5,[[0,8,6,10],[2],[8,7,6,4,9]],10],[7,9,1,0,[]],[[[8]],[8,0,4,[8],[1]],4,[[9],4]]]");
		final RadioPacketReader.ListPacket listPacket = assertInstanceOf(RadioPacketReader.ListPacket.class, packet);
		assertNotNull(listPacket);
		assertEquals(
				"[[[7,[6,4,3],4,[5,5,0]],[9,2],5],[[0,[3]],[[2,3,2,10,8],10,8,[2,7,6]],5,[[0,8,6,10],[2],[8,7,6,4,9]],10],[7,9,1,0,[]],[[[8]],[8,0,4,[8],[1]],4,[[9],4]]]",
				listPacket.toString());
	}

	@Test
	void parseInvalid1()
	{
		final IllegalStateException exc = assertThrows(IllegalStateException.class, () -> RadioPacketReader.Packet.from("]"));
		assertEquals("Invalid list close", exc.getMessage());
	}

	@Test
	void parseInvalid2()
	{
		final IllegalStateException exc = assertThrows(IllegalStateException.class, () -> RadioPacketReader.Packet.from("1,]"));
		assertEquals("Invalid character at position 1", exc.getMessage());
	}

	@Test
	void parseInvalid3()
	{
		final IllegalStateException exc = assertThrows(IllegalStateException.class, () -> RadioPacketReader.Packet.from("[1,2"));
		assertEquals("Incomplete value at position 4", exc.getMessage());
	}

	@Test
	void parseInvalid4()
	{
		final IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
				() -> RadioPacketReader.Packet.from("[1,2a"));
		assertEquals("Unsupported character a in packet at position 4", exc.getMessage());
	}

}
