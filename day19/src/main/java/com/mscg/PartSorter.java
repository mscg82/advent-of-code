package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.Collector;

public record PartSorter(Map<WorkflowId, Workflow> workflows, List<Part> parts)
{

	public static PartSorter parseInput(final BufferedReader in) throws IOException
	{
		try {
			class Parser
			{
				boolean parsingWorkflows = true;

				final Map<WorkflowId, Workflow> workflows = new HashMap<>();

				final List<Part> parts = new ArrayList<>();
			}

			final Parser parser = in.lines().collect(Collector.of(Parser::new, //
					(p, line) -> {
						if (line.isBlank()) {
							p.parsingWorkflows = false;
						} else {
							if (p.parsingWorkflows) {
								final Workflow workflow = Workflow.from(line);
								p.workflows.put(workflow.id(), workflow);
							} else {
								p.parts.add(Part.from(line));
							}
						}
					}, //
					StreamUtils.unsupportedMerger()));
			return new PartSorter(Map.copyOf(parser.workflows), List.copyOf(parser.parts));
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long rateAcceptedParts()
	{
		final var acceptedParts = new ArrayList<Part>(parts.size());
		final var initialWorkflow = workflows.get(new WorkflowId("in"));
		for (final Part part : parts) {
			applyWorkflow(part, initialWorkflow, acceptedParts);
		}
		return acceptedParts.stream() //
				.mapToLong(part -> part.x() + part.m() + part.a() + part.s()) //
				.sum();
	}

	private void applyWorkflow(final Part part, final Workflow workflow, final List<Part> acceptedParts)
	{
		for (final PartRule rule : workflow.rules()) {
			final Optional<Destination> destination = rule.apply(part);
			if (destination.isPresent()) {
				switch (destination.get()) {
					case final WorkflowId next -> applyWorkflow(part, workflows.get(next), acceptedParts);
					case final Rejected _ -> { /* do nothing */}
					case final Accepted _ -> acceptedParts.add(part);
				}
				return;
			}
		}
		switch (workflow.defaultDestination()) {
			case final WorkflowId next -> applyWorkflow(part, workflows.get(next), acceptedParts);
			case final Rejected _ -> { /* do nothing */}
			case final Accepted _ -> acceptedParts.add(part);
		}
	}

	public sealed interface Destination
	{
		static Destination from(final String destination)
		{
			return switch (destination) {
				case "R" -> Rejected.REJECTED;
				case "A" -> Accepted.ACCEPTED;
				default -> new WorkflowId(destination);
			};
		}
	}

	public sealed interface PartFilter extends Predicate<Part>
	{
		static PartFilter from(final String line)
		{
			final var filterPattern = Pattern.compile("^([xmasXMAS])([<>])(\\d+)$");
			final var matcher = filterPattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid part filter '" + line + "'");
			}
			final char functionChar = matcher.group(2).charAt(0);
			return switch (functionChar) {
				case '<' -> new PartFilterLessThan(PartDataExtractor.from(matcher.group(1).charAt(0)),
						Long.parseLong(matcher.group(3)));
				case '>' -> new PartFilterGreaterThan(PartDataExtractor.from(matcher.group(1).charAt(0)),
						Long.parseLong(matcher.group(3)));
				default -> throw new IllegalArgumentException("Invalid part filter '" + line + "'");
			};
		}
	}

	public record Part(long x, long m, long a, long s)
	{
		private static final Pattern PART_PATTERN = Pattern.compile("^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}$");

		public static Part from(final String line)
		{
			final var matcher = PART_PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid part line: '" + line + "'");
			}
			return new Part(Long.parseLong(matcher.group(1)), //
					Long.parseLong(matcher.group(2)), //
					Long.parseLong(matcher.group(3)), //
					Long.parseLong(matcher.group(4)));
		}
	}

	public record WorkflowId(String name) implements Destination {}

	public record PartFilterLessThan(PartDataExtractor extractor, long targetValue) implements PartFilter
	{

		@Override
		public boolean test(final Part part)
		{
			return extractor.applyAsLong(part) < targetValue;
		}
	}

	public record PartFilterGreaterThan(PartDataExtractor extractor, long targetValue) implements PartFilter
	{

		@Override
		public boolean test(final Part part)
		{
			return extractor.applyAsLong(part) > targetValue;
		}
	}

	public record PartRule(PartFilter filter, Destination destination) implements Function<Part, Optional<Destination>>
	{

		public static PartRule from(final String ruleStr)
		{
			final var parts = ruleStr.split(":");
			if (parts.length != 2) {
				throw new IllegalArgumentException("Invalid part rule: '" + ruleStr + "'");
			}
			return new PartRule(PartFilter.from(parts[0]), Destination.from(parts[1]));
		}

		@Override
		public Optional<Destination> apply(final Part part)
		{
			return filter.test(part) ? Optional.of(destination) : Optional.empty();
		}
	}

	public record Workflow(WorkflowId id, List<PartRule> rules, Destination defaultDestination)
	{
		private static final Pattern WORKFLOW_PATTERN = Pattern.compile("^([^{]+)\\{([^}]+)}$");

		public static Workflow from(final String line)
		{
			final var matcher = WORKFLOW_PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid workflow line: '" + line + "'");
			}
			final var rulesParts = matcher.group(2).split(",");
			final var rules = Arrays.stream(rulesParts) //
					.takeWhile(part -> !part.equals(rulesParts[rulesParts.length - 1])) //
					.map(PartRule::from) //
					.toList();
			final var defaultDestination = Destination.from(rulesParts[rulesParts.length - 1]);
			return new Workflow(new WorkflowId(matcher.group(1)), rules, defaultDestination);
		}
	}

	public enum Rejected implements Destination
	{
		REJECTED
	}

	public enum Accepted implements Destination
	{
		ACCEPTED
	}

	public enum PartDataExtractor implements ToLongFunction<Part>
	{
		X, M, A, S;

		public static PartDataExtractor from(final char c)
		{
			return switch (c) {
				case 'x', 'X' -> X;
				case 'm', 'M' -> M;
				case 'a', 'A' -> A;
				case 's', 'S' -> S;
				default -> throw new IllegalArgumentException("Invalid part data extractor '" + c + "'");
			};
		}

		@Override
		public long applyAsLong(final Part value)
		{
			return switch (this) {
				case X -> value.x;
				case M -> value.m;
				case A -> value.a;
				case S -> value.s;
			};
		}
	}
}
