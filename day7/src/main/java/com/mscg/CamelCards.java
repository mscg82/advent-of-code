package com.mscg;

import com.mscg.utils.StreamUtils;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Stream;

public record CamelCards(List<Game> games)
{
	public static CamelCards parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Game> games = in.lines() //
					.map(line -> {
						final var parts = line.split(" ");
						if (parts.length != 2) {
							throw new IllegalArgumentException("Invalid game format " + line);
						}
						return new Game(CardHand.from(parts[0]), Long.parseLong(parts[1]));
					}) //
					.toList();
			return new CamelCards(games);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeTotalWinning()
	{
		final List<Game> sortedGames = games.stream() //
				.sorted(Game.BASE_COMPARATOR) //
				.toList();
		return Seq.zipWithIndex(sortedGames) //
				.mapToLong(elIdx -> elIdx.v1().bid() * (elIdx.v2() + 1)) //
				.sum();
	}

	public long computeTotalWinningWithJolly()
	{
		final List<Game> sortedGames = games.stream() //
				.sorted(Game.CUSTOM_COMPARATOR) //
				.toList();
		return Seq.zipWithIndex(sortedGames) //
				.mapToLong(elIdx -> elIdx.v1().bid() * (elIdx.v2() + 1)) //
				.sum();
	}

	public record Game(CardHand hand, long bid)
	{
		private static final Comparator<Game> BASE_COMPARATOR = Comparator.comparing(
						(Game game) -> HandType.fromCardHand(game.hand)) //
				.thenComparing(Game::hand, CardHand.BASE_COMPARATOR);

		private static final Comparator<Game> CUSTOM_COMPARATOR = Comparator.comparing(
						(Game game) -> HandType.fromCardHandWithJolly(game.hand)) //
				.thenComparing(Game::hand, CardHand.CUSTOM_COMPARATOR);
	}

	public record CardHand(CardLabel card1, CardLabel card2, CardLabel card3, CardLabel card4, CardLabel card5)
	{
		private static final Comparator<CardHand> BASE_COMPARATOR = Comparator //
				.comparing(CardHand::card1, CardLabel.BASE_COMPARATOR) //
				.thenComparing(CardHand::card2, CardLabel.BASE_COMPARATOR) //
				.thenComparing(CardHand::card3, CardLabel.BASE_COMPARATOR) //
				.thenComparing(CardHand::card4, CardLabel.BASE_COMPARATOR) //
				.thenComparing(CardHand::card5, CardLabel.BASE_COMPARATOR);

		private static final Comparator<CardHand> CUSTOM_COMPARATOR = Comparator //
				.comparing(CardHand::card1, CardLabel.CUSTOM_COMPARATOR) //
				.thenComparing(CardHand::card2, CardLabel.CUSTOM_COMPARATOR) //
				.thenComparing(CardHand::card3, CardLabel.CUSTOM_COMPARATOR) //
				.thenComparing(CardHand::card4, CardLabel.CUSTOM_COMPARATOR) //
				.thenComparing(CardHand::card5, CardLabel.CUSTOM_COMPARATOR);

		public static CardHand from(final String handStr)
		{
			if (handStr.length() != 5) {
				throw new IllegalArgumentException("Invalid had format \"" + handStr + "\"");
			}
			return new CardHand( //
					CardLabel.from(handStr.charAt(0)), //
					CardLabel.from(handStr.charAt(1)), //
					CardLabel.from(handStr.charAt(2)), //
					CardLabel.from(handStr.charAt(3)), //
					CardLabel.from(handStr.charAt(4)));
		}

		public Stream<CardLabel> stream()
		{
			return Stream.of(card1, card2, card3, card4, card5);
		}
	}

	public enum CardLabel
	{
		L2, L3, L4, L5, L6, L7, L8, L9, T, J, Q, K, A;

		public static final Comparator<CardLabel> BASE_COMPARATOR = Comparator.naturalOrder();

