package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public record BitsParser(BitSet bits, int length)
{

	public static BitsParser parseInput(final BufferedReader in) throws IOException
	{
		final String line = in.readLine();

		final int totalBits = line.length() * 4;
		final BitSet bits = new BitSet(totalBits);
		for (int i = 0, l = line.length(); i < l; i++) {
			final String binaryStr = Integer.toString(Integer.parseInt(line.substring(i, i + 1), 16), 2);
			final int offset = binaryStr.length() - 4;
			for (int j = 0; j < 4; j++) {
				if (offset + j >= 0 && binaryStr.charAt(offset + j) == '1') {
					bits.set(totalBits - 1 - (i * 4 + j));
				}
			}
		}

		return new BitsParser(bits, totalBits);
	}

	public long sumAllVersions()
	{
		final var packet = parsePacket(0, true);

		final Deque<Packet> queue = new LinkedList<>();
		queue.add(packet.packet());

		long sum = 0;
		while (!queue.isEmpty()) {
			final var currentPacket = queue.pop();
			sum += currentPacket.getVersion();
			if (currentPacket instanceof OperatorPacket op) {
				queue.addAll(op.getSubPackets());
			}
		}

		return sum;
	}

	public long evaluate()
	{
		final var packet = parsePacket(0, true);

		return packet.packet().getValue();
	}

	private PacketWithIndex parsePacket(final int offset, final boolean pad)
	{
		final Packet ret;

		final byte version = (byte) getValue(offset, offset + 3);
		final byte type = (byte) getValue(offset + 3, offset + 6);

		int index = offset + 6;
		if (type == Packet.LITERAL_TYPE) {
			long value = 0;
			while (true) {
				final long part = getValue(index, index + 5);
				index += 5;
				final long payload = part & 0xf;
				value = (value << 4) + payload;
				if (part == payload) {
					break;
				}
			}

			ret = new LiteralPacket(version, type, value);
		} else {
			final boolean opMode = get(index);
			index++;
			final List<Packet> subPackets;
			if (opMode) {
				// read the number of sub-packages
				final int subPacketsNum = (int) getValue(index, index + 11);
				index += 11;
				subPackets = new ArrayList<>(subPacketsNum);
				for (int i = 0; i < subPacketsNum; i++) {
					final var subPacket = parsePacket(index, false);
					index = subPacket.index();
					subPackets.add(subPacket.packet());
				}
			} else {
				subPackets = new ArrayList<>();
				// read the total size of subpackets
				final int subsize = (int) getValue(index, index + 15);
				index += 15;
				final int endIndex = index + subsize;
				while (index < endIndex) {
					final PacketWithIndex subPacket = parsePacket(index, false);
					index = subPacket.index();
					subPackets.add(subPacket.packet());
				}
			}

			ret = new OperatorPacket(version, type, List.copyOf(subPackets));
		}

		if (pad) {
			while (index % 4 != 0) {
				index++;
			}
		}

		return new PacketWithIndex(ret, index);
	}

	private boolean get(final int index)
	{
		return bits.get(length - 1 - index);
	}

	private long getValue(final int begin, final int end)
	{
		final BitSet subBits = bits.get(length - end, length - begin);
		if (subBits.length() == 0) {
			return 0;
		}
		return subBits.toLongArray()[0];
	}

	private record PacketWithIndex(Packet packet, int index)
	{
	}

	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	@ToString
	private abstract static sealed class Packet permits LiteralPacket,OperatorPacket
	{
		public static final byte LITERAL_TYPE = 4;

		protected final byte version;

		protected final byte type;

		public abstract long getValue();
	}

	@Getter
	@EqualsAndHashCode(callSuper = true)
	@ToString
	private static final class LiteralPacket extends Packet
	{
		private final long value;

		public LiteralPacket(final byte version, final byte type, final long value) {
			super(version, type);
			this.value = value;
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = true)
	@ToString
	private static final class OperatorPacket extends Packet
	{
		private final List<Packet> subPackets;

		public OperatorPacket(final byte version, final byte type, final List<Packet> subPackets) {
			super(version, type);
			this.subPackets = subPackets;
		}

		@Override
		public long getValue()
		{
			return switch (type) {
				case 0 -> subPackets.stream().mapToLong(Packet::getValue).sum();
				case 1 -> subPackets.stream().mapToLong(Packet::getValue).reduce(1, (v1, v2) -> v1 * v2);
				case 2 -> subPackets.stream().mapToLong(Packet::getValue).min().orElseThrow();
				case 3 -> subPackets.stream().mapToLong(Packet::getValue).max().orElseThrow();
				case 5 -> subPackets.get(0).getValue() > subPackets.get(1).getValue() ? 1L : 0L;
				case 6 -> subPackets.get(0).getValue() < subPackets.get(1).getValue() ? 1L : 0L;
				case 7 -> subPackets.get(0).getValue() == subPackets.get(1).getValue() ? 1L : 0L;
				default -> throw new IllegalArgumentException("Unsupported operator of type " + type);
			};
		}
	}
}
