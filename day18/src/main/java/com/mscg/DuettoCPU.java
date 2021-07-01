package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuettoCPU {

    private final Map<Register, Long> registers = new HashMap<>();
    private final List<Instruction> instructions;

    public void reset() {
        registers.clear();
    }

    public long retrieveSound() {
        int pc = 0;
        while (pc < instructions.size()) {
            final var currentInstruction = instructions.get(pc);
            final int jump = currentInstruction.execute(registers);
            pc += jump;
            if (currentInstruction instanceof Rcv && registers.get(SoundRegister.RETRIEVED) != null) {
                return registers.get(SoundRegister.RETRIEVED);
            }
        }
        throw new IllegalStateException("Can't retrieve sound");
    }

    public static DuettoCPU parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .map(Instruction::parse) //
                    .toList();
            return new DuettoCPU(instructions);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public interface Register {
    }

    public enum SoundRegister implements Register {
        OUTPUT, RETRIEVED
    }

    public static record NamedRegister(char name) implements Register {

    }

    public interface Value {
        long get(Map<Register, Long> registers);

        static Value parse(final String val) {
            try {
                return new Constant(Integer.parseInt(val));
            }
            catch (final NumberFormatException e) {
                return new RegisterVal(new NamedRegister(val.charAt(0)));
            }
        }
    }

    public static record Constant(int value) implements Value {
        @Override
        public long get(final Map<Register, Long> registers) {
            return value;
        }
    }

    public static record RegisterVal(NamedRegister register) implements Value {
        @Override
        public long get(final Map<Register, Long> registers) {
            return registers.computeIfAbsent(register, __ -> 0L);
        }
    }

    public interface Instruction {
        int execute(Map<Register, Long> registers);

        static Instruction parse(final String line) {
            final String[] parts = line.split(" ");
            return switch (parts[0]) {
                case "snd" -> new Snd(Value.parse(parts[1]));
                case "set" -> new Set(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "add" -> new Add(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "mul" -> new Mul(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "mod" -> new Mod(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "rcv" -> new Rcv(Value.parse(parts[1]));
                case "jgz" -> new Jgz(Value.parse(parts[1]), Value.parse(parts[2]));
                default -> throw new IllegalArgumentException("Unsupported instruction " + line);
            };
        }
    }

    public static record Snd(Value value) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.put(SoundRegister.OUTPUT, value.get(registers));
            return 1;
        }
    }

    public static record Set(NamedRegister target, Value value) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.put(target, value.get(registers));
            return 1;
        }
    }

    public static record Add(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) + amount.get(registers));
            return 1;
        }
    }

    public static record Mul(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) * amount.get(registers));
            return 1;
        }
    }

    public static record Mod(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) % amount.get(registers));
            return 1;
        }
    }

    public static record Rcv(Value trigger) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            if (trigger.get(registers) != 0) {
                registers.put(SoundRegister.RETRIEVED, registers.get(SoundRegister.OUTPUT));
            }
            return 1;
        }
    }

    public static record Jgz(Value trigger, Value amount) implements Instruction {
        @Override
        public int execute(final Map<Register, Long> registers) {
            if (trigger.get(registers) <= 0) {
                return 1;
            }
            return (int) amount.get(registers);
        }
    }
}
