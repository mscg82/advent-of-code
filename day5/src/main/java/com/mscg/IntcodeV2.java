package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public record IntcodeV2(int[] data)
{

	@SuppressWarnings("unused")
	private static final int MODE_POSITION = 0;

	private static final int MODE_IMMEDIATE = 1;

	private static final int CODE_EXIT = 99;

	private static final int CODE_ADD = 1;

	private static final int CODE_MULTIPLY = 2;

	private static final int CODE_INPUT = 3;

	private static final int CODE_OUTPUT = 4;

	private static final int CODE_JUMP_TRUE = 5;

	private static final int CODE_JUMP_FALSE = 6;

	private static final int CODE_LESS_THAN = 7;

	private static final int CODE_EQUALS = 8;

	public static IntcodeV2 parseInput(final BufferedReader in) throws IOException
	{
		final int[] data = Arrays.stream(in.readLine().split(",")) //
				.mapToInt(Integer::parseInt) //
				.toArray();
		return new IntcodeV2(data);
	}

	private static int data(final int[] data, final int value, final int mode)
	{
		return mode == MODE_IMMEDIATE ? value : data[value];
	}

	public ComputationResult execute(final Integer noun, final Integer verb, final Iterator<Integer> inputs)
	{
		// defensive copy to avoid modifying input data
		final int[] data = this.data.clone();
		if (noun != null) {
			data[1] = noun;
		}
		if (verb != null) {
			data[2] = verb;
		}

		final List<Integer> outputs = new ArrayList<>();

		int ip = 0;
		while (ip >= 0) {
			final int instruction = data[ip];
			final int opcode = instruction % 100;
			final int modes = instruction / 100;
			ip = switch (opcode) {
				case CODE_ADD -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					final int p3 = data[ip + 3];
					data[p3] = data(data, p1, mode1) + data(data, p2, mode2);
					yield ip + 4;
				}

				case CODE_MULTIPLY -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					final int p3 = data[ip + 3];
					data[p3] = data(data, p1, mode1) * data(data, p2, mode2);
					yield ip + 4;
				}

				case CODE_INPUT -> {
					final int p1 = data[ip + 1];
					data[p1] = inputs.next();
					yield ip + 2;
				}

				case CODE_OUTPUT -> {
					final int p1 = data[ip + 1];
					outputs.add(data(data, p1, modes));
					yield ip + 2;
				}

				case CODE_JUMP_TRUE -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					yield data(data, p1, mode1) != 0 ? data(data, p2, mode2) : ip + 3;
				}

				case CODE_JUMP_FALSE -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					yield data(data, p1, mode1) == 0 ? data(data, p2, mode2) : ip + 3;
				}

				case CODE_LESS_THAN -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					final int p3 = data[ip + 3];
					data[p3] = data(data, p1, mode1) < data(data, p2, mode2) ? 1 : 0;
					yield ip + 4;
				}

				case CODE_EQUALS -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final int p1 = data[ip + 1];
					final int p2 = data[ip + 2];
					final int p3 = data[ip + 3];
					data[p3] = data(data, p1, mode1) == data(data, p2, mode2) ? 1 : 0;
					yield ip + 4;
				}

				case CODE_EXIT -> -1;

				default -> throw new IllegalStateException("Unknown opcode " + data[ip]);
			};
		}

		final int[] outputsArr = outputs.stream() //
				.mapToInt(Integer::intValue) //
				.toArray();

		return new ComputationResult(data, outputsArr);
	}

	public record ComputationResult(int[] data, int[] outputs)
	{

	}

}
