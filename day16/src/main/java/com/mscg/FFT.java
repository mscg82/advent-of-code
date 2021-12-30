package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record FFT(int[] values)
{

	public static String print(final int[] values, final int offset, final int maxLength)
	{
		final int length = Math.min(values.length, maxLength - offset);
		final var ret = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			ret.append(values[offset + i]);
		}
		return ret.toString();
	}

	public static FFT parseInput(final BufferedReader in) throws IOException
	{
		final int[] values = in.readLine() //
				.chars() //
				.map(c -> c - '0') //
				.toArray();
		return new FFT(values);
	}

	public static List<Mask> generateMasks(final int firstMask, final int valuesLength)
	{
		final List<Mask> masks = Arrays.asList(new Mask[valuesLength - firstMask]);
		for (int i = firstMask; i < valuesLength; i++) {
			masks.set(i - firstMask, generateMask(i, valuesLength));
		}

		return masks;
	}

	public static Mask generateMask(final int maskIndex, final int valuesLength)
	{
		enum MaskStep
		{
			ZERO_1, ONE, ZERO_2, MINUS_ONE
		}

		final List<MaskEntry> entries = new ArrayList<>();
		var curStep = MaskStep.ONE;
		int offset = maskIndex;
		while (offset < valuesLength) {
			if (curStep != MaskStep.ZERO_1 && curStep != MaskStep.ZERO_2) {
				final int value = switch (curStep) {
					case ONE -> 1;
					case ZERO_1, ZERO_2 -> 0;
					case MINUS_ONE -> -1;
				};
				entries.add(new MaskEntry(offset, Math.min(offset + maskIndex, valuesLength - 1), value));
			}
			curStep = switch (curStep) {
				case ZERO_1 -> MaskStep.ONE;
				case ONE -> MaskStep.ZERO_2;
				case ZERO_2 -> MaskStep.MINUS_ONE;
				case MINUS_ONE -> MaskStep.ZERO_1;
			};
			offset += maskIndex + 1;
		}

		return new Mask(entries);
	}

	public int[] apply(final int steps)
	{
		final List<Mask> masks = generateMasks(0, values.length);
		int[] current = values;
		for (int step = 0; step < steps; step++) {
			final int[] newVal = new int[current.length];
			for (int i = 0; i < current.length; i++) {
				final Mask mask = masks.get(i);
				long total = 0;
				for (final var entry : mask.entries()) {
					for (int j = entry.start(); j <= entry.end(); j++) {
						total += (long) current[j] * entry.value();
					}
				}
				newVal[i] = (int) (Math.abs(total) % 10);
			}
			current = newVal;
		}
		return current;
	}

	public String getMessage(final int steps)
	{
		int offset = 0;
		for (int i = 0; i < 7; i++) {
			offset = offset * 10 + values[i];
		}

		final int inputLength = values.length * 10_000;
		if (offset < inputLength / 2) {
			throw new UnsupportedOperationException("Can't get message");
		}

		int[] current = values;
		for (int step = 0; step < steps; step++) {
			final int[] newVal = new int[inputLength - offset];
			newVal[newVal.length - 1] = current[current.length - 1];
			int prevIndex = current.length - 2;
			for (int i = newVal.length - 2; i >= 0; i--) {
				newVal[i] = (newVal[i + 1] + current[prevIndex]) % 10;
				prevIndex--;
				if (prevIndex < 0) {
					prevIndex = current.length - 1;
				}
			}
			current = newVal;
		}

		return FFT.print(current, 0, 8);
	}

	public record MaskEntry(int start, int end, int value) {}

	public record Mask(List<MaskEntry> entries) {}
}
