package com.mscg.utils.chrono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public sealed interface ChronoAction permits //
		ChronoAction.DoubleChronoAction, //
		ChronoAction.IntChronoAction, //
		ChronoAction.LongChronoAction, //
		ChronoAction.ObjectChronoAction, //
		ChronoAction.VoidChronoAction //
{

	static ChronoAction run(final IntSupplier block)
	{
		final var before = Instant.now();
		final var value = block.getAsInt();
		final var elapsed = Duration.between(before, Instant.now());
		return new IntChronoAction(value, elapsed);
	}

	static ChronoAction run(final LongSupplier block)
	{
		final var before = Instant.now();
		final var value = block.getAsLong();
		final var elapsed = Duration.between(before, Instant.now());
		return new LongChronoAction(value, elapsed);
	}

	static ChronoAction run(final DoubleSupplier block)
	{
		final var before = Instant.now();
		final var value = block.getAsDouble();
		final var elapsed = Duration.between(before, Instant.now());
		return new DoubleChronoAction(value, elapsed);
	}

	static <T> ChronoAction run(final Supplier<T> block)
	{
		final var before = Instant.now();
		final var value = block.get();
		final var elapsed = Duration.between(before, Instant.now());
		return new ObjectChronoAction<>(value, elapsed);
	}

	static <E extends Throwable> ChronoAction run(final ThrowingRunnable<E> block) throws E
	{
		final var before = Instant.now();
		block.run();
		final var elapsed = Duration.between(before, Instant.now());
		return new VoidChronoAction(elapsed);
	}

	Duration elapsed();

	interface ThrowingRunnable<E extends Throwable>
	{
		void run() throws E;
	}

	record IntChronoAction(int value, Duration elapsed) implements ChronoAction
	{
		@Override
		public String toString()
		{
			return "[value: %d, elapsed: %s]".formatted(value, elapsed);
		}
	}

	record LongChronoAction(long value, Duration elapsed) implements ChronoAction
	{
		@Override
		public String toString()
		{
			return "[value: %d, elapsed: %s]".formatted(value, elapsed);
		}
	}

	record DoubleChronoAction(double value, Duration elapsed) implements ChronoAction
	{
		@Override
		public String toString()
		{
			return "[value: %f, elapsed: %s]".formatted(value, elapsed);
		}
	}

	record ObjectChronoAction<T>(T value, Duration elapsed) implements ChronoAction
	{
		@Override
		public String toString()
		{
			return "[value: %s, elapsed: %s]".formatted(value, elapsed);
		}
	}

	record VoidChronoAction(Duration elapsed) implements ChronoAction
	{
		@Override
		public String toString()
		{
			return "[elapsed: %s]".formatted(elapsed);
		}
	}

}
