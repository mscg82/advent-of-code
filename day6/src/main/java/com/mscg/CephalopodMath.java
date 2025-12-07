package com.mscg;

import com.mscg.utils.StreamUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public record CephalopodMath(List<String> worksheetLines, List<Operator> operators)
{

	private static final Pattern EMPTY_SPACES_PATTERN = Pattern.compile("\\s+");

	public static CephalopodMath parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> allLines = in.lines() //
					.filter(StreamUtils.nonEmptyString()) //
					.toList();
			final List<Operator> operators = EMPTY_SPACES_PATTERN.splitAsStream(allLines.getLast()) //
					.map(piece -> switch (piece.charAt(0)) {
						case '*' -> Operator.MUL;
						case '+' -> Operator.SUM;
						default -> throw new IllegalArgumentException("Unsupported operator " + piece.charAt(0));
					}) //
					.toList();
			return new CephalopodMath(allLines.subList(0, allLines.size() - 1), operators);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeGrandTotal()
	{
		final var operands = IntStream.range(0, operators.size()) //
				.mapToObj(_ -> new LongArrayList(worksheetLines.size())) //
				.toList();
		for (final String line : worksheetLines) {
			final long[] lineOperands = EMPTY_SPACES_PATTERN.splitAsStream(line) //
					.filter(StreamUtils.nonEmptyString()) //
					.mapToLong(Long::parseLong) //
					.toArray();
			for (int i = 0; i < lineOperands.length; i++) {
				operands.get(i).add(lineOperands[i]);
			}
		}
		long grandTotal = 0;
		for (int i = 0; i < operands.size(); i++) {
			final LongList values = operands.get(i);
			final long result = switch (operators.get(i)) {
				case MUL -> values.longStream().reduce((op1, op2) -> op1 * op2).orElse(0);
				case SUM -> values.longStream().reduce(Long::sum).orElse(0);
			};
			grandTotal += result;
		}
		return grandTotal;
	}

	public long computeCorrectGrandTotal()
	{
		long grandTotal = 0;
		final char[] currentValue = new char[worksheetLines.size()];
		final LongList values = new LongArrayList(operators.size());
		int operatorIdx = operators.size() - 1;
		for (int i = worksheetLines.getFirst().length() - 1; i >= 0; i--) {
			Arrays.fill(currentValue, ' ');
			final boolean nonSpaceFound = extractCurrentValue(i, currentValue);

			if (nonSpaceFound) {
				values.add(parseAsLong(currentValue));
			}
			if (i == 0 || !nonSpaceFound) {
				final var op = operators.get(operatorIdx--);
				final long result = switch (op) {
					case MUL -> values.longStream().reduce((op1, op2) -> op1 * op2).orElse(0);
					case SUM -> values.longStream().reduce(Long::sum).orElse(0);
				};
				grandTotal += result;
				values.clear();
			}
		}
		return grandTotal;
	}

	private boolean extractCurrentValue(final int i, final char[] currentValue)
	{
		boolean nonSpaceFound = false;
		for (int j = 0; j < worksheetLines.size(); j++) {
			final String line = worksheetLines.get(j);
			final char c = line.charAt(i);
			currentValue[j] = c;
			if (c != ' ') {
				nonSpaceFound = true;
			}
		}
		return nonSpaceFound;
	}

	private static long parseAsLong(final char[] currentValue)
	{
		long value = 0;
		for (final char c : currentValue) {
			if (c != ' ') {
				value = value * 10L + (c - '0');
			}
		}
		return value;
	}

	public enum Operator
	{
		SUM, MUL
	}

}
