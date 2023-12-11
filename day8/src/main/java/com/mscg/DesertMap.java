package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mscg.utils.StringTemplates.ILLEGAL_ARGUMENT_EXC;

public record DesertMap(List<Direction> directions, Map<String, Node> nodes)
{

	public static DesertMap parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines().toList();
			final String directionsStr = allLines.get(0);
			final List<Direction> directions = directionsStr.codePoints() //
					.mapToObj(c -> Direction.from((char) c)) //
					.toList();

			final var pattern = Pattern.compile("([^ ]+) = \\(([^,]+), (.+)\\)");
			final Map<String, Node> nodes = allLines.stream() //
					.skip(2) //
					.map(StreamUtils.matchOrFail(pattern, line -> STR."Unsupported node format \"\{line}\"")) //
					.map(matcher -> new Node(matcher.group(1), matcher.group(2), matcher.group(3))) //
					.collect(Collectors.toMap(Node::name, Function.identity(), (n1, __) -> n1, LinkedHashMap::new));

			return new DesertMap(directions, Collections.unmodifiableMap(nodes));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long countStepsToDestination()
	{
		return findStepsToEnd(nodes.get("AAA"), node -> node.name.equals("ZZZ")).steps();
	}

	public long countStepsToDestinationForGhosts()
	{
		final List<Node> nodes = this.nodes.values().stream() //
				.filter(node -> node.name().endsWith("A")) //
				.collect(Collectors.toCollection(ArrayList::new));

		final List<Status> finalStatuses = nodes.stream() //
				.map(node -> findStepsToEnd(node, currentNode -> currentNode.name().endsWith("Z"))) //
				.toList();

		return finalStatuses.stream() //
				.mapToLong(Status::steps) //
				.mapToObj(BigInteger::valueOf) //
				.reduce(DesertMap::lcm) //
				.orElseThrow() //
				.longValue();
	}

	private Status findStepsToEnd(final Node initialNode, final Predicate<Node> finalNode)
	{
		return Stream.iterate(new Status(0, initialNode), status -> {
					final int stepIndex = (int) (status.steps % directions.size());
					final var direction = directions.get(stepIndex);
					return new Status(status.steps + 1, switch (direction) {
						case LEFT -> nodes.get(status.currentNode.left());
						case RIGHT -> nodes.get(status.currentNode.right());
					});
				}) //
				.filter(status -> finalNode.test(status.currentNode)) //
				.findFirst() //
				.orElseThrow();
	}

	private static BigInteger lcm(final BigInteger n1, final BigInteger n2)
	{
		final var gcd = n1.gcd(n2);
		return n1.multiply(n2).divide(gcd);
	}

	public record Node(String name, String left, String right) {}

	private record Status(long steps, Node currentNode) {}

	public enum Direction
	{
		LEFT, RIGHT;

		public static Direction from(final char c)
		{
			return switch (c) {
				case 'L' -> LEFT;
				case 'R' -> RIGHT;
				default -> throw ILLEGAL_ARGUMENT_EXC."Invalid direction '\{c}'";
			};
		}
	}

}
