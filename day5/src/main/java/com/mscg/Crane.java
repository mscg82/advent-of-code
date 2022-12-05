package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Crane(List<Deque<String>> stacks, List<MoveOperation> moves)
{
	public static Crane parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var stacks = new ArrayList<Deque<String>>();
			final var moves = new ArrayList<MoveOperation>();
			Seq.zipWithIndex(StreamUtils.splitted(in.lines(), String::isBlank)) //
					.forEach(idx -> {
						switch (idx.v2().intValue()) {
							case 0 -> parseStacks(idx.v1(), stacks);
							case 1 -> parseOperations(idx.v1(), moves);
							default -> throw new IllegalArgumentException("Too many sections in input");
						}
					});
			return new Crane(stacks, moves);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public String executeMoves()
	{
		for (final var move : moves) {
			for (int i = 0; i < move.amount(); i++) {
				final String value = stacks.get(move.origin()).removeFirst();
				stacks.get(move.destination()).addFirst(value);
			}
		}
		return stacks.stream() //
				.map(Deque::peekFirst) //
				.collect(Collectors.joining());
	}

	public String executeMoves2()
	{
		for (final var move : moves) {
			final Deque<String> pickedUp = new ArrayDeque<>(move.amount());
			for (int i = 0; i < move.amount(); i++) {
				final String value = stacks.get(move.origin()).removeFirst();
				pickedUp.addFirst(value);
			}
			final Deque<String> destination = stacks.get(move.destination());
			pickedUp.forEach(destination::addFirst);
		}
		return stacks.stream() //
				.map(Deque::peekFirst) //
				.collect(Collectors.joining());
	}

	private static void parseStacks(final Stream<String> lines, final List<Deque<String>> stacks)
	{
		final var allLines = lines.toList();
		final var stackIndexes = allLines.get(allLines.size() - 1);
		final var stackLines = allLines.subList(0, allLines.size() - 1);
		final var pattern = Pattern.compile("(\\d+)");
		final var matcher = pattern.matcher(stackIndexes);
		while (matcher.find()) {
			final int position = matcher.start();
			final Deque<String> stack = new ArrayDeque<>();
			stacks.add(stack);
			for (final var stackLine : stackLines) {
				if (position < stackLine.length()) {
					final String value = stackLine.substring(position, position + 1);
					if (!value.isBlank()) {
						stack.add(value);
					}
				}
			}
		}
	}

	private static void parseOperations(final Stream<String> lines, final List<MoveOperation> moves)
	{
		final var pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
		lines.map(pattern::matcher) //
				.filter(Matcher::matches) //
				.map(matcher -> new MoveOperation( //
						Integer.parseInt(matcher.group(2)) - 1, //
						Integer.parseInt(matcher.group(3)) - 1, //
						Integer.parseInt(matcher.group(1)))) //
				.forEach(moves::add);
	}

	record MoveOperation(int origin, int destination, int amount) {}
}
