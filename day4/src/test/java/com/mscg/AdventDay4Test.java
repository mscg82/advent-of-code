package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay4Test {

    @Test
    public void parseInput() throws Exception {
        try (BufferedReader in = readInput()) {
            List<Passport> passports = Passport.parseInput(in);
            Assertions.assertEquals(5, passports.size());
        }
    }

    @Test
    public void readPassports() throws Exception {
        try (BufferedReader in = readInput()) {
            List<Passport> passports = Passport.parseInput(in);
            int[] fieldsCount = passports.stream().mapToInt(p -> p.fields().size()).toArray();
            Assertions.assertArrayEquals(new int[] {8, 7, 7, 6, 6}, fieldsCount);
        }
    }

    @Test
    public void validatePassports() throws Exception {
        try (BufferedReader in = readInput()) {
            List<Passport> passports = Passport.parseInput(in);
            long validPassports = passports.stream().filter(Passport::isValidPart1).count();
            Assertions.assertEquals(2, validPassports);
        }
    }

    @Test
    public void validatePassoprtsPart2() throws Exception {
        try (BufferedReader in = readInput2()) {
            List<Passport> passports = Passport.parseInput(in);
            Assertions.assertEquals(9, passports.size());
            int[] fieldsCount = passports.stream().mapToInt(p -> p.fields().size()).toArray();
            Assertions.assertArrayEquals(new int[] {8, 8, 7, 8, 7, 7, 8, 8, 7}, fieldsCount);
            long validPassports = passports.stream().filter(Passport::isValidPart2).count();
            Assertions.assertEquals(5, validPassports);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

    private BufferedReader readInput2() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input-2.txt"), StandardCharsets.UTF_8));
    }

}
