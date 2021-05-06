package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongUnaryOperator;

import com.codepoetics.protonpack.StreamUtils;

@SuppressWarnings("SpellCheckingInspection")
public class AssembunnyCPU2 {

    private final List<? extends Instruction> instructions;
    private final Map<Register, Long> registers = new EnumMap<>(Register.class);
    private final Map<Instruction, Boolean> switches = new HashMap<>();

    private int ic;

    public AssembunnyCPU2(final List<? extends Instruction> instructions) {
        this.instructions = instructions;
        reset();
    }

    @SuppressWarnings("UnusedReturnValue")
    public AssembunnyCPU2 register(final Register register, final long value) {
        registers.put(register, value);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AssembunnyCPU2 register(final Register register, final LongUnaryOperator valueComputer) {
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

    public void run(final boolean optimized) {
        while (ic < instructions.size()) {
            if (optimized && ic == 4) {
                register(Register.A, register(Register.B) * register(Register.D));
                register(Register.C, 0);
                register(Register.D, 0);
                ic = 10;
            } else {
                ic += instructions.get(ic).execute(this);
            }
        }
    }

    public static AssembunnyCPU2 parseInput(final BufferedReader in) throws IOException {
        try {
            final List<? extends Instruction> instructions = StreamUtils.zipWithIndex(in.lines()) //
                    .map(idx -> {
                        final int lineNumber = (int) idx.getIndex();
                        final String line = idx.getValue();
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
                                yield new Cpy(lineNumber, source, Register.fromString(parts[2]));
                            }
                            case "inc" -> new Inc(lineNumber, Register.fromString(parts[1]));
                            case "dec" -> new Dec(lineNumber, Register.fromString(parts[1]));
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
                                yield new Jnz(lineNumber, source, delta);
                            }
                            case "tgl" -> new Toggle(lineNumber, Register.fromString(parts[1]));
                            default -> throw new IllegalArgumentException("Invalid instruction " + line);
                        };
                    }) //
                    .toList();
            return new AssembunnyCPU2(instructions);
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

    public interface Instruction {

        default boolean isNotSwitched(final AssembunnyCPU2 cpu) {
            return !cpu.switches.getOrDefault(this, Boolean.FALSE);
        }

        int execute(AssembunnyCPU2 cpu);
    }

    public interface Value {

        long getValue(AssembunnyCPU2 cpu);

    }

    public record RegisterValue(Register register) implements Value {

        @Override
        public long getValue(final AssembunnyCPU2 cpu) {
            return cpu.register(register);
        }

    }

    public record ConstValue(long value) implements Value {

        @Override
        public long getValue(final AssembunnyCPU2 cpu) {
            return value;
        }

    }

    public record Cpy(int line, Value source, Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU2 cpu) {
            if (isNotSwitched(cpu)) {
                cpu.register(target, source.getValue(cpu));
                return 1;
            } else {
                return source.getValue(cpu) != 0 ? (int) cpu.register(target) : 1;
            }
        }

    }

    public record Inc(int line, Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU2 cpu) {
            if (isNotSwitched(cpu)) {
                cpu.register(target, val -> val + 1);
            } else {
                cpu.register(target, val -> val - 1);
            }
            return 1;
        }

    }

    public record Dec(int line, Register target) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU2 cpu) {
            if (isNotSwitched(cpu)) {
                cpu.register(target, val -> val - 1);
            } else {
                cpu.register(target, val -> val + 1);
            }
            return 1;
        }

    }

    public record Jnz(int line, Value source, Value delta) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU2 cpu) {
            if (isNotSwitched(cpu)) {
                return source.getValue(cpu) != 0 ? (int) delta.getValue(cpu) : 1;
            } else {
                if (delta instanceof RegisterValue target) {
                    cpu.register(target.register(), source.getValue(cpu));
                }
                return 1;
            }
        }

    }

    public record Toggle(int line, Register delta) implements Instruction {

        @Override
        public int execute(final AssembunnyCPU2 cpu) {
            if (isNotSwitched(cpu)) {
                final int index = cpu.ic + (int) cpu.register(delta);
                if (index < cpu.instructions.size()) {
                    final Instruction instruction = cpu.instructions.get(index);
                    cpu.switches.compute(instruction, (__, s) -> s == null ? Boolean.TRUE : !s);
                }
            } else {
                cpu.register(delta, val -> val + 1);
            }
            return 1;
        }

    }

}
