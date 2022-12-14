package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record RadioPacketReader(List<PacketPair> packetPairs)
{
	public static RadioPacketReader parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<PacketPair> packetPairs = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.map(PacketPair::from) //
					.toList();
			return new RadioPacketReader(packetPairs);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumIndicesOfCorrectPackages()
	{
		return Seq.zipWithIndex(packetPairs.stream()) //
				.filter(idx -> idx.v1().isCorrect()) //
				.mapToLong(idx -> idx.v2() + 1L) //
				.sum();
	}

	public long getDecodeKey()
	{
		final Packet separator1 = Packet.from("[[2]]");
		final Packet separator2 = Packet.from("[[6]]");
		final List<Packet> sortedPackages = Stream.concat( //
						Stream.of(separator1, separator2), //
						packetPairs.stream().flatMap(PacketPair::stream)) //
				.sorted() //
				.toList();
		final long idx1 = Collections.binarySearch(sortedPackages, separator1) + 1L;
		final long idx2 = Collections.binarySearch(sortedPackages, separator2) + 1L;

		return idx1 * idx2;
	}

	public sealed interface Packet extends Comparable<Packet> permits ListPacket, ValuePacket
	{
		@SuppressWarnings("java:S1119")
		static Packet from(final String line)
		{
			final Deque<Packet> stack = new ArrayDeque<>();
			int i = 0;
			final int l = line.length();
			parsingLoop:
			while (i < l) {
				final char c = line.charAt(i);
				switch (c) {
					case ',' -> {
						if (stack.size() < 2) {
							break parsingLoop;
						} else {
							popIntoHead(stack);
						}
					}
					case '[' -> stack.addFirst(new ListPacket(new ArrayList<>()));
					case ']' -> {
						if (stack.isEmpty()) {
							throw new IllegalStateException("Invalid list close");
						}
						final char prevChar = line.charAt(i - 1);
						if (prevChar != '[') {
							popIntoHead(stack);
						}
					}
					case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
						int j = i + 1;
						while (j < l && Character.isDigit(line.charAt(j))) {
							j++;
						}
						stack.addFirst(new ValuePacket(Long.parseLong(line.substring(i, j))));
						i = j - 1;
					}
					default -> throw new IllegalArgumentException("Unsupported character " + c + " in packet at position " + i);
				}
				i++;
			}

			if (stack.size() != 1) {
				throw new IllegalStateException("Incomplete value at position " + i);
			}
			if (i != l) {
				throw new IllegalStateException("Invalid character at position " + i);
			}
			return stack.pop();
		}

		private static void popIntoHead(final Deque<Packet> stack)
		{
			final Packet packet = stack.pop();
			if (!(stack.peek() instanceof ListPacket head)) {
				throw new IllegalStateException("Head of stack must be a list to store closed value");
			}
			head.packets().add(packet);
		}

	}

	public record PacketPair(Packet first, Packet second)
	{
		public static PacketPair from(final List<String> lines)
		{
			return new PacketPair(Packet.from(lines.get(0)), Packet.from(lines.get(1)));
		}

		public boolean isCorrect()
		{
			return first.compareTo(second) < 0;
		}

		public Stream<Packet> stream()
		{
			return Stream.of(first, second);
		}
	}

	public record ValuePacket(long value) implements Packet
	{
		public ListPacket asList()
		{
			return new ListPacket(List.of(this));
		}

		@Override
		public String toString()
		{
			return String.valueOf(value);
		}

		@Override
		public int compareTo(final Packet other)
		{
			return switch (other) {
				case ListPacket list -> this.asList().compareTo(list);
				case ValuePacket(long otherValue) -> Long.compare(value, otherValue);
			};
		}
	}

	public record ListPacket(List<Packet> packets) implements Packet
	{
		@Override
		public String toString()
		{
			return packets.stream() //
					.map(Packet::toString) //
					.collect(Collectors.joining(",", "[", "]"));
		}

		@Override
		public int compareTo(final Packet other)
		{
			return switch (other) {
				case ValuePacket value -> this.compareTo(value.asList());
				case ListPacket(List<Packet> otherPackets) -> {
					final int size = packets.size();
					final int otherSize = otherPackets.size();
					int i = 0;
					while (i < size && i < otherSize) {
						final int compare = packets.get(i).compareTo(otherPackets.get(i));
						if (compare != 0) {
							yield compare;
						}
						i++;
					}
					if (i == size && i == otherSize) {
						yield 0;
					} else if (i == size) {
						yield -1;
					}
					yield 1;
				}
			};
		}
	}
}
