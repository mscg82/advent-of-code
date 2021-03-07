package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;

public class Computer implements Runnable {

    @Getter
    private final List<Instruction> instructions;
    private final long[] registers;
    private int pic;

    public Computer(List<Instruction> instructions) {
        this.instructions = instructions;
        this.registers = new long[] { 0, 0 };
        this.pic = 0;
    }

    public long register(@NonNull Register register) {
        return registers[register.ordinal()];
    }

    public void register(@NonNull Register register, long value) {
        registers[register.ordinal()] = validateRegisterValue(value, register);
    }

    public void register(@NonNull Register register, LongUnaryOperator valueTransformer) {
        registers[register.ordinal()] = validateRegisterValue(
                valueTransformer.applyAsLong(registers[register.ordinal()]), register);
    }

    private long validateRegisterValue(long value, @NonNull Register register) {
        if (value < 0) {
            throw new IllegalStateException("Value " + value + " in invalid for register " + register.name());
        }
        return value;
    }

    @Override
    public void run() {
        while (pic < instructions.size()) {
            var instruction = instructions.get(pic);

            int picIncrement = switch (instruction.code()) {
                case hlf -> {
                    register(instruction.register(), v -> v / 2);
                    yield 1;
                }
                case tpl -> {
                    register(instruction.register(), v -> v * 3);
                    yield 1;
                }
                case inc -> {
                    register(instruction.register(), v -> v + 1);
                    yield 1;
                }
                case jmp -> instruction.offset();
                case jie -> register(instruction.register()) % 2 == 0 ? instruction.offset() : 1;
                case jio -> register(instruction.register()) == 1 ? instruction.offset() : 1;
            };

            pic += picIncrement;
        }
    }

    public static Computer parseInput(BufferedReader in) throws IOException {
        List<Instruction> instructions = in.lines() //
                .map(line -> {
                    int index = line.indexOf(' ');
                    InstructionCode code = InstructionCode.valueOf(line.substring(0, index));
                    Register register = switch (code) {
                        case jmp -> null;
                        case hlf, tpl, inc, jie, jio -> Register.valueOf(line.substring(index + 1, index + 2));
                    };
                    int offset = switch (code) {
                        case hlf, tpl, inc -> 0;
                        case jmp -> Integer.parseInt(line.substring(index + 1).trim());
                        case jie, jio -> Integer.parseInt(line.substring(index + 3).trim());
                    };
                    return new Instruction(code, register, offset);
                }) //
                .collect(Collectors.toUnmodifiableList());
        return new Computer(instructions);
    }

    public enum InstructionCode {
        hlf, tpl, inc, jmp, jie, jio;
    }

    public enum Register {
        a, b
    }

    public static record Instruction(InstructionCode code, Register register, int offset) {
        @Override
        public String toString() {
            return switch (code) {
                case hlf, tpl, inc -> code.name() + " " + register;
                case jmp -> code.name() + " %+d".formatted(offset);
                case jie, jio -> code.name() + " " + register + ", %+d".formatted(offset);
            };

        }
    }

}
