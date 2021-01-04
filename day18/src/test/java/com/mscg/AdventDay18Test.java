package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.OptionalLong;

public class AdventDay18Test {

    @Test
    public void testSimpleExpr() {
        {
            var calculator = new Calculator(List.of(
                    new Calculator.Value(1),
                    new Calculator.Value(2),
                    new Calculator.Value(3),
                    Calculator.Operator.MUL,
                    Calculator.Operator.SUM));
            Assertions.assertEquals(OptionalLong.of(7), calculator.compute());
        }

        {
            var calculator = new Calculator(List.of(
                    new Calculator.Value(1),
                    new Calculator.Value(2),
                    Calculator.Operator.SUM,
                    new Calculator.Value(3),
                    Calculator.Operator.MUL));
            Assertions.assertEquals(OptionalLong.of(9), calculator.compute());
        }
    }

    @Test
    public void testParseSingle1() {
        {
            var calculator = Calculator.fromString1("1 + 2 * 3");
            Assertions.assertEquals(OptionalLong.of(9), calculator.compute());
        }
        {
            var calculator = Calculator.fromString1("1 + (2 * 3)");
            Assertions.assertEquals(OptionalLong.of(7), calculator.compute());
        }
        {
            var calculator = Calculator.fromString1("(1 + 2) * 3");
            Assertions.assertEquals(OptionalLong.of(9), calculator.compute());
        }
        {
            var calculator = Calculator.fromString1("(1 + (2 * 3)) + 4");
            Assertions.assertEquals(OptionalLong.of(11), calculator.compute());
        }
        {
            var calculator = Calculator.fromString1("((1 + 2) * (2 + 3)) + 4");
            Assertions.assertEquals(OptionalLong.of(19), calculator.compute());
        }
    }

    @Test
    public void testParse1() throws Exception {
        try (BufferedReader in = readInput()) {
            final long[] values = Calculator.parseInput1(in).stream()
                    .map(Calculator::compute)
                    .mapToLong(OptionalLong::orElseThrow)
                    .toArray();
            Assertions.assertArrayEquals(new long[] { 71, 51, 26, 437, 12240, 13632 }, values);
        }
    }

    @Test
    public void testParseSingle2() {
        {
            var calculator = Calculator.fromString2("1 + 2 * 3");
            Assertions.assertEquals(OptionalLong.of(9), calculator.compute());
        }
        {
            var calculator = Calculator.fromString2("1 + (2 * 3)");
            Assertions.assertEquals(OptionalLong.of(7), calculator.compute());
        }
        {
            var calculator = Calculator.fromString2("2 * 3 + 4");
            Assertions.assertEquals(OptionalLong.of(14), calculator.compute());
        }
        {
            var calculator = Calculator.fromString2("(2 * 3) + 4");
            Assertions.assertEquals(OptionalLong.of(10), calculator.compute());
        }
    }

    @Test
    public void testParse2() throws Exception {
        try (BufferedReader in = readInput()) {
            final long[] values = Calculator.parseInput2(in).stream()
                    .map(Calculator::compute)
                    .mapToLong(OptionalLong::orElseThrow)
                    .toArray();
            Assertions.assertArrayEquals(new long[] { 231, 51, 46, 1445, 669060, 23340 }, values);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
