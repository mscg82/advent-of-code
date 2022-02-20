package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongPredicate;

@RecordBuilder
public record IntcodeV8(long relativeBase, long[] data, Map<Long, Long> additionalData, long[] outputs, int ip, boolean halted)
		implements IntcodeV8Builder.With
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

	public static IntcodeV8 parseInput(final BufferedReader in) throws IOException
	{
		final long[] data = Arrays.stream(in.readLine().split(",")) //
				.mapToLong(Long::parseLong) //
				.toArray();
		return new IntcodeV8(data);
	}

	public IntcodeV8(final long[] data)
	{
		this(0, data, Map.of(), new long[0], 0, false);
	}

	public IntcodeV8 withNounAndVerb(final int noun, final int verb)
	{
		return withUpdatedData(data -> {
			data[1] = noun;
			data[2] = verb;
		});
	}

	public IntcodeV8 withUpdatedData(final Consumer<long[]> dataChanger)
	{
		final long[] data = this.data.clone();
		dataChanger.accept(data);
		return new IntcodeV8(relativeBase, data, additionalData, outputs, ip, halted);
	}

	public IntcodeV8 execute(final Iterator<Long> inputs)
	{
		return execute(inputs, Integer.MAX_VALUE);
	}

	public IntcodeV8 execute(final Iterator<Long> inputs, final int outputsBeforeInterrupt)
	{
		return execute(inputs::next, outputsBeforeInterrupt, null);
	}

	public IntcodeV8 execute(final InputGenerator inputs)
	{
		return execute(inputs, Integer.MAX_VALUE, null);
	}

	public IntcodeV8 execute(final InputGenerator inputs, final int outputsBeforeInterrupt, final LongPredicate outputObserver)
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
			final int opcode = Thread.interrupted() ? CODE_EXIT : instruction % 100;
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
					try {
						data.set(p1, inputs.next(), modes);
						yield ip + 2;
					} catch (final InputGenerator.HaltingException e) {
						halted = true;
						lastIp = ip;
						yield -1;
					}
				}

				case CODE_OUTPUT -> {
					final long p1 = data.getDir(ip + 1);
					final long output = data.get(p1, modes);
					if (outputObserver == null || outputObserver.test(output)) {
						outputs.add(output);
						if (outputs.size() >= outputsBeforeInterrupt) {
							lastIp = ip + 2;
							yield -1;
						}
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

		return new IntcodeV8(data.relativeBase(), data.data(), Map.copyOf(data.additionalData()), outputsArr, lastIp, halted);
	}

	@FunctionalInterface
	public interface InputGenerator
	{
		static InputGenerator forArray(final long[] inputs)
		{
			return forArray(inputs, 0);
		}

		static InputGenerator forArray(final long[] inputs, final int offset)
		{
			return new ArrayInputGenerator(inputs, offset);
		}

		long next() throws HaltingException;

		class HaltingException extends Exception {}
	}

	public interface QueueInputGenerator extends InputGenerator
	{
		static QueueInputGenerator queue()
		{
			return new SynchQueueGenerator();
		}

		static QueueInputGenerator queue(final long value)
		{
			final SynchQueueGenerator generator = new SynchQueueGenerator();
			generator.add(value);
			return generator;
		}

		void add(final long value);

		void add(final long value1, final long value2);

		void add(final long value1, final long value2, final long... others);

		boolean hasEmptyReads();
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

	@AllArgsConstructor
	private static class ArrayInputGenerator implements InputGenerator
	{
		private final long[] inputs;

		private int position;

		@Override
		public long next() throws HaltingException
		{
			if (position >= inputs.length) {
				throw new HaltingException();
			}
			return inputs[position++];
		}
	}

	private static class SynchQueueGenerator implements QueueInputGenerator
	{
		private final Deque<Long> values = new ArrayDeque<>();

		private boolean emptyRead;

		@Override
		public synchronized void add(final long value)
		{
			values.add(value);
		}

		@Override
		public synchronized void add(final long value1, final long value2)
		{
			values.add(value1);
			values.add(value2);
		}

		@Override
		public synchronized void add(final long value1, final long value2, final long... others)
		{
			values.add(value1);
			values.add(value2);
			for (final long other : others) {
				values.add(other);
			}
		}

		@Override
		public synchronized boolean hasEmptyReads()
		{
			return values.isEmpty() && emptyRead;
		}

		@Override
		public synchronized long next()
		{
			emptyRead = values.isEmpty();
			return emptyRead ? -1 : values.pop();
		}
	}

}
