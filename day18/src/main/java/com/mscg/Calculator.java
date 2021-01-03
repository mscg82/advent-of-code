package com.mscg;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.util.ArrayList;
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

    public static Calculator fromString(String s) {
        var tokenizer = new StringTokenizer(s, " ()", true);
        var tokens = new ArrayList<String>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }

        return new Calculator(Calculator.parseTokens(tokens));
    }

    private static List<Computable> parseTokens(List<String> tokens) {
        var queue = new ArrayList<Computable>();
        Operator op = null;

        for (int i = 0, l = tokens.size(); i < l; i++) {
            final String token = tokens.get(i);
            if (token.equals("(")) {
                final int startIdx = ++i;
                int open = 0;
                loop: for (; i < l; i++) {
                    String innerToken = tokens.get(i);
                    switch (innerToken) {
                        case "(" -> open++;
                        case ")" -> {
                            if (open != 0) {
                                open--;
                            }
                            else {
                                break loop;
                            }
                        }
                        default -> {}
                    }
                }
                queue.addAll(parseTokens(tokens.subList(startIdx, i)));
                continue;
            }

            var optOperator = Operator.fromString(token);
            if (optOperator.isEmpty()) {
                queue.add(new Value(Long.parseLong(token)));
            }
            else {
                if (op != null) {
                    queue.add(op);
                }
                op = optOperator.get();
            }
        }

        if (op != null) {
            queue.add(op);
        }

        return queue;
    }

    @SuppressWarnings("RedundantThrows")
    public static List<Calculator> parseInput(BufferedReader in) throws Exception {
        return in.lines()
                .map(Calculator::fromString)
                .collect(Collectors.toList());
    }

    public sealed interface Computable {

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

        public static Optional<Operator> fromString(String s) {
            return switch (s) {
                case "+" -> Optional.of(SUM);
                case "*" -> Optional.of(MUL);
                default -> Optional.empty();
            };
        }
    }

}
