package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuettoCPU {

    private final Map<Register, Integer> registers = new HashMap<>();
    private final List<Instruction> instructions;
    private int pc;

    public void reset() {
        registers.clear();
    }

    public int retrieveSound() {
        pc = 0;
        while (pc < instructions.size()) {
            var currentInstruction = instructions.get(pc);
            int jump = currentInstruction.execute(registers);
            pc += jump;
            if (currentInstruction instanceof Rcv && registers.get(SoundRegister.RETRIEVED) != null) {
                return registers.get(SoundRegister.RETRIEVED);
            }
        }
        throw new IllegalStateException("Can't retrieve sound");
    }

    public static DuettoCPU parseInput(BufferedReader in) throws IOException {
        try {
            List<Instruction> instructions = in.lines() //
                    .map(Instruction::parse) //
                    .toList();
            return new DuettoCPU(instructions);
        }
        catch (UncheckedIOException e) {
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
        int get(Map<Register, Integer> registers);

        static Value parse(String val) {
            try {
                return new Constant(Integer.parseInt(val));
            }
            catch (NumberFormatException e) {
                return new RegisterVal(new NamedRegister(val.charAt(0)));
            }
        }
    }

    public static record Constant(int value) implements Value {
        @Override
        public int get(Map<Register, Integer> registers) {
            return value;
        }
    }

    public static record RegisterVal(NamedRegister register) implements Value {
        @Override
        public int get(Map<Register, Integer> registers) {
            return registers.computeIfAbsent(register, __ -> 0);
        }
    }

    public interface Instruction {
        int execute(Map<Register, Integer> registers);

        static Instruction parse(String line) {
            String[] parts = line.split(" ");
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
        public int execute(Map<Register, Integer> registers) {
            registers.put(SoundRegister.OUTPUT, value.get(registers));
            return 1;
        }
    }

    public static record Set(NamedRegister target, Value value) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            registers.put(target, value.get(registers));
            return 1;
        }
    }

    public static record Add(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) + amount.get(registers));
            return 1;
        }
    }

    public static record Mul(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) * amount.get(registers));
            return 1;
        }
    }

    public static record Mod(NamedRegister target, Value amount) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            registers.compute(target, (__, value) -> (value == null ? 0 : value) % amount.get(registers));
            return 1;
        }
    }

    public static record Rcv(Value trigger) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            if (trigger.get(registers) != 0) {
                registers.put(SoundRegister.RETRIEVED, registers.get(SoundRegister.OUTPUT));
            }
            return 1;
        }
    }

    public static record Jgz(Value trigger, Value amount) implements Instruction {
        @Override
        public int execute(Map<Register, Integer> registers) {
            if (trigger.get(registers) <= 0) {
                return 1;
            }
            return amount.get(registers);
        }
    }
}
