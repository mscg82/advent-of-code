package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CPU {

    private final List<Instruction> instructions;
    private final Map<String, Long> registers = new HashMap<>();

    public long run() {
        long highestValueWritten = Long.MIN_VALUE;
        for (final var instruction : instructions) {
            final OptionalLong newValue = instruction.run(registers);
            if (newValue.isPresent() && newValue.getAsLong() > highestValueWritten) {
                highestValueWritten = newValue.getAsLong();
            }
        }
        return highestValueWritten;
    }

    public long getLargestValue() {
        return Math.max(0L, registers.values().stream()
                .mapToLong(Long::longValue) //
                .max() //
                .orElseThrow());
    }

    public static CPU parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .map(line -> line.split(" ")) //
                    .map(parts -> new Instruction(parts[0], Operation.from(parts[1]), Long.parseLong(parts[2]), //
                            new Condition(parts[4], Comparison.from(parts[5]), Long.parseLong(parts[6])))) //
                    .toList();
            return new CPU(instructions);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public enum Comparison {
        EQ, NEQ, LT, LTE, GT, GTE;

        public static Comparison from(final String value) {
            return switch (value) {
                case "==" -> EQ;
                case "!=" -> NEQ;
                case "<" -> LT;
                case "<=" -> LTE;
                case ">" -> GT;
                case ">=" -> GTE;
                default -> throw new IllegalArgumentException("Invalid value for comparison: " + value);
            };
        }
    }

    public enum Operation {
        INC, DEC;

        public static Operation from(final String value) {
            return switch (value) {
                case "inc" -> INC;
                case "dec" -> DEC;
                default -> throw new IllegalArgumentException("Invalid value for operation: " + value);
            };
        }
    }

    public record Condition(String register, Comparison comparison, long value) {

        public boolean test(final Map<String, Long> registers) {
            final long value = registers.getOrDefault(register, 0L);
            return switch (comparison) {
                case EQ -> value == this.value;
                case NEQ -> value != this.value;
                case LT -> value < this.value;
                case LTE -> value <= this.value;
                case GT -> value > this.value;
                case GTE -> value >= this.value;
            };
        }

    }

    public record Instruction(String register, Operation operation, long amount, Condition condition) {

        public OptionalLong run(final Map<String, Long> registers) {
            if (condition.test(registers)) {
                return OptionalLong.of(registers.compute(register, (__, oldVal) -> switch (operation) {
                    case INC -> (oldVal == null ? 0L : oldVal) + amount;
                    case DEC -> (oldVal == null ? 0L : oldVal) - amount;
                }));
            }

            return OptionalLong.empty();
        }

    }

}
