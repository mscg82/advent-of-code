package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.lambda.Seq;

public record IntcodeV3(int[] data, int[] outputs, int ip, boolean halted) {

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

	public IntcodeV3(final int[] data) {
		this(data, new int[0], 0, false);
	}

	public static IntcodeV3 parseInput(final BufferedReader in) throws IOException
	{
		final int[] data = Arrays.stream(in.readLine().split(",")) //
				.mapToInt(Integer::parseInt) //
				.toArray();
		return new IntcodeV3(data);
	}

	private static int data(final int[] data, final int value, final int mode)
	{
		return switch (mode) {
			case MODE_IMMEDIATE -> value;
			case MODE_POSITION -> data[value];
			default -> throw new IllegalArgumentException("Unsupported mode " + mode);
		};
	}

	public IntcodeV3 withNounAndVerb(final int noun, final int verb)
	{
		final int[] data = this.data.clone();
		data[1] = noun;
		data[2] = verb;
		return new IntcodeV3(data, outputs, ip, halted);
	}

	public IntcodeV3 execute(final Iterator<Integer> inputs, final boolean interruptOnOutput)
	{
		final Deque<Integer> inputsQueue = Seq.seq(inputs).collect(Collectors.toCollection(LinkedList::new));
		return execute(inputsQueue, interruptOnOutput);
	}

	public IntcodeV3 execute(final Deque<Integer> inputs, final boolean interruptOnOutput)
	{
		if (halted) {
			throw new IllegalStateException("Computer is halted");
		}

		// defensive copy to avoid modifying input data
		final int[] data = this.data.clone();

		final List<Integer> outputs = new ArrayList<>();

		int lastIp = -1;
		boolean halted = false;
		int ip = this.ip;
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
					data[p1] = inputs.pop();
					yield ip + 2;
				}

				case CODE_OUTPUT -> {
					final int p1 = data[ip + 1];
					outputs.add(data(data, p1, modes));
					if (interruptOnOutput) {
						lastIp = ip + 2;
						yield -1;
					}
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

				case CODE_EXIT -> {
					lastIp = ip + 1;
					halted = true;
					yield -1;
				}

				default -> throw new IllegalStateException("Unknown opcode " + data[ip]);
			};
		}

		final int[] outputsArr = outputs.stream() //
				.mapToInt(Integer::intValue) //
				.toArray();

		return new IntcodeV3(data, outputsArr, lastIp, halted);
	}

}
