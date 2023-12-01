package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CalibrationSequence(List<String> sequences)
{
	public static CalibrationSequence parseInput(final BufferedReader in) throws IOException
	{
		try {
			return new CalibrationSequence(in.lines().toList());
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeCalibrationSequence()
	{
		return sequences.stream() //
				.mapToInt(sequence -> {
					int digit1 = 0;
					for (int i = 0, l = sequence.length(); i < l; i++) {
						final char c = sequence.charAt(i);
						if (Character.isDigit(c)) {
							digit1 = c - '0';
							break;
						}
					}
					int digit2 = 0;
					for (int i = sequence.length() - 1; i >= 0; i--) {
						final char c = sequence.charAt(i);
						if (Character.isDigit(c)) {
							digit2 = c - '0';
							break;
						}
					}
					return digit1 * 10 + digit2;
				}) //
				.sum();
	}

	public long computeFixedCalibrationSequence()
	{
		final var digits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		final var strings = new String[] { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
		final Map<String, Integer> digitsToValue = new LinkedHashMap<>();
		for (int i = 0; i < digits.length; i++) {
			digitsToValue.put(digits[i], i);
			digitsToValue.put(strings[i], i);
		}
		long calibrationSequence = 0L;
		for (final String sequence : sequences) {
			final Map<String, Set<Integer>> digitToPositions = new HashMap<>();
			for (final String digit : digitsToValue.keySet()) {
				final int firstOccurrence = sequence.indexOf(digit);
				final int lastOccurrence = sequence.lastIndexOf(digit);
				final var positions = new HashSet<Integer>();
				if (firstOccurrence >= 0) {
					positions.add(firstOccurrence);
				}
				if (lastOccurrence >= 0) {
					positions.add(lastOccurrence);
				}
				if (!positions.isEmpty()) {
					digitToPositions.put(digit, positions);
				}
			}
			final String firstDigit = digitToPositions.entrySet().stream() //
					.min(Comparator.comparingInt(
							entry -> entry.getValue().stream().mapToInt(Integer::valueOf).min().orElseThrow())) //
					.map(Map.Entry::getKey) //
					.orElseThrow();
			final String secondDigit = digitToPositions.entrySet().stream() //
					.max(Comparator.comparingInt(
							entry -> entry.getValue().stream().mapToInt(Integer::valueOf).max().orElseThrow())) //
					.map(Map.Entry::getKey) //
					.orElseThrow();
			final int value = digitsToValue.get(firstDigit) * 10 + digitsToValue.get(secondDigit);
			calibrationSequence += value;
		}
		return calibrationSequence;
	}
}
