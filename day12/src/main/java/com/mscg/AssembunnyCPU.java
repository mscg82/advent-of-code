package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongUnaryOperator;

@SuppressWarnings("SpellCheckingInspection")
public class AssembunnyCPU {

    private final List<? extends Instruction> instructions;
    private final Map<Register, Long> registers = new EnumMap<>(Register.class);

    private int ic;

    public AssembunnyCPU(final List<? extends Instruction> instructions) {
        this.instructions = instructions;
        reset();
    }

    @SuppressWarnings("UnusedReturnValue")
    public AssembunnyCPU register(final Register register, final long value) {
        registers.put(register, value);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AssembunnyCPU register(final Register register, final LongUnaryOperator valueComputer) {
        registers.compute(register, (__, val) -> valueComputer.applyAsLong(val == null ? 0L : val));
        return this;
    }

    public long register(final Register register) {
        return registers.get(register);
    }

    public void reset() {
        ic = 0;
        for (final var register : Register.values()) {
            registers.put(register, 0L);
        }
    }

    public void run() {
        while (ic < instructions.size()) {
            ic += instructions.get(ic).execute(this);
        }
    }

    public static AssembunnyCPU parseInput(final BufferedReader in) throws IOException {
        try {
            final List<? extends Instruction> instructions = in.lines() //
                    .map(line -> {
                        final String[] parts = line.split(" ");
                        return switch (parts[0].toLowerCase()) {
                            case "cpy" -> {
                                Value source;
                                try {
                                    source = new ConstValue(Long.parseLong(parts[1]));
                                }
                                catch (final NumberFormatException e) {
                                    source = new RegisterValue(Register.fromString(parts[1]));
                                }
                                yield new Cpy(source, Register.fromString(parts[2]));
                            }
                            case "inc" -> new Inc(Register.fromString(parts[1]));
                            case "dec" -> new Dec(Register.fromString(parts[1]));
                            case "jnz" -> {
                                Value source;
                                try {
                                    source = new ConstValue(Long.parseLong(parts[1]));
                                }
                                catch (final NumberFormatException e) {
                                    source = new RegisterValue(Register.fromString(parts[1]));
                                }
                                Value delta;
                                try {
                                    delta = new ConstValue(Long.parseLong(parts[2]));
                                }
                                catch (final NumberFormatException e) {
                                    delta = new RegisterValue(Register.fromString(parts[2]));
                                }
                                yield new Jnz(source, delta);
                            }
                            default -> throw new IllegalArgumentException("Invalid instruction " + line);
                        };
                    }) //
                    .toList();
            return new AssembunnyCPU(instructions);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public enum Register {
        A, B, C, D;

        public static Register fromString(final String s) {
            return switch (s) {
                case "a", "A" -> Register.A;
                case "b", "B" -> Register.B;
                case "c", "C" -> Register.C;
                case "d", "D" -> Register.D;
                default -> throw new IllegalArgumentException("Invalid register name " + s);
            };
        }
    }

    public interface Value {

        long getValue(AssembunnyCPU cpu);

    }

    public record RegisterValue(Register register) implements Value {

        @Override
        public long getValue(final AssembunnyCPU cpu) {
            return cpu.register(register);
        }

    }

    public record ConstValue(long value) implements Value {

        @Override
        public long getValue(final AssembunnyCPU cpu) {
            return value;
        }

    }

    public interface Instruction {
        int execute(AssembunnyCPU cpu);
    }

    public record Cpy(Value source, Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU cpu) {
            cpu.register(target, source.getValue(cpu));
            return 1;
        }

    }

    public record Inc(Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU cpu) {
            cpu.register(target, val -> val + 1);
            return 1;
        }

    }

    public record Dec(Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU cpu) {
            cpu.register(target, val -> val - 1);
            return 1;
        }

    }

    public record Jnz(Value source, Value delta) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU cpu) {
            return source.getValue(cpu) != 0 ? (int) delta.getValue(cpu) : 1;
        }

    }

}
