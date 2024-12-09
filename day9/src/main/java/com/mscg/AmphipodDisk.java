package com.mscg;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public record AmphipodDisk(IntList descriptors)
{

	public static AmphipodDisk parseInput(final BufferedReader in) throws IOException
	{
		try {
			final IntList descriptors = IntImmutableList.of(in.lines() //
					.flatMapToInt(line -> line.chars().map(c -> c - '0')) //
					.toArray());
			return new AmphipodDisk(descriptors);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long simpleDefragAndComputeChecksum()
	{
		final IntList diskStructure = buildDiskStructure();

		int writePos = moveForwardToNextEmptyPosition(diskStructure, 0);
		int readPos = moveBackwardToPreviousFullPosition(diskStructure, diskStructure.size() - 1);

		while (writePos < readPos) {
			final int blockId = diskStructure.getInt(readPos);
			diskStructure.set(readPos, -1);
			diskStructure.set(writePos, blockId);
			writePos = moveForwardToNextEmptyPosition(diskStructure, writePos);
			readPos = moveBackwardToPreviousFullPosition(diskStructure, readPos);
		}

		return computeChecksum(diskStructure);
	}

	public long defragAndComputeChecksum()
	{
		final IntList diskStructure = buildDiskStructure();

		record FileDescriptor(int size, int offset) {}
		final var fileIdToDescriptor = new Int2ObjectOpenHashMap<FileDescriptor>();
		int maxFileId = 0;
		for (int i = 0, l = diskStructure.size(); i < l; i++) {
			final int blockId = diskStructure.getInt(i);
			if (blockId == -1) {
				continue;
			}
			final int offset = i;
			int size = 0;
			while (i < l && diskStructure.getInt(i) == blockId) {
				size++;
				i++;
			}
			i--;
			fileIdToDescriptor.put(blockId, new FileDescriptor(size, offset));
			maxFileId = Math.max(maxFileId, blockId);
		}

		for (int fileId = maxFileId; fileId >= 1; fileId--) {
			final FileDescriptor fileDescriptor = fileIdToDescriptor.get(fileId);
			for (int writePos = 0; writePos < fileDescriptor.offset(); writePos++) {
				if (diskStructure.getInt(writePos) != -1) {
					continue;
				}
				final int emptyBlockSize = computeEmptyBlockSize(diskStructure, writePos);
				if (emptyBlockSize >= fileDescriptor.size()) {
					for (int i = 0; i < fileDescriptor.size(); i++) {
						diskStructure.set(writePos + i, fileId);
						diskStructure.set(fileDescriptor.offset() + i, -1);
					}
					break;
				}
			}
		}

		return computeChecksum(diskStructure);
	}

	private IntList buildDiskStructure()
	{
		final int maxSize = descriptors.intStream().sum();
		final IntList diskStructure = new IntArrayList(maxSize);
		for (int i = 0, l = descriptors.size(); i < l; i++) {
			final int blockId = i / 2;
			final boolean emptySpace = blockId * 2 != i;
			for (int j = 0, blocks = descriptors.getInt(i); j < blocks; j++) {
				diskStructure.add(emptySpace ? -1 : blockId);
			}
		}
		return diskStructure;
	}

	private static long computeChecksum(final IntList diskStructure)
	{
		long checksum = 0L;
		for (int i = 0, l = diskStructure.size(); i < l; ++i) {
			final long blockId = diskStructure.getInt(i);
			if (blockId < 0) {
				continue;
			}
			checksum += blockId * i;
		}
		return checksum;
	}

	private static int computeEmptyBlockSize(final IntList diskStructure, final int offset)
	{
		int emptyBlockSize = 0;
		for (int i = offset, l = diskStructure.size(); i < l; i++) {
			final int blockId = diskStructure.getInt(i);
			if (blockId != -1) {
				break;
			}
			emptyBlockSize++;
		}
		return emptyBlockSize;
	}

	private static int moveForwardToNextEmptyPosition(final IntList diskStructure, final int current)
	{
		for (int i = current, l = diskStructure.size() - 1; i < l; i++) {
			final int blockId = diskStructure.getInt(i);
			if (blockId == -1) {
				return i;
			}
		}
		return -1;
	}

	private static int moveBackwardToPreviousFullPosition(final IntList diskStructure, final int current)
	{
		for (int i = current; i >= 0; i--) {
			final int blockId = diskStructure.getInt(i);
			if (blockId != -1) {
				return i;
			}
		}
		return -1;
	}

}
