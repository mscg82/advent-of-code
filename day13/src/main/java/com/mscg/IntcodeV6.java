package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

public record IntcodeV6(long relativeBase, long[] data, Map<Long, Long> additionalData, long[] outputs, int ip, boolean halted)
{

	private static final int MODE_POSITION = 0;

	private static final int MODE_IMMEDIATE = 1;

	private static final int MODE_RELATIVE = 2;

	private static final int CODE_EXIT = 99;

	private static final int CODE_ADD = 1;

	private static final int CODE_MULTIPLY = 2;

	private static final int CODE_INPUT = 3;

	private static final int CODE_OUTPUT = 4;

	private static final int CODE_JUMP_TRUE = 5;

	private static final int CODE_JUMP_FALSE = 6;

	private static final int CODE_LESS_THAN = 7;

	private static final int CODE_EQUALS = 8;

	private static final int CODE_SET_REL_BASE = 9;

	public static IntcodeV6 parseInput(final BufferedReader in) throws IOException
	{
		final long[] data = Arrays.stream(in.readLine().split(",")) //
				.mapToLong(Long::parseLong) //
				.toArray();
		return new IntcodeV6(data);
	}

	public IntcodeV6(final long[] data) {
		this(0, data, Map.of(), new long[0], 0, false);
	}

	public IntcodeV6 withNounAndVerb(final int noun, final int verb)
	{
		return withUpdatedData(data -> {
			data[1] = noun;
			data[2] = verb;
		});
	}

	public IntcodeV6 withUpdatedData(final Consumer<long[]> dataChanger)
	{
		final long[] data = this.data.clone();
		dataChanger.accept(data);
		return new IntcodeV6(relativeBase, data, additionalData, outputs, ip, halted);
	}

	public IntcodeV6 execute(final Iterator<Long> inputs)
	{
		return execute(inputs, Integer.MAX_VALUE);
	}

	public IntcodeV6 execute(final Iterator<Long> inputs, final int outputsBeforeInterrupt)
	{
		return execute(inputs::next, outputsBeforeInterrupt);
	}

	public IntcodeV6 execute(final InputGenerator inputs)
	{
		return execute(inputs, Integer.MAX_VALUE);
	}

	public IntcodeV6 execute(final InputGenerator inputs, final int outputsBeforeInterrupt)
	{
		if (halted) {
			throw new IllegalStateException("Computer is halted");
		}

		// defensive copy to avoid modifying input data
		final var data = new MutableData(this.relativeBase, this.data.clone(), new HashMap<>(this.additionalData));

		final List<Long> outputs = new ArrayList<>();

		int lastIp = -1;
		boolean halted = false;
		int ip = this.ip;
		while (ip >= 0) {
			final int instruction = (int) data.getDir(ip);
			final int opcode = instruction % 100;
			final int modes = instruction / 100;
			ip = switch (opcode) {
				case CODE_ADD -> {
					final int mode1 = modes % 10;
					final int mode2 = (modes / 10) % 10;
					final int mode3 = modes / 100;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					final long p3 = data.getDir(ip + 3);
					data.set(p3, data.get(p1, mode1) + data.get(p2, mode2), mode3);
					yield ip + 4;
				}

				case CODE_MULTIPLY -> {
					final int mode1 = modes % 10;
					final int mode2 = (modes / 10) % 10;
					final int mode3 = modes / 100;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					final long p3 = data.getDir(ip + 3);
					data.set(p3, data.get(p1, mode1) * data.get(p2, mode2), mode3);
					yield ip + 4;
				}

				case CODE_INPUT -> {
					final int p1 = (int) data.getDir(ip + 1);
					data.set(p1, inputs.next(), modes);
					yield ip + 2;
				}

				case CODE_OUTPUT -> {
					final long p1 = data.getDir(ip + 1);
					outputs.add(data.get(p1, modes));
					if (outputs.size() >= outputsBeforeInterrupt) {
						lastIp = ip + 2;
						yield -1;
					}
					yield ip + 2;
				}

				case CODE_JUMP_TRUE -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					yield data.get(p1, mode1) != 0 ? (int) data.get(p2, mode2) : ip + 3;
				}

				case CODE_JUMP_FALSE -> {
					final int mode1 = modes % 10;
					final int mode2 = modes / 10;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					yield data.get(p1, mode1) == 0 ? (int) data.get(p2, mode2) : ip + 3;
				}

				case CODE_LESS_THAN -> {
					final int mode1 = modes % 10;
					final int mode2 = (modes / 10) % 10;
					final int mode3 = modes / 100;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					final long p3 = data.getDir(ip + 3);
					data.set(p3, data.get(p1, mode1) < data.get(p2, mode2) ? 1 : 0, mode3);
					yield ip + 4;
				}

				case CODE_EQUALS -> {
					final int mode1 = modes % 10;
					final int mode2 = (modes / 10) % 10;
					final int mode3 = modes / 100;
					final long p1 = data.getDir(ip + 1);
					final long p2 = data.getDir(ip + 2);
					final long p3 = data.getDir(ip + 3);
					data.set(p3, data.get(p1, mode1) == data.get(p2, mode2) ? 1 : 0, mode3);
					yield ip + 4;
				}

				case CODE_SET_REL_BASE -> {
					final long p1 = data.getDir(ip + 1);
					data.adjustRelativeBase(data.get(p1, modes));
					yield ip + 2;
				}

				case CODE_EXIT -> {
					lastIp = ip + 1;
					halted = true;
					yield -1;
				}

				default -> throw new IllegalStateException("Unknown opcode " + instruction);
			};
		}

		final long[] outputsArr = outputs.stream() //
				.mapToLong(Long::longValue) //
				.toArray();

		return new IntcodeV6(data.relativeBase(), data.data(), Map.copyOf(data.additionalData()), outputsArr, lastIp, halted);
	}

	@AllArgsConstructor
	private static final class MutableData
	{
		private long relativeBase;

		private final long[] data;

		private final Map<Long, Long> additionalData;

		public void adjustRelativeBase(final long relativeBase)
		{
			this.relativeBase += relativeBase;
		}

		public long getDir(final int index)
		{
			return data[index];
		}

		public long get(final long value, final int mode)
		{
			return switch (mode) {
				case MODE_IMMEDIATE -> value;
				case MODE_POSITION -> value < data.length ? data[(int) value] : additionalData.getOrDefault(value, 0L);
				case MODE_RELATIVE -> {
					final long index = value + relativeBase;
					yield index < data.length ? data[(int) index] : additionalData.getOrDefault(index, 0L);
				}
				default -> throw new IllegalArgumentException("Unsupported mode " + mode);
			};
		}

		public void set(final long index, final long value, final int mode)
		{
			final long realIndex = switch (mode) {
				case MODE_POSITION -> index;
				case MODE_RELATIVE -> index + relativeBase;
				default -> throw new IllegalStateException("Unsupported mode " + mode + " for set");
			};

			if (realIndex < data.length) {
				data[(int) realIndex] = value;
			} else {
				additionalData.put(realIndex, value);
			}
		}

		public long relativeBase()
		{
			return relativeBase;
		}

		public long[] data()
		{
			return data;
		}

		public Map<Long, Long> additionalData()
		{
			return additionalData;
		}

	}

	@FunctionalInterface
	public interface InputGenerator
	{
		long next();
	}

}
