package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record MonkeyNumbers(Map<String, Expression> monkeys)
{
	public static MonkeyNumbers parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Map<String, Expression> monkeys = in.lines() //
					.map(line -> {
						final String[] parts = line.split(":");
						final var exp = Expression.from(parts[1].trim());
						return Map.entry(parts[0].trim(), exp);
					}) //
					.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
			return new MonkeyNumbers(monkeys);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeFor(final String monkeyName)
	{
		return monkeys.get(monkeyName).compute(monkeys::get);
	}

	public long computeValueForEquality()
	{
		if (!(monkeys.get("root") instanceof Calculation root)) {
			throw new IllegalStateException("Root must be an operation");
		}
		final var left = monkeys.get(root.left());
		final var right = monkeys.get(root.right());

		final Map<String, Expression> fixedMonkeys = new HashMap<>(monkeys);
		fixedMonkeys.put("humn", new Value(0));
		final long leftProbe1 = left.compute(fixedMonkeys::get);
		final long rigthProbe1 = right.compute(fixedMonkeys::get);

		final long probeHuman = 1000;
		fixedMonkeys.put("humn", new Value(probeHuman));
		final long leftProbe2 = left.compute(fixedMonkeys::get);
		final long rigthProbe2 = right.compute(fixedMonkeys::get);

		final long targetValue;
		final long probeValue;
		final Expression expr;
		final boolean increasing;
		if (leftProbe1 == leftProbe2) {
			targetValue = leftProbe2;
			probeValue = rigthProbe2;
			expr = right;
			increasing = rigthProbe1 < rigthProbe2;
		} else if (rigthProbe1 == rigthProbe2) {
			targetValue = rigthProbe2;
			probeValue = leftProbe2;
			expr = left;
			increasing = leftProbe1 < leftProbe2;
		} else {
			throw new IllegalStateException("No side is constant");
		}

		final var bounds = findBounds(increasing, probeValue, targetValue, probeHuman, expr, fixedMonkeys);
		long lowerBound = bounds.lowerBound();
		long upperBound = bounds.upperBound();
		while (upperBound - lowerBound > 1) {
			final long midPoint = (upperBound - lowerBound) / 2 + lowerBound;
			fixedMonkeys.put("humn", new Value(midPoint));
			final long newValue = expr.compute(fixedMonkeys::get);
			if (increasing) {
				if (newValue < targetValue) {
					lowerBound = midPoint;
				} else {
					upperBound = midPoint;
				}
			} else {
				if (newValue > targetValue) {
					lowerBound = midPoint;
				} else {
					upperBound = midPoint;
				}
			}
		}

		fixedMonkeys.put("humn", new Value(lowerBound));
		final long value = expr.compute(fixedMonkeys::get);
		if (value == targetValue) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	private Bounds findBounds(final boolean increasing, final long probeValue, final long targetValue, final long probeHuman,
			final Expression expr, final Map<String, Expression> fixedMonkeys)
	{
		long lowerBound;
		long upperBound;
		if (increasing) {
			if (probeValue < targetValue) {
				lowerBound = probeHuman;
				upperBound = 2 * probeHuman;
				boolean found = false;
				for (int i = 0; i < 1000 && !found; i++) {
					fixedMonkeys.put("humn", new Value(upperBound));
					final long newValue = expr.compute(fixedMonkeys::get);
					if (newValue >= targetValue) {
						found = true;
					} else {
						upperBound = 2 * upperBound;
					}
				}
				if (!found) {
					throw new IllegalStateException("Can't find upper bound");
				}
			} else {
				upperBound = probeHuman;
				lowerBound = upperBound / 2;
				boolean found = false;
				for (int i = 0; i < 1000 && !found; i++) {
					fixedMonkeys.put("humn", new Value(lowerBound));
					final long newValue = expr.compute(fixedMonkeys::get);
					if (newValue <= targetValue) {
						found = true;
					} else {
						lowerBound = lowerBound / 2;
					}
				}
				if (!found) {
					throw new IllegalStateException("Can't find lower bound");
				}
			}
		} else {
			if (probeValue < targetValue) {
				upperBound = probeHuman;
				lowerBound = upperBound / 2;
				boolean found = false;
				for (int i = 0; i < 1000 && !found; i++) {
					fixedMonkeys.put("humn", new Value(lowerBound));
					final long newValue = expr.compute(fixedMonkeys::get);
					if (newValue >= targetValue) {
						found = true;
					} else {
						lowerBound = lowerBound / 2;
					}
				}
				if (!found) {
					throw new IllegalStateException("Can't find lower bound");
				}
			} else {
				lowerBound = probeHuman;
				upperBound = 2 * probeHuman;
				boolean found = false;
				for (int i = 0; i < 1000 && !found; i++) {
					fixedMonkeys.put("humn", new Value(upperBound));
					final long newValue = expr.compute(fixedMonkeys::get);
					if (newValue <= targetValue) {
						found = true;
					} else {
						upperBound = 2 * upperBound;
					}
				}
				if (!found) {
					throw new IllegalStateException("Can't find upper bound");
				}
			}
		}

		return new Bounds(lowerBound, upperBound);
	}

	public sealed interface Expression permits Calculation, Value
	{
		static Expression from(final String line)
		{
			try {
				final long value = Long.parseLong(line);
				return new Value(value);
			} catch (final NumberFormatException e) {
				final String[] parts = line.split(" ");
				return new Calculation(parts[0].trim(), parts[2].trim(), Operation.from(parts[1].trim()));
			}
		}

		default long compute(final Function<String, Expression> monkeyResolver)
		{
			return switch (this) {
				case Value(long value) -> value;
				case Calculation(String left, String right, Operation operation) -> {
					final long leftValue = monkeyResolver.apply(left).compute(monkeyResolver);
					final long rightValue = monkeyResolver.apply(right).compute(monkeyResolver);
					yield switch (operation) {
						case SUM -> leftValue + rightValue;
						case DIFF -> leftValue - rightValue;
						case MUL -> leftValue * rightValue;
						case DIV -> leftValue / rightValue;
					};
				}
			};
		}
	}

	public record Value(long value) implements Expression {}

	public record Calculation(String left, String right, Operation operation) implements Expression {}

	private record Bounds(long lowerBound, long upperBound) {}

	public enum Operation
	{
		SUM, DIFF, MUL, DIV;

		public static Operation from(final String value)
		{
			return switch (value) {
				case "+" -> SUM;
				case "-" -> DIFF;
				case "*" -> MUL;
				case "/" -> DIV;
				default -> throw new IllegalArgumentException("Unsupported operation " + value);
			};
		}
	}

}
