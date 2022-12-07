package com.mscg;

import com.mscg.utils.StreamUtils;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record DeviceFileSystem(Directory root)
{
	public static DeviceFileSystem parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<List<String>> navigationInstructions = in.lines() //
					.collect(StreamUtils.splitAt(line -> line.startsWith("$")));

			final Directory root = new Directory(null, "/");
			Directory current = null;
			for (final var instructions : navigationInstructions) {
				final var command = instructions.get(0);
				switch (command.substring(0, 4)) {
					case "$ cd" -> {
						final String folderName = command.substring(5);
						switch (folderName) {
							case "/" -> current = root;
							case ".." -> current = Objects.requireNonNull(current).parent();
							default -> current = Objects.requireNonNull(current).subFolders() //
									.filter(node -> folderName.equals(node.name())) //
									.findFirst() //
									.orElseThrow();
						}
					}

					case "$ ls" -> {
						final var parent = Objects.requireNonNull(current);
						final List<SizedNode> children = instructions.stream() //
								.skip(1) //
								.map(line -> {
									if (line.startsWith("dir ")) {
										return new Directory(parent, line.substring("dir ".length()));
									} else {
										final String[] parts = line.split(" ");
										return new File(parent, parts[1], Long.parseLong(parts[0]));
									}
								}) //
								.toList();
						parent.setChildren(children);
					}

					default -> throw new IllegalArgumentException("Unsupported command");
				}
			}

			computeSizes(root);

			return new DeviceFileSystem(root);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long sumSmallDirectories()
	{
		final Deque<Directory> queue = new ArrayDeque<>();
		queue.add(root);
		final List<Directory> smallDirectories = new ArrayList<>();
		while (!queue.isEmpty()) {
			final var current = queue.pop();
			if (current.size() <= 100_000) {
				smallDirectories.add(current);
			}
			current.subFolders().forEach(queue::add);
		}
		return smallDirectories.stream() //
				.mapToLong(Directory::size) //
				.sum();
	}

	public long getSizeOfFolderToDelete()
	{
		final long totalSize = 70_000_000;
		final long neededSpace = 30_000_000;
		final long freeSpace = totalSize - root().size();
		final Deque<Directory> queue = new ArrayDeque<>();
		queue.add(root);
		final List<Directory> candidatesForDeletion = new ArrayList<>();
		while (!queue.isEmpty()) {
			final var current = queue.pop();
			if (current.size() + freeSpace >= neededSpace) {
				candidatesForDeletion.add(current);
			}
			current.subFolders().forEach(queue::add);
		}
		candidatesForDeletion.sort(Comparator.comparingLong(Directory::size));
		return candidatesForDeletion.get(0).size();
	}

	private static void computeSizes(final Directory root)
	{
		final Deque<Directory> stack = new ArrayDeque<>();
		stack.add(root);
		while (!stack.isEmpty()) {
			final var current = stack.pop();
			final List<Directory> subFoldersWithUnknownSize = current.subFolders() //
					.filter(folder -> folder.size < 0) //
					.toList();
			if (!subFoldersWithUnknownSize.isEmpty()) {
				stack.addFirst(current);
				subFoldersWithUnknownSize.forEach(stack::addFirst);
			} else {
				final long size = current.children().stream() //
						.mapToLong(SizedNode::size) //
						.sum();
				current.setSize(size);
			}
		}
	}

	public sealed interface SizedNode permits Directory, File
	{
		String name();

		long size();
	}

	@RequiredArgsConstructor
	private static final class Directory implements SizedNode
	{
		private final Directory parent;

		private final String name;

		private long size = -1L;

		private List<SizedNode> children = List.of();

		public Directory parent()
		{
			return parent;
		}

		@Override
		public String name()
		{
			return name;
		}

		public void setSize(final long size)
		{
			this.size = size;
		}

		@Override
		public long size()
		{
			return size;
		}

		public void setChildren(final List<SizedNode> children)
		{
			this.children = children == null ? List.of() : List.copyOf(children);
		}

		public List<SizedNode> children()
		{
			return children;
		}

		public Stream<Directory> subFolders()
		{
			return children.stream() //
					.flatMap(node -> switch (node) {
						case Directory d -> Stream.of(d);
						case File __ -> Stream.of();
					});
		}

		@Override
		public String toString()
		{
			return "Directory[parent: %s, name: %s, size: %d, children: %d]".formatted( //
					parent == null ? "N/A" : parent.name(), //
					name, //
					size, //
					children.size() //
			);
		}
	}

	private record File(Directory parent, String name, long size) implements SizedNode
	{
		File
		{
			Objects.requireNonNull(parent, "Parent cannot be null");
		}

		@Override
		public String toString()
		{
			return "File[parent: %s, name: %s, size: %d]".formatted( //
					parent.name(), //
					name, //
					size //
			);
		}
	}
}
