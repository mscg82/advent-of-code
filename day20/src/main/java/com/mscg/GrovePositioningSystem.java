package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public record GrovePositioningSystem(long[] values)
{
	public static GrovePositioningSystem parseInput(final BufferedReader in) throws IOException
	{
		try {
			final long[] values = in.lines() //
					.mapToLong(Long::parseLong) //
					.toArray();
			return new GrovePositioningSystem(values);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long getGroveCoordinates()
	{
		final var list = new InlineLinkedList(values);
		list.mix(1);

		final var coordinates = list.getCoordinates();
		return coordinates.c1() + coordinates.c2() + coordinates.c3();
	}

	public long decryptAndGetGroveCoordinates()
	{
		final long[] newValues = Arrays.stream(values) //
				.map(value -> value * 811589153L) //
				.toArray();

		final var list = new InlineLinkedList(newValues);
		list.mix(10);

		final var coordinates = list.getCoordinates();
		return coordinates.c1() + coordinates.c2() + coordinates.c3();
	}

	private static class InlineLinkedList
	{
		private final long[] values;

		private final int[] next;

		private final int[] prev;

		public InlineLinkedList(final long[] values)
		{
			this.values = Arrays.copyOf(values, values.length);
			this.next = new int[values.length];
			this.prev = new int[values.length];

			for (int i = 0; i < values.length; i++) {
				this.next[i] = (i + 1) % values.length;
				this.prev[i] = (i + values.length - 1) % values.length;
			}
		}

		public Optional<Pointer> find(final long value)
		{
			for (int i = 0; i < values.length; i++) {
				if (values[i] == value) {
					return Optional.of(new Pointer(i));
				}
			}
			return Optional.empty();
		}

		public Coordinates getCoordinates()
		{
			final var pos0 = find(0).orElseThrow();
			final var pos1 = pos0.skip(1000);
			final var pos2 = pos1.skip(1000);
			final var pos3 = pos2.skip(1000);

			return new Coordinates(pos1.val(), pos2.val(), pos3.val());
		}

		public void mix(final int times)
		{
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < values.length; i++) {
					final var pointer = new Pointer(i);
					final var value = pointer.val();
					if (value == 0) {
						continue;
					}
					final var destination = pointer.skip(value);

					// detach from current position
					next[prev[i]] = next[i];
					prev[next[i]] = prev[i];

					// reattach after or before destination
					if (value > 0) {
						next[i] = next[destination.index];
						prev[i] = destination.index;
						prev[next[destination.index]] = i;
						next[destination.index] = i;
					} else {
						next[i] = destination.index;
						prev[i] = prev[destination.index];
						next[prev[destination.index]] = i;
						prev[destination.index] = i;
					}
				}
			}
		}

		public class Pointer
		{
			private final int index;

			public Pointer(final int index)
			{
				if (index < 0 || index >= values.length) {
					throw new IllegalArgumentException("Invalid index " + index + " for range [0, " + values.length + ")");
				}
				this.index = index;
			}

			public long val()
			{
				return values[index];
			}

			public Pointer skip(final long amount)
			{
				final int distance = (int) (amount % (values.length - 1));

				if (distance == 0) {
					return this;
				}

				int position = index;
				if (distance > 0) {
					for (int i = 0; i < distance; i++) {
						position = next[position];
					}
				} else {
					for (int i = 0; i < -distance; i++) {
						position = prev[position];
					}
				}
				return new Pointer(position);
			}

			@Override
			public String toString()
			{
				return "*" + index + "=" + val();
			}

			@SuppressWarnings("unused")
			public Stream<Pointer> stream()
			{
				return Stream.concat( //
						Stream.of(this), //
						Stream.iterate(this.skip(1), p -> p.index != this.index, p -> p.skip(1)));
			}
		}

		public record Coordinates(long c1, long c2, long c3) {}
	}

}