		public static final Comparator<CardLabel> CUSTOM_COMPARATOR = Comparator.comparingInt(label -> switch (label) {
			case J -> 1;
			case L2 -> 2;
			case L3 -> 3;
			case L4 -> 4;
			case L5 -> 5;
			case L6 -> 6;
			case L7 -> 7;
			case L8 -> 8;
			case L9 -> 9;
			case T -> 10;
			case Q -> 11;
			case K -> 12;
			case A -> 13;
		});

		public static CardLabel from(final char c)
		{
			return switch (c) {
				case '2' -> L2;
				case '3' -> L3;
				case '4' -> L4;
				case '5' -> L5;
				case '6' -> L6;
				case '7' -> L7;
				case '8' -> L8;
				case '9' -> L9;
				case 'T' -> T;
				case 'J' -> J;
				case 'Q' -> Q;
				case 'K' -> K;
				case 'A' -> A;
				default -> throw new IllegalArgumentException("Unsupported card label '" + c + "'");
			};
		}
	}

	public enum HandType
	{
		HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND;

		public static HandType fromCardHand(final CardHand hand)
		{
			final List<CardLabel> labels = hand.stream() //
					.sorted(CardLabel.BASE_COMPARATOR) //
					.toList();

			if (allEquals(labels)) {
				return FIVE_OF_A_KIND;
			}

			if (StreamUtils.windowed(labels, 4) //
					.anyMatch(HandType::allEquals)) {
				return FOUR_OF_A_KIND;
			}

			if (StreamUtils.windowed(labels, 3) //
					.anyMatch(HandType::allEquals)) {
				if (allEquals(List.of(labels.get(0), labels.get(1))) && //
						allEquals(List.of(labels.get(3), labels.get(4)))) {
					return FULL_HOUSE;
				}
				return THREE_OF_A_KIND;
			}

			final int pairCounts = (int) StreamUtils.windowed(labels, 2) //
					.filter(HandType::allEquals) //
					.count();

			return switch (pairCounts) {
				case 2 -> TWO_PAIR;
				case 1 -> ONE_PAIR;
				default -> HIGH_CARD;
			};
		}

		public static HandType fromCardHandWithJolly(final CardHand hand)
		{
			final List<CardLabel> labels = hand.stream() //
					.sorted(CardLabel.CUSTOM_COMPARATOR) //
					.toList();

			final int jollyCount = (int) labels.stream() //
					.filter(CardLabel.J::equals) //
					.count();

			return switch (jollyCount) {
				case 5, 4 -> FIVE_OF_A_KIND;
				case 3 -> {
					if (labels.get(3).equals(labels.get(4))) {
						yield FIVE_OF_A_KIND;
					}
					yield FOUR_OF_A_KIND;
				}
				case 2 -> {
					if (allEquals(List.of(labels.get(2), labels.get(3), labels.get(4)))) {
						yield FIVE_OF_A_KIND;
					}
					if (allEquals(List.of(labels.get(2), labels.get(3))) || allEquals(List.of(labels.get(3), labels.get(4)))) {
						yield FOUR_OF_A_KIND;
					}
					yield THREE_OF_A_KIND;
				}
				case 1 -> {
					if (allEquals(List.of(labels.get(1), labels.get(2), labels.get(3), labels.get(4)))) {
						yield FIVE_OF_A_KIND;
					}
					if (allEquals(List.of(labels.get(1), labels.get(2), labels.get(3))) || allEquals(
							List.of(labels.get(2), labels.get(3), labels.get(4)))) {
						yield FOUR_OF_A_KIND;
					}
					final int pairCounts = (int) StreamUtils.windowed(labels.stream().skip(1), 2) //
							.filter(HandType::allEquals) //
							.count();
					yield switch (pairCounts) {
						case 2 -> FULL_HOUSE;
						case 1 -> THREE_OF_A_KIND;
						default -> ONE_PAIR;
					};
				}
				default -> fromCardHand(hand);
			};
		}

		private static boolean allEquals(final SequencedCollection<CardLabel> cards)
		{
			final var first = cards.getFirst();
			return cards.stream().allMatch(first::equals);
		}
	}

}
