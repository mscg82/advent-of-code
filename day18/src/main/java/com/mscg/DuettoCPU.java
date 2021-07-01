package com.mscg;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;

@RequiredArgsConstructor
public class DuettoCPU implements ToLongFunction<DuettoCPU.Register> {

    private final Map<Register, Long> registers = new HashMap<>();

    private final List<Instruction> instructions;

    @Override
    public long applyAsLong(final Register register) {
        return register(register);
    }

    public long register(final Register register) {
        return registers.computeIfAbsent(register, __ -> 0L);
    }

    public long register(final Register register, final long value) {
        final Long oldValue = registers.put(register, value);
        return oldValue == null ? 0 : oldValue;
    }

    public void reset() {
        registers.clear();
    }

    public long retrieveSound() {
        int pc = 0;
        while (pc < instructions.size()) {
            final var currentInstruction = instructions.get(pc);
            final int jump = currentInstruction.execute(this);
            pc += jump;
            if (currentInstruction instanceof Rcv && registers.get(SoundRegister.RETRIEVED) != null) {
                return register(SoundRegister.RETRIEVED);
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
        } catch (final UncheckedIOException e) {
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

        long get(ToLongFunction<DuettoCPU.Register> valueExtractor);

        static Value parse(final String val) {
            try {
                return new Constant(Integer.parseInt(val));
            } catch (final NumberFormatException e) {
                return new RegisterVal(new NamedRegister(val.charAt(0)));
            }
        }

    }

    public static record Constant(int value) implements Value {

        @Override
        public long get(final ToLongFunction<DuettoCPU.Register> valueExtractor) {
            return value;
        }

    }

    public static record RegisterVal(NamedRegister register) implements Value {

        @Override
        public long get(final ToLongFunction<DuettoCPU.Register> valueExtractor) {
            return valueExtractor.applyAsLong(register);
        }

    }

    public interface Instruction {

        int execute(DuettoCPU cpu);

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
        public int execute(final DuettoCPU cpu) {
            cpu.register(SoundRegister.OUTPUT, value.get(cpu));
            return 1;
        }

    }

    public static record Set(NamedRegister target, Value value) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, value.get(cpu));
            return 1;
        }

    }

    public static record Add(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) + amount.get(cpu));
            return 1;
        }

    }

    public static record Mul(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) * amount.get(cpu));
            return 1;
        }

    }

    public static record Mod(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) % amount.get(cpu));
            return 1;
        }

    }

    public static record Rcv(Value trigger) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            if (trigger.get(cpu) != 0) {
                cpu.register(SoundRegister.RETRIEVED, cpu.registers.get(SoundRegister.OUTPUT));
            }
            return 1;
        }

    }

    public static record Jgz(Value trigger, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            if (trigger.get(cpu) <= 0) {
                return 1;
            }
            return (int) amount.get(cpu);
        }

    }

}
