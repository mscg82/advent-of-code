package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay8Test {

    @Test
    public void testChecksum() throws Exception {
        try (var in = readInput()) {
            final var validator = LicenseValidator.parseInput(in);
            Assertions.assertEquals(138, validator.computeChecksum());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
