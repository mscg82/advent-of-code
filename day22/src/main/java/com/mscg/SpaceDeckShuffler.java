package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public record SpaceDeckShuffler(List<ShuffleType> shuffleInstructions, long cards)
{

	public static SpaceDeckShuffler parseInput(final BufferedReader in, final long cards) throws IOException
	{
		final var newDeckPatter = Pattern.compile("deal into new stack");
		final var cutPatter = Pattern.compile("cut (-?\\d+)");
		final var incrementPatter = Pattern.compile("deal with increment (\\d+)");
		try {
			final List<ShuffleType> shuffleInstructions = in.lines() //
					.map(line -> {
						var matcher = newDeckPatter.matcher(line);
						if (matcher.matches()) {
							return ShuffleType.NewDeck.NEW_DECK;
						}

						matcher = cutPatter.matcher(line);
						if (matcher.matches()) {
							return new ShuffleType.Cut(Long.parseLong(matcher.group(1)));
						}

						matcher = incrementPatter.matcher(line);
						if (matcher.matches()) {
							return new ShuffleType.Increment(Long.parseLong(matcher.group(1)));
						}

						throw new IllegalArgumentException("Unsupported instruction " + line);
					}) //
					.toList();
			return new SpaceDeckShuffler(shuffleInstructions, cards);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static <U> BinaryOperator<U> throwerMerger()
	{
		return (v1, v2) -> {
			throw new UnsupportedOperationException("Can't work on parallel streams");
		};
	}

	@SuppressWarnings("unused")
	public long trackCard(final long card)
	{
		return shuffleInstructions.stream() //
				.map(shuffle -> shuffle instanceof ShuffleType.Cut cut && cut.value() < 0 ? //
						cut.withValue(cards + cut.value()) : //
						shuffle) //
				.reduce(card, //
						(curPosition, shuffle) -> switch (shuffle) {
							case ShuffleType.NewDeck n -> cards - 1 - curPosition;
							case ShuffleType.Cut cut && curPosition >= cut.value() -> curPosition - cut.value();
							case ShuffleType.Cut cut -> (cards - cut.value()) + curPosition;
							case ShuffleType.Increment inc -> (curPosition * inc.value()) % cards;
						}, //
						throwerMerger());
	}

	@SuppressWarnings("unused")
	public long trackCardsInPositionInBigDeck(final long position, final long iterations)
	{
		final var biCards = BigInteger.valueOf(cards);
		final var minOne = BigInteger.ONE.negate();

		final var globalLinFun = Seq.seq(shuffleInstructions.stream()) //
				.reverse() //
				.map(shuffle -> switch (shuffle) {
					case ShuffleType.NewDeck n -> new LinFun(minOne, biCards.subtract(BigInteger.ONE));
					case ShuffleType.Cut cut -> new LinFun(BigInteger.ONE, BigInteger.valueOf(cut.value()).mod(biCards));
					case ShuffleType.Increment inc -> new LinFun(BigInteger.valueOf(inc.value()).modInverse(biCards),
							BigInteger.ZERO);
				}) //
				.reduce(LinFun.ID, LinFun::revCombine, throwerMerger()).simplify(cards);

		final var repeatedLinFun = globalLinFun.applyNTimes(iterations, cards);

		return repeatedLinFun.applyAsLong(position, cards);
	}

	public sealed interface ShuffleType permits ShuffleType.Cut, ShuffleType.Increment, ShuffleType.NewDeck
	{
		enum NewDeck implements ShuffleType
		{
			NEW_DECK
		}

		@RecordBuilder
		record Cut(long value) implements ShuffleType, SpaceDeckShufflerShuffleTypeCutBuilder.With {}

		record Increment(long value) implements ShuffleType {}

	}

	@FunctionalInterface
	private interface UnaryLongOperatorWithMod
	{
		long applyAsLong(long operand, long mod);
	}

	private record LinFun(BigInteger k, BigInteger m) implements UnaryOperator<BigInteger>, UnaryLongOperatorWithMod
	{
		public static final LinFun ID = new LinFun(BigInteger.ONE, BigInteger.ZERO);

		public LinFun applyNTimes(final long times, final long mod)
		{
			if (times == 0) {
				return ID;
			} else if (times % 2 == 0) {
				return this.combine(this).simplify(mod).applyNTimes(times / 2, mod).simplify(mod);
			} else {
				return this.combine(applyNTimes(times - 1, mod)).simplify(mod);
			}
		}

		@Override
		public BigInteger apply(final BigInteger x)
		{
			return k.multiply(x).add(m);
		}

		public LinFun combine(final LinFun f)
		{
			return new LinFun(k.multiply(f.k), k.multiply(f.m).add(m));
		}

		public LinFun revCombine(final LinFun f)
		{
			return f.combine(this);
		}

		public LinFun simplify(final long n)
		{
			final var bin = BigInteger.valueOf(n);
			return new LinFun(k.mod(bin), m.mod(bin));
		}

		@Override
		public long applyAsLong(final long operand, final long mod)
		{
			return apply(BigInteger.valueOf(operand)).mod(BigInteger.valueOf(mod)).longValue();
		}
	}

}
