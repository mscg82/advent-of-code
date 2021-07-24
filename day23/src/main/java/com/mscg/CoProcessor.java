package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoProcessor implements ToLongFunction<CoProcessor.Register> {

    private final Map<Register, Long> registers = new EnumMap<>(Register.class);

    private final List<Instruction> instructions;

    public long register(final Register register) {
        return registers.computeIfAbsent(register, __ -> 0L);
    }

    @SuppressWarnings("UnusedReturnValue")
    public long register(final Register register, final long value) {
        final Long oldValue = registers.put(register, value);
        return oldValue == null ? 0 : oldValue;
    }

    @Override
    public long applyAsLong(final Register register) {
        return register(register);
    }

    public void run(final boolean optimized) {
        int pc = 0;
        if (!optimized) {
            while (pc < instructions.size() && !Thread.currentThread().isInterrupted()) {
                final var currentInstruction = instructions.get(pc);
                final int jump = currentInstruction.execute(this);
                pc += jump;
            }
        } else {
            while (pc < instructions.size() && pc <= 8 && !Thread.currentThread().isInterrupted()) {
                final var currentInstruction = instructions.get(pc);
                final int jump = currentInstruction.execute(this);
                pc += jump;
            }

            final long c = register(Register.C);
            for (long b = register(Register.B); b <= c; b += 17) {
                register(Register.B, b);
                final BigInteger big = new BigInteger(String.valueOf(b), 10);
                if (!big.isProbablePrime(5)) {
                    register(Register.H, register(Register.H) + 1);
                }
            }
        }
    }

    public static CoProcessor parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .map(Instruction::parse) //
                    .toList();
            return new CoProcessor(instructions);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public enum Register {
        A, B, C, D, E, F, G, H, MUL_STAT;

        public static Register from(final char c) {
            return switch (c) {
                case 'a', 'A' -> A;
                case 'b', 'B' -> B;
                case 'c', 'C' -> C;
                case 'd', 'D' -> D;
                case 'e', 'E' -> E;
                case 'f', 'F' -> F;
                case 'g', 'G' -> G;
                case 'h', 'H' -> H;
                default -> throw new IllegalArgumentException("Unknown register name " + c);
            };
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public interface Value {

        long get(ToLongFunction<Register> valueExtractor);

        static Value parse(final String val) {
            try {
                return new Constant(Integer.parseInt(val));
            }
            catch (final NumberFormatException e) {
                return new RegisterVal(Register.from(val.charAt(0)));
            }
        }

    }

    public static record Constant(int value) implements Value {

        @Override
        public long get(final ToLongFunction<Register> valueExtractor) {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static record RegisterVal(Register register) implements Value {

        @Override
        public long get(final ToLongFunction<Register> valueExtractor) {
            return valueExtractor.applyAsLong(register);
        }

        @Override
        public String toString() {
            return register.toString();
        }
    }

    public interface Instruction {

        int execute(CoProcessor cpu);

        static Instruction parse(final String line) {
            final String[] parts = line.split(" ");
            return switch (parts[0]) {
                case "set" -> new Set(Register.from(parts[1].charAt(0)), Value.parse(parts[2]));
                case "sub" -> new Sub(Register.from(parts[1].charAt(0)), Value.parse(parts[2]));
                case "mul" -> new Mul(Register.from(parts[1].charAt(0)), Value.parse(parts[2]));
                case "jnz" -> new Jnz(Value.parse(parts[1]), Value.parse(parts[2]));
                default -> throw new IllegalArgumentException("Unsupported instruction " + line);
            };
        }

    }

    public static record Set(Register target, Value value) implements Instruction {

        @Override
        public int execute(final CoProcessor cpu) {
            cpu.register(target, value.get(cpu));
            return 1;
        }

        @Override
        public String toString() {
            return "set " + target + " " + value;
        }
    }

    public static record Sub(Register target, Value amount) implements Instruction {

        @Override
        public int execute(final CoProcessor cpu) {
            cpu.register(target, cpu.register(target) - amount.get(cpu));
            return 1;
        }

        @Override
        public String toString() {
            return "sub " + target + " " + amount;
        }
    }

    public static record Mul(Register target, Value amount) implements Instruction {

        @Override
        public int execute(final CoProcessor cpu) {
            cpu.register(target, cpu.register(target) * amount.get(cpu));
            cpu.register(Register.MUL_STAT, cpu.register(Register.MUL_STAT) + 1);
            return 1;
        }

        @Override
        public String toString() {
            return "mul " + target + " " + amount;
        }
    }

    public static record Jnz(Value trigger, Value amount) implements Instruction {

        @Override
        public int execute(final CoProcessor cpu) {
            if (trigger.get(cpu) == 0) {
                return 1;
            }
            return (int) amount.get(cpu);
        }

        @Override
        public String toString() {
            return "jnz " + trigger + " " + amount;
        }
    }

}
