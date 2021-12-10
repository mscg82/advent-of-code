package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public record RulesParser(List<String> rules)
{

	public static ParseOutcome parse(final String rule)
	{
		final Deque<Token> queue = new LinkedList<>();
		var currentToken = Token.EMPTY_TOKEN;

		for (int i = 0, l = rule.length(); i < l; i++) {
			final char c = rule.charAt(i);

			switch (c) {
				case '(', '[', '{', '<' -> {
					queue.addFirst(new Token(c, i, currentToken.depth() + 1));
				}
				case ')' -> {
					switch (checkToken(c,'(', currentToken, i)) {
						case ParseOutcome.Success __ -> queue.pop();
						case ParseOutcome.Invalid invalid -> { return invalid; }
						default -> throw new IllegalStateException();
					}
				}
				case ']' -> {
					switch (checkToken(c,'[', currentToken, i)) {
						case ParseOutcome.Success __ -> queue.pop();
						case ParseOutcome.Invalid invalid -> { return invalid; }
						default -> throw new IllegalStateException();
					}
				}
				case '}' -> {
					switch (checkToken(c,'{', currentToken, i)) {
						case ParseOutcome.Success __ -> queue.pop();
						case ParseOutcome.Invalid invalid -> { return invalid; }
						default -> throw new IllegalStateException();
					}
				}
				case '>' -> {
					switch (checkToken(c,'<', currentToken, i)) {
						case ParseOutcome.Success __ -> queue.pop();
						case ParseOutcome.Invalid invalid -> { return invalid; }
						default -> throw new IllegalStateException();
					}
				}
				default -> throw new IllegalArgumentException("Unsupported character " + c);
			}

			currentToken = queue.isEmpty() ? Token.EMPTY_TOKEN : queue.getFirst();
		}

		if (queue.isEmpty()) {
			return ParseOutcome.Success.OK;
		}

		return new ParseOutcome.Incomplete(queue.stream() //
				.sorted(Comparator.comparingInt(Token::offset).reversed()) //
				.toList());
	}

	public static RulesParser parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<String> rules = in.lines().toList();
			return new RulesParser(rules);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static ParseOutcome checkToken(final char c, final char expectedToken, final Token currentToken, final int position)
	{
		if (currentToken.c() == expectedToken) {
			return ParseOutcome.Success.OK;
		} else if (currentToken == Token.EMPTY_TOKEN) {
			return new ParseOutcome.Invalid.Exceeding(new Token(c, position, Math.max(0, currentToken.depth())));
		} else {
			return new ParseOutcome.Invalid.Unexpected(new Token(c, position, Math.max(0, currentToken.depth())), expectedToken);
		}
	}

	public long scoreInvalidTokens()
	{
		return rules.stream() //
				.map(RulesParser::parse) //
				.filter(outcome -> outcome instanceof ParseOutcome.Invalid) //
				.map(outcome -> (ParseOutcome.Invalid) outcome) //
				.map(ParseOutcome.Invalid::token) //
				.mapToLong(token -> switch (token.c()) {
				case ')' -> 3;
				case ']' -> 57;
				case '}' -> 1197;
				case '>' -> 25137;
				default -> throw new IllegalArgumentException("Unsupported token " + token);
				}).sum();
	}

	public long scoreIncompleteTokens()
	{
		final long[] scores = rules.stream() //
				.map(RulesParser::parse) //
				.filter(outcome -> outcome instanceof ParseOutcome.Incomplete) //
				.map(outcome -> (ParseOutcome.Incomplete) outcome) //
				.mapToLong(incomplete -> incomplete.unclosedTokens().stream() //
						.mapToLong(token -> switch (token.c()) {
						case '(' -> 1;
						case '[' -> 2;
						case '{' -> 3;
						case '<' -> 4;
						default -> throw new IllegalArgumentException("Unsupported token " + token);
						}) //
						.reduce(0, (score, val) -> score * 5 + val) //
				) //
				.sorted() //
				.toArray();
		return scores[scores.length / 2];
	}

	public sealed interface ParseOutcome permits ParseOutcome.Incomplete,ParseOutcome.Invalid,ParseOutcome.Success
	{

		enum Success implements ParseOutcome
		{
			OK;
		}

		sealed interface Invalid extends ParseOutcome permits ParseOutcome.Invalid.Unexpected,ParseOutcome.Invalid.Exceeding
		{

			Token token();

			record Unexpected(Token token, char expected) implements Invalid
			{

			}

			record Exceeding(Token token) implements Invalid
			{

			}

		}

		record Incomplete(List<Token> unclosedTokens) implements ParseOutcome
		{

		}

	}

	public record Token(char c, int offset, int depth)
	{

		public static final Token EMPTY_TOKEN = new Token('\0', -1, -1);

	}

}
