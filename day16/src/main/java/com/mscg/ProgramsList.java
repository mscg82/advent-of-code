package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ProgramsList(List<Instruction> instructions) {

    public String dance(final char[] start) {
        final char[] status = new char[start.length];
        System.arraycopy(start, 0, status, 0, start.length);

        executeDance(status);

        return new String(status);
    }

    private char[] executeDance(final char[] status) {
        for (final var instruction : instructions) {
            instruction.execute(status);
        }

        return status;
    }

    public String multiDance(final char[] start, final int runs) {
        char[] status = new char[start.length];
        System.arraycopy(start, 0, status, 0, start.length);

        final Map<String, char[]> results = new LinkedHashMap<>();
        do {
            status = results.computeIfAbsent(new String(status), s -> executeDance(s.toCharArray()));
        }
        while (!Arrays.equals(status, start));

        final int loopLength = results.size();
        final int remainder = runs % loopLength;
        status = results.entrySet().stream() //
                .skip(remainder - 1) //
                .findFirst() //
                .map(Map.Entry::getValue) //
                .orElseThrow();

        return new String(status);
    }

    public static char[] initialState() {
        final char[] start = new char[16];
        for (int i = 0; i < start.length; i++) {
            start[i] = (char) (i + 'a');
        }

        return start;
    }

    public static ProgramsList parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .flatMap(line -> Arrays.stream(line.split(","))) //
                    .map(Instruction::parse) //
                    .toList();

            return new ProgramsList(instructions);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public interface Instruction {
        void execute(char[] status);

        static Instruction parse(final String instruction) {
            return switch (instruction.charAt(0)) {
                case 's' -> new Spin(Integer.parseInt(instruction.substring(1)));
                case 'x' -> {
                    final var parts = instruction.substring(1).split("/");
                    yield new Exchange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                }
                case 'p' -> {
                    final var parts = instruction.substring(1).split("/");
                    yield new Swap(parts[0].charAt(0), parts[1].charAt(0));
                }
                default -> throw new IllegalArgumentException("Unsupported instruction " + instruction);
            };
        }

    }

    public static record Spin(int amount) implements Instruction {

        @Override
        public void execute(final char[] status) {
            final char[] last = new char[amount];
            System.arraycopy(status, status.length - amount, last, 0, amount);
            System.arraycopy(status, 0, status, amount, status.length - amount);
            System.arraycopy(last, 0, status, 0, amount);
        }

    }

    public static record Exchange(int pos1, int pos2) implements Instruction {

        @Override
        public void execute(final char[] status) {
            final char tmp = status[pos1];
            status[pos1] = status[pos2];
            status[pos2] = tmp;
        }

    }

    public static record Swap(char p1, char p2) implements Instruction {

        @Override
        public void execute(final char[] status) {
            int pos1 = -1;
            int pos2 = -1;
            for (int i = 0; i < status.length; i++) {
                if (status[i] == p1) {
                    pos1 = i;
                } else if (status[i] == p2) {
                    pos2 = i;
                }

                if (pos1 >= 0 && pos2 >= 0) {
                    break;
                }
            }

            final char tmp = status[pos1];
            status[pos1] = status[pos2];
            status[pos2] = tmp;
        }

    }

}
