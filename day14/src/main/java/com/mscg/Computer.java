package com.mscg;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Computer {

    private final Map<Long, Long> memory;

    private Bitmask bitmask;

    @Getter(AccessLevel.NONE)
    private int currentInstruction;

    private final List<Instruction> instructions;

    public Computer() {
        currentInstruction = 0;
        memory = new HashMap<>();
        instructions = new ArrayList<>();

        BitmaskValue[] bitmaskValues = new BitmaskValue[36];
        Arrays.fill(bitmaskValues, BitmaskValue._X);
        bitmask = new Bitmask(Arrays.asList(bitmaskValues));
    }

    public void reset() {
        currentInstruction = 0;
    }

    public boolean runNext1() {
        if (currentInstruction >= instructions.size()) {
            return false;
        }

        var instruction = instructions.get(currentInstruction++);
        switch (instruction.type()) {
            case MASK -> bitmask = Bitmask.fromString(instruction.value());
            case MEM -> memory.put(instruction.index(), writeWithMask(bitmask, Long.parseLong(instruction.value())));
        }
        return true;
    }

    public boolean runNext2() {
        if (currentInstruction >= instructions.size()) {
            return false;
        }

        var instruction = instructions.get(currentInstruction++);
        switch (instruction.type()) {
            case MASK -> bitmask = Bitmask.fromString(instruction.value());
            case MEM -> Arrays.stream(maskAddresses(bitmask, instruction.index()))
                    .forEach(address -> memory.put(address, Long.parseLong(instruction.value())));
        }
        return true;
    }

    public void run1() {
        reset();
        //noinspection StatementWithEmptyBody
        while (runNext1()) {
        }
    }

    public void run2() {
        reset();
        //noinspection StatementWithEmptyBody
        while (runNext2()) {
        }
    }

    public static long[] maskAddresses(Bitmask bitmask, long address) {
        final var maskedAddress = new Bitmask(Arrays.asList(new BitmaskValue[bitmask.values().size()]));

        BitSet bitset = BitSet.valueOf(new long[]{ address });
        int lastBitIndex = bitmask.values().size() - 1;

        StreamUtils.zipWithIndex(bitmask.values().stream())
                .forEach(idx -> {
                    final int index = (int) idx.getIndex();
                    switch (idx.getValue()) {
                        case _0 -> maskedAddress.values().set(index, bitset.get(lastBitIndex - index) ? BitmaskValue._1 : BitmaskValue._0);
                        case _1 -> maskedAddress.values().set(index, BitmaskValue._1);
                        case _X -> maskedAddress.values().set(index, BitmaskValue._X);
                    }
                });

        List<Bitmask> expanded = maskedAddress.expand();
        return expanded.stream()
                .mapToLong(Bitmask::asLong)
                .toArray();
    }

    public static long writeWithMask(Bitmask bitmask, long value) {
        BitSet bitset = BitSet.valueOf(new long[]{ value });

        int lastBitIndex = bitmask.values().size() - 1;
        StreamUtils.zipWithIndex(bitmask.values().stream())
                .forEach(idx -> {
                    switch (idx.getValue()) {
                        case _0 -> bitset.clear(lastBitIndex - (int) idx.getIndex());
                        case _1 -> bitset.set(lastBitIndex - (int) idx.getIndex());
                        case _X -> { /* do nothing here */ }
                    }
                });

        return bitset.toLongArray()[0];
    }

    public static Computer parseInput(BufferedReader in) throws Exception {
        var computer = new Computer();

        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split(" = ");
            InstructionType type = getInstructionType(line, parts[0]);
            long index = switch (type) {
                case MASK -> 0;
                case MEM -> {
                    int bracketIndex = parts[0].indexOf(']');
                    yield Long.parseLong(parts[0].substring(4, bracketIndex));
                }
            };

            var instruction = new Instruction(type, index, parts[1]);
            computer.getInstructions().add(instruction);
        }

        return computer;
    }

    private static InstructionType getInstructionType(String line, String instructionType) {
        InstructionType type;
        if ("mask".equals(instructionType)) {
            type = InstructionType.MASK;
        } else if (instructionType.startsWith("mem")) {
            type = InstructionType.MEM;
        } else {
            throw new IllegalArgumentException("Invalid instruction " + line);
        }
        return type;
    }

    @RequiredArgsConstructor
    public enum BitmaskValue {
        _X('X'), _0('0'), _1('1');

        @Getter
        private final char value;


        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public record Bitmask(List<BitmaskValue> values) {

        public long asLong() {
            BitSet bitset = new BitSet(values.size());
            for (int i = 0, l = values.size(); i < l; i++) {
                int bitIndex = values.size() - 1 - i;
                switch (values.get(i)) {
                    case _0 -> bitset.clear(bitIndex);
                    case _1 -> bitset.set(bitIndex);
                    case _X -> { /* do nothing here */ }
                }
            }
            return bitset.toLongArray()[0];
        }

        public List<Bitmask> expand() {
            int numOfX = (int) values.stream()
                    .filter(v -> v == BitmaskValue._X)
                    .count();

            var clearedBitmasks = new ArrayList<Bitmask>(1 << numOfX);
            var queue = new ArrayDeque<Bitmask>(1 << numOfX);
            queue.add(this);

            while (!queue.isEmpty()) {
                var nextBitmask = queue.poll();
                int lastX = nextBitmask.values().lastIndexOf(BitmaskValue._X);
                if (lastX < 0) {
                    clearedBitmasks.add(nextBitmask);
                } else {
                    {
                        var fixedValues = new ArrayList<>(nextBitmask.values());
                        fixedValues.set(lastX, BitmaskValue._0);
                        queue.add(new Bitmask(fixedValues));
                    }
                    {
                        var fixedValues = new ArrayList<>(nextBitmask.values());
                        fixedValues.set(lastX, BitmaskValue._1);
                        queue.add(new Bitmask(fixedValues));
                    }
                }
            }

            return clearedBitmasks;
        }

        public static Bitmask fromString(String s) {
            var values = Arrays.asList(new BitmaskValue[36]);

            for (int i = 0, l = s.length(); i < l; i++) {
                values.set(i, switch (s.charAt(i)) {
                    case 'X' -> BitmaskValue._X;
                    case '0' -> BitmaskValue._0;
                    case '1' -> BitmaskValue._1;
                    default -> throw new IllegalArgumentException("Invalid bitmask value " + s);
                });
            }

            return new Bitmask(values);
        }

    }

    public enum InstructionType {
        MASK, MEM
    }

    public record Instruction(InstructionType type, long index, String value) {

    }

}
