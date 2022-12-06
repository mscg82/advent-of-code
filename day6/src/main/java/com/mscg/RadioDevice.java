package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;

public record RadioDevice(String signal)
{
	public static RadioDevice parseInput(final BufferedReader in) throws IOException
	{
		return new RadioDevice(in.readLine());
	}

	public int findStartPacket()
	{
		return findPacketBoundary(4, "No start packet found");
	}

	public int findStartOfMessagePacket()
	{
		return findPacketBoundary(14, "No start of message packet found");
	}

	private int findPacketBoundary(final int packetLength, final String noPacketFoundErrorMessage)
	{
		for (int i = packetLength; i <= signal.length(); i++) {
			final long distinctChars = signal.substring(i - packetLength, i).chars() //
					.distinct() //
					.count();
			if (distinctChars == packetLength) {
				return i;
			}
		}
		throw new IllegalArgumentException(noPacketFoundErrorMessage);
	}
}
