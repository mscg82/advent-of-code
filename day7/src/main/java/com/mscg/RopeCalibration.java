package com.mscg;

import it.unimi.dsi.fastutil.longs.LongImmutableList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record RopeCalibration(List<EquationSides> equationSides)
{
	public static RopeCalibration parseInput(final BufferedReader in) throws IOException
	{
		try {
			final var equationSides = in.lines() //
					.map(line -> {
						final var parts = line.split(":");
						if (parts.length != 2) {
							throw new IllegalArgumentException("Unsupported line format: " + line);
						}
						final long result = Long.parseLong(parts[0]);
						final var operandParts = parts[1].trim().split("\\s+");
						final var operants = LongImmutableList.of(Arrays.stream(operandParts) //
								.map(String::trim) //
								.mapToLong(Long::parseLong) //
								.toArray());
						return new EquationSides(result, operants);
					}) //
					.toList();
			return new RopeCalibration(equationSides);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long findCalibrationResult()
	{
		return findCalibrationResult(Equation::expand);
	}

	public long findExtendedCalibrationResult()
	{
		return findCalibrationResult(Equation::expandExt);
	}

	private long findCalibrationResult(final Function<Equation, Collection<Equation>> expander)
	{
		final var validEquations = new ArrayList<EquationSides>(equationSides.size());
		for (final EquationSides equationSide : equationSides) {
			final var queue = new ArrayDeque<Equation>(10_000);
			queue.push(new Equation(equationSide, new Operator[0]));
			final int previousSize = validEquations.size();
			while (!queue.isEmpty() && previousSize == validEquations.size()) {
				final var equation = queue.pop();

				switch (equation.check()) {
					case WRONG -> { /* do nothing */ }
					case CONSISTENT -> expander.apply(equation).forEach(queue::addFirst);
					case CORRECT -> validEquations.add(equationSide);
				}
			}
		}

		return validEquations.stream() //
				.mapToLong(EquationSides::result) //
				.sum();
	}

	public record Equation(EquationSides equationSides, Operator[] operators)
	{
		public Equation
		{
			Objects.requireNonNull(equationSides, "Equation sides cannot be null");
			Objects.requireNonNull(operators, "Operators cannot be null");
			if (operators.length > equationSides.operands().size() - 1) {
				throw new IllegalArgumentException("Operators must be consistent with equation sides");
			}
		}

		public EquationResult check()
		{
			long result = equationSides().operands().getLong(0);
			for (int i = 0; i < operators.length; i++) {
				final var operator = operators[i];
				final var operand = equationSides.operands().getLong(i + 1);
				switch (operator) {
					case SUM -> result += operand;
					case MUL -> result *= operand;
					case CAT -> result = concat(result, operand);
				}
				if (result > equationSides().result()) {
					return EquationResult.WRONG;
				}
			}
			return result == equationSides().result() ? EquationResult.CORRECT : EquationResult.CONSISTENT;
		}

		public List<Equation> expand()
		{
			if (operators.length == equationSides.operands().size() - 1) {
				return List.of();
			}

			return List.of( //
					new Equation(equationSides, extendOperators(Operator.SUM)), //
					new Equation(equationSides, extendOperators(Operator.MUL)));
		}

		public List<Equation> expandExt()
		{
			if (operators.length == equationSides.operands().size() - 1) {
				return List.of();
			}

			return List.of( //
					new Equation(equationSides, extendOperators(Operator.SUM)), //
					new Equation(equationSides, extendOperators(Operator.MUL)), //
					new Equation(equationSides, extendOperators(Operator.CAT)));
		}

		private Operator[] extendOperators(final Operator newOperator)
		{
			final var extendedOperators = new Operator[operators.length + 1];
			System.arraycopy(operators, 0, extendedOperators, 0, operators.length);
			extendedOperators[operators.length] = newOperator;
			return extendedOperators;
		}

		private static long concat(long a, final long b)
		{
			if (b == 0) {
				a *= 10;
			} else {
				long tempB = b;
				while (tempB > 0) {
					tempB /= 10;
					a *= 10;
				}
			}
			return a + b;
		}
	}

	public record EquationSides(long result, LongList operands) {}

	public enum EquationResult
	{
		CORRECT, CONSISTENT, WRONG
	}

	public enum Operator
	{
		SUM, MUL, CAT
	}

}
