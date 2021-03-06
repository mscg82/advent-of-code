package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
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
        registers[register.ordinal()] = value;
    }

    @Override
    public void run() {
        while (pic < instructions.size()) {
            var instruction = instructions.get(pic);

            switch (instruction.code()) {
                case hlf -> registers[instruction.register().ordinal()] /= 2;
                case tpl -> registers[instruction.register().ordinal()] *= 3;
                case inc -> registers[instruction.register().ordinal()] += 1;
                case jmp, jie, jio -> {
                }
            }

            if (registers[Register.a.ordinal()] < 0) {
                throw new IllegalStateException("Register a has an invalid value");
            }
            if (registers[Register.b.ordinal()] < 0) {
                throw new IllegalStateException("Register b has an invalid value");
            }

            pic = switch (instruction.code()) {
                case hlf, tpl, inc -> pic + 1;
                case jmp -> pic + instruction.offset();
                case jie -> pic + (register(instruction.register()) % 2 == 0 ? instruction.offset() : 1);
                case jio -> pic + (register(instruction.register()) == 1 ? instruction.offset() : 1);
            };
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
