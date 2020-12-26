package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdventDay14Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(in);
            Assertions.assertEquals(List.of(
                    new Computer.Instruction(Computer.InstructionType.MASK, 0, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X"),
                    new Computer.Instruction(Computer.InstructionType.MEM, 8, "11"),
                    new Computer.Instruction(Computer.InstructionType.MEM, 7, "101"),
                    new Computer.Instruction(Computer.InstructionType.MEM, 8, "0")
                    ),
                    computer.getInstructions());

            var bitmask = Computer.Bitmask.fromString(computer.getInstructions().get(0).value());
            Assertions.assertArrayEquals(new Computer.BitmaskValue[] {
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._1, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._0, Computer.BitmaskValue._X,
            }, bitmask.values().toArray(new Computer.BitmaskValue[0]));
        }
    }

    @Test
    public void testMaskedWrite() {
        var bitmask = Computer.Bitmask.fromString("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X");
        Assertions.assertEquals(73L, Computer.writeWithMask(bitmask, 11L));
        Assertions.assertEquals(101L, Computer.writeWithMask(bitmask, 101L));
        Assertions.assertEquals(64L, Computer.writeWithMask(bitmask, 0L));
    }

    @Test
    public void testRun1() throws Exception {
        try (BufferedReader in = readInput()) {
            var computer = Computer.parseInput(in);
            Assertions.assertTrue(computer.runNext1());
            Assertions.assertArrayEquals(new Computer.BitmaskValue[] {
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._1, Computer.BitmaskValue._X, Computer.BitmaskValue._X,
                    Computer.BitmaskValue._X, Computer.BitmaskValue._X, Computer.BitmaskValue._0, Computer.BitmaskValue._X,
            }, computer.getBitmask().values().toArray(new Computer.BitmaskValue[0]));

            Assertions.assertTrue(computer.runNext1());
            Assertions.assertEquals(Map.of(
                    8L, 73L),
                    computer.getMemory());

            Assertions.assertTrue(computer.runNext1());
            Assertions.assertEquals(Map.of(
                    7L, 101L,
                    8L, 73L),
                    computer.getMemory());

            Assertions.assertTrue(computer.runNext1());
            Assertions.assertEquals(Map.of(
                    7L, 101L,
                    8L, 64L),
                    computer.getMemory());

            Assertions.assertFalse(computer.runNext1());

            computer.run1();
            Assertions.assertEquals(Map.of(
                    7L, 101L,
                    8L, 64L),
                    computer.getMemory());
        }
    }

    @Test
    public void testRun2() {
        var computer = new Computer();
        computer.getInstructions().add(new Computer.Instruction(Computer.InstructionType.MASK, 0, "000000000000000000000000000000X1001X"));
        computer.getInstructions().add(new Computer.Instruction(Computer.InstructionType.MEM, 42, "100"));
        computer.getInstructions().add(new Computer.Instruction(Computer.InstructionType.MASK, 0, "00000000000000000000000000000000X0XX"));
        computer.getInstructions().add(new Computer.Instruction(Computer.InstructionType.MEM, 26, "1"));

        computer.run2();
        Assertions.assertEquals(Map.ofEntries(
                Map.entry(58L, 100L),
                Map.entry(59L, 100L),
                Map.entry(16L, 1L),
                Map.entry(17L, 1L),
                Map.entry(18L, 1L),
                Map.entry(19L, 1L),
                Map.entry(24L, 1L),
                Map.entry(25L, 1L),
                Map.entry(26L, 1L),
                Map.entry(27L, 1L)),
                computer.getMemory());
    }

    @Test
    public void testBitmaskAsLong() {
        {
            var bitmask = Computer.Bitmask.fromString("000000000000000000000000000000010000");
            Assertions.assertEquals(16L, bitmask.asLong());
        }
        {
            var bitmask = Computer.Bitmask.fromString("000000000000000000000000000000011001");
            Assertions.assertEquals(25L, bitmask.asLong());
        }
        {
            var bitmask = Computer.Bitmask.fromString("000000000000000000000000000000011011");
            Assertions.assertEquals(27L, bitmask.asLong());
        }
    }

    @Test
    public void testExpandMask() {
        {
            var bitmask = Computer.Bitmask.fromString("000000000000000000000000000000X1101X");
            List<Computer.Bitmask> expanded = bitmask.expand();
            Assertions.assertEquals(Set.of(
                    Computer.Bitmask.fromString("000000000000000000000000000000011010"),
                    Computer.Bitmask.fromString("000000000000000000000000000000011011"),
                    Computer.Bitmask.fromString("000000000000000000000000000000111010"),
                    Computer.Bitmask.fromString("000000000000000000000000000000111011")
                    ),
                    new HashSet<>(expanded));
        }
        {
            var bitmask = Computer.Bitmask.fromString("00000000000000000000000000000001X0XX");
            List<Computer.Bitmask> expanded = bitmask.expand();
            Assertions.assertEquals(Set.of(
                    Computer.Bitmask.fromString("000000000000000000000000000000010000"),
                    Computer.Bitmask.fromString("000000000000000000000000000000010001"),
                    Computer.Bitmask.fromString("000000000000000000000000000000010010"),
                    Computer.Bitmask.fromString("000000000000000000000000000000010011"),
                    Computer.Bitmask.fromString("000000000000000000000000000000011000"),
                    Computer.Bitmask.fromString("000000000000000000000000000000011001"),
                    Computer.Bitmask.fromString("000000000000000000000000000000011010"),
                    Computer.Bitmask.fromString("000000000000000000000000000000011011")
                    ),
                    new HashSet<>(expanded));
        }
    }

    @Test
    public void testMaskAddress() {
        {
            var bitmask = Computer.Bitmask.fromString("000000000000000000000000000000X1001X");
            long address = 42;
            long[] maskedAddresses = Computer.maskAddresses(bitmask, address);
            Arrays.sort(maskedAddresses);
            Assertions.assertArrayEquals(new long[] { 26L, 27L, 58L, 59L}, maskedAddresses);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
