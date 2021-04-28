package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AssembunnyCPU {

    private final List<? extends Instruction> instructions;
    private final Map<Register, Long> registers = new EnumMap<>(Register.class);

    private int ic;

    public AssembunnyCPU(final List<? extends Instruction> instructions) {
        this.instructions = instructions;
        reset();
    }

    public AssembunnyCPU register(final Register register, final long value) {
        registers.put(register, value);
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
            ic += instructions.get(ic).execute(registers);
        }
    }

    public static AssembunnyCPU parseInput(final BufferedReader in) throws IOException {
        try {
            final List<? extends Instruction> instructions = in.lines() //
                    .map(line -> {
                        final String[] parts = line.split(" ");
                        return switch (parts[0].toLowerCase()) {
                            case "cpy" -> {
                                try {
                                    yield new CpyConst(Integer.parseInt(parts[1]), Register.fromString(parts[2]));
                                }
                                catch (final NumberFormatException e) {
                                    yield new CpyReg(Register.fromString(parts[1]), Register.fromString(parts[2]));
                                }
                            }
                            case "inc" -> new Inc(Register.fromString(parts[1]));
                            case "dec" -> new Dec(Register.fromString(parts[1]));
                            case "jnz" -> {
                                try {
                                    yield new JnzConst(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                                }
                                catch (final NumberFormatException e) {
                                    yield new JnzReg(Register.fromString(parts[1]), Integer.parseInt(parts[2]));
                                }
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

    public interface Instruction {
        int execute(Map<Register, Long> registers);
    }

    public record CpyConst(long source, Register target) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.put(target, source);
            return 1;
        }

    }

    public record CpyReg(Register source, Register target) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.put(target, registers.get(source));
            return 1;
        }

    }

    public record Inc(Register target) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.compute(target, (__, val) -> val == null ? 1 : val + 1);
            return 1;
        }

    }

    public record Dec(Register target) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.compute(target, (__, val) -> val == null ? -1 : val - 1);
            return 1;
        }

    }

    public record JnzConst(int source, int delta) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            return source != 0 ? delta : 1;
        }

    }

    public record JnzReg(Register source, int delta) implements Instruction {

        @Override
        public int execute(final Map<Register, Long> registers) {
            return registers.get(source) != 0 ? delta : 1;
        }

    }

}
