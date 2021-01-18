package com.mscg;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;

public class Calculator {

    private final Queue<Computable> queue;

    public Calculator(List<Computable> queue) {
        this.queue = new LinkedList<>(queue);
    }

    public OptionalLong compute() {
        if (queue.isEmpty()) {
            return OptionalLong.empty();
        }

        Deque<Value> stack = new LinkedList<>();
        while (!queue.isEmpty()) {
            var computable = queue.remove();
            if (computable instanceof Value v) {
                stack.add(v);
            }
            else if (computable instanceof Operator op) {
                Value op2 = stack.removeLast();
                Value op1 = stack.removeLast();
                stack.add(op.compute(op1, op2));
            }
            else {
                throw new IllegalArgumentException("Invalid computable type " + computable.getClass());
            }
        }

        return OptionalLong.of(stack.remove().value());
    }

    private static List<String> extractTokens(String s) {
        var tokenizer = new StringTokenizer(s, " ()", true);
        var tokens = new ArrayList<String>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static Calculator fromString1(String s) {
        return new Calculator(Calculator.parseTokens(extractTokens(s), OperatorToken.FLAT_COMPARATOR));
    }

    public static Calculator fromString2(String s) {
        return new Calculator(Calculator.parseTokens(extractTokens(s), OperatorToken.PRECEDENCE_COMPARATOR));
    }

    private static List<Computable> parseTokens(List<String> tokens, Comparator<OperatorToken> comparator) {
        var queue = new ArrayList<Computable>();
        Deque<OperatorToken> operators = new LinkedList<>();

        for (String token : tokens) {
            final Optional<OperatorToken> optOperatorToken = OperatorToken.fromString(token);
            if (optOperatorToken.isPresent()) {
                var operatorToken = optOperatorToken.get();
                switch (operatorToken) {
                    case OPEN_BRACKET -> operators.add(operatorToken);
                    case CLOSE_BRACKET -> {
                        boolean foundMatchBracket = false;
                        while (!operators.isEmpty() && !foundMatchBracket) {
                            var lastOperatorToken = operators.removeLast();
                            if (lastOperatorToken == OperatorToken.OPEN_BRACKET) {
                                foundMatchBracket = true;
                            }
                            else {
                                queue.add(lastOperatorToken.toOperator());
                            }
                        }
                        if (!foundMatchBracket) {
                            throw new IllegalStateException("Unbalanced brackets in expression");
                        }
                    }
                    default -> {
                        while (!operators.isEmpty()) {
                            var lastOperatorToken = operators.peekLast();
                            if (comparator.compare(lastOperatorToken, operatorToken) >= 0) {
                                queue.add(operators.removeLast().toOperator());
                            }
                            else {
                                break;
                            }
                        }
                        operators.add(operatorToken);
                    }
                }
            }
            else {
                queue.add(new Value(Long.parseLong(token)));
            }
        }

        while (!operators.isEmpty()) {
            var lastOperatorToken = operators.removeLast();
            switch (lastOperatorToken) {
                case OPEN_BRACKET, CLOSE_BRACKET -> throw new IllegalStateException("Unbalanced brackets in expressions");
                default -> queue.add(lastOperatorToken.toOperator());
            }
        }

        return queue;
    }

    @SuppressWarnings("RedundantThrows")
    public static List<Calculator> parseInput1(BufferedReader in) throws Exception {
        return in.lines()
                .map(Calculator::fromString1)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("RedundantThrows")
    public static List<Calculator> parseInput2(BufferedReader in) throws Exception {
        return in.lines()
                .map(Calculator::fromString2)
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    private enum OperatorToken {
        SUM(2),
        MUL(1),
        OPEN_BRACKET(0),
        CLOSE_BRACKET(0);

        public static final Comparator<OperatorToken> FLAT_COMPARATOR = Comparator.comparingInt(o -> switch (o) {
            case SUM, MUL -> 1;
            case OPEN_BRACKET, CLOSE_BRACKET -> 0;
        });
        public static final Comparator<OperatorToken> PRECEDENCE_COMPARATOR = Comparator.comparingInt(o -> o.precedence);

        private final int precedence;

        public Operator toOperator() {
            return switch (this) {
                case SUM -> Operator.SUM;
                case MUL -> Operator.MUL;
                default -> throw new IllegalArgumentException("Operator token " + this + " can't be converted to computable operator");
            };
        }

        public static Optional<OperatorToken> fromString(String token) {
            return switch (token) {
                case "+" -> Optional.of(SUM);
                case "*" -> Optional.of(MUL);
                case "(" -> Optional.of(OPEN_BRACKET);
                case ")" -> Optional.of(CLOSE_BRACKET);
                default -> Optional.empty();
            };
        }
    }

    public interface Computable {

    }

    public static record Value(long value) implements Computable {}

    @RequiredArgsConstructor
    public enum Operator implements Computable {
        SUM((o1, o2) -> o1.value() + o2.value()),
        MUL((o1, o2) -> o1.value() * o2.value());

        private final ToLongBiFunction<Value, Value> op;

        public Value compute(Value v1, Value v2) {
            return new Value(this.op.applyAsLong(v1, v2));
        }
    }

}
