package com.mscg;

import com.mscg.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record MonkeysGame(List<Monkey> monkeys)
{

	public static MonkeysGame parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Monkey> monkeys = StreamUtils.splitted(in.lines(), String::isBlank) //
					.map(Stream::toList) //
					.map(Monkey::from) //
					.toList();
			return new MonkeysGame(monkeys);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeMonkeyBusinessLevel(final int maxRounds, final boolean reduceWorry)
	{
		final LongUnaryOperator worryController;

		if (reduceWorry) {
			final long controlValue = monkeys.stream() //
					.mapToLong(Monkey::divisor) //
					.reduce(1L, (a, v) -> a * v);
			worryController = val -> val % controlValue;
		} else {
			worryController = val -> val / 3;
		}

		final long[] activities = new long[monkeys.size()];

		for (int round = 1; round <= maxRounds; round++) {
			for (int i = 0; i < monkeys.size(); i++) {
				final Monkey monkey = monkeys.get(i);
				while (!monkey.items().isEmpty()) {
					activities[i]++;
					final long oldValue = monkey.items().pop();
					final long newValue = monkey.worryLevelCalculator().applyAsLong(oldValue);
					final long reducedValue = worryController.applyAsLong(newValue);
					final int destination = monkey.destinationFinder().applyAsInt(reducedValue);
					monkeys.get(destination).items().add(reducedValue);
				}
			}
		}

		Arrays.sort(activities);

		return Math.multiplyExact(activities[activities.length - 1], activities[activities.length - 2]);
	}

	public record Monkey(int id, Deque<Long> items, long divisor, LongUnaryOperator worryLevelCalculator,
						 LongToIntFunction destinationFinder)
	{
		@SuppressWarnings({ "SwitchStatementWithTooFewBranches", "RegExpRepeatedSpace", "java:S1192", "java:S6326" })
		public static Monkey from(final List<String> lines)
		{
			// parse id
			final var idPatter = Pattern.compile("Monkey (\\d+):");
			final var idMatcher = idPatter.matcher(lines.get(0));
			if (!idMatcher.matches()) {
				throw new IllegalArgumentException("Illegal monkey description: Illegal id");
			}
			final int id = Integer.parseInt(idMatcher.group(1));

			// parse items
			final var itemsStr = lines.get(1).substring(lines.get(1).indexOf(':') + 1).split(",");
			final Deque<Long> items = Arrays.stream(itemsStr) //
					.map(String::trim) //
					.map(Long::parseLong) //
					.collect(Collectors.toCollection(ArrayDeque::new));

			// parse worry level calculator
			final var operationPattern = Pattern.compile("  Operation: new = (old|\\d+) ([*+]) (old|\\d+)");
			final var operationMatcher = operationPattern.matcher(lines.get(2));
			if (!operationMatcher.matches()) {
				throw new IllegalArgumentException("Illegal monkey description: Illegal operation");
			}

			final LongUnaryOperator worryLevelCalculator = switch (operationMatcher.group(1)) {
				case "old" -> switch (operationMatcher.group(3)) {
					case "old" -> switch (operationMatcher.group(2)) {
						case "*" -> old -> Math.multiplyExact(old, old);
						case "+" -> old -> Math.addExact(old, old);
						default -> throw new IllegalArgumentException("Illegal monkey description: Illegal operand in operation");
					};
					default -> switch (operationMatcher.group(2)) {
						case "*" -> old -> Math.multiplyExact(old, Long.parseLong(operationMatcher.group(3)));
						case "+" -> old -> Math.addExact(old, Long.parseLong(operationMatcher.group(3)));
						default -> throw new IllegalArgumentException("Illegal monkey description: Illegal operand in operation");
					};
				};
				default -> switch (operationMatcher.group(3)) {
					case "old" -> switch (operationMatcher.group(2)) {
						case "*" -> old -> Math.multiplyExact(Long.parseLong(operationMatcher.group(1)), old);
						case "+" -> old -> Math.addExact(Long.parseLong(operationMatcher.group(1)), old);
						default -> throw new IllegalArgumentException("Illegal monkey description: Illegal operand in operation");
					};
					default -> throw new IllegalArgumentException(
							"Illegal monkey description: Operation does not depend on old value");
				};
			};

			// parse destination finder
			final var testPattern = Pattern.compile("  Test: divisible by (\\d+)");
			final var testMatcher = testPattern.matcher(lines.get(3));
			if (!testMatcher.matches()) {
				throw new IllegalArgumentException("Illegal monkey description: Illegal tester");
			}
			final long divisor = Long.parseLong(testMatcher.group(1));
			final int trueDestination = Integer.parseInt(lines.get(4).substring(lines.get(4).lastIndexOf(' ') + 1));
			final int falseDestination = Integer.parseInt(lines.get(5).substring(lines.get(5).lastIndexOf(' ') + 1));

			return new Monkey(id, items, divisor, //
					worryLevelCalculator, //
					val -> val % divisor == 0 ? trueDestination : falseDestination);
		}
	}

}
