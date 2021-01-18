package com.mscg;

import lombok.Getter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Interpreter {

    private int pic;

    @Getter
    private int accumulator;

    @Getter
    private final List<Instruction> instructions = new ArrayList<>();

    public Result run() {
        return run(instructions);
    }

    private Result run(List<Instruction> instructions) {
        pic = 0;
        accumulator = 0;
        var debugger = new BitSet(this.instructions.size());
        boolean correct = true;

        while (pic < this.instructions.size()) {
            if (debugger.get(pic)) {
                correct = false;
                break;
            }
            debugger.set(pic);

            var instruction = instructions.get(pic);
            int picIncrement = switch (instruction.opCode()) {
                case NOP -> 1;
                case ACC -> {
                    accumulator += instruction.data();
                    yield 1;
                }
                case JMP -> instruction.data();
            };

            pic += picIncrement;
        }

        return new Result(correct, accumulator);
    }

    public OptionalInt runFixed() {
        for (int i = 0, l = instructions.size(); i < l; i++) {
            var instruction = instructions.get(i);
            if (instruction.opCode() == OpCodes.ACC) {
                continue;
            }

            var fixedInstructions = new ArrayList<>(instructions);
            switch (instruction.opCode) {
                case NOP -> fixedInstructions.set(i, new Instruction(OpCodes.JMP, instruction.data()));
                case JMP -> fixedInstructions.set(i, new Instruction(OpCodes.NOP, instruction.data()));
                case ACC -> { /* DO NOTHING HERE */ }
            }

            Result result = run(fixedInstructions);
            if (result.correct()) {
                return OptionalInt.of(result.result());
            }
        }

        return OptionalInt.empty();
    }

    public static Interpreter parseInput(BufferedReader in) throws Exception {
        var interpreter = new Interpreter();

        int row = 0;
        String line;
        while ((line = in.readLine()) != null) {
            int currentRow = ++row;
            Instruction.fromString(line)
                .ifPresentOrElse(interpreter.getInstructions()::add, () -> System.out.println("Invalid instruction on line " + currentRow));
        }

        return interpreter;
    }

    public static record Result(boolean correct, int result) {}

    public static record Instruction(OpCodes opCode, int data) {

        public static Optional<Instruction> fromString(String s) {
            String[] parts = s.split(" ");
            if (parts.length != 2) {
                return Optional.empty();
            }

            return OpCodes.fromString(parts[0]).flatMap(opCode -> {
                try {
                    return Optional.of(new Instruction(opCode, Integer.parseInt(parts[1])));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
        }

    }

    public enum OpCodes {
        NOP,
        ACC,
        JMP;

        public static Optional<OpCodes> fromString(String s) {
            return switch (s) {
                case "nop" -> Optional.of(OpCodes.NOP);
                case "acc" -> Optional.of(OpCodes.ACC);
                case "jmp" -> Optional.of(OpCodes.JMP);
                default -> Optional.empty();
            };
        }
    }

}
