package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay19Test {

    @Test
    public void testRun() throws Exception {
        try (BufferedReader in = readInput()) {
            final var router = PacketRouter.parseInput(in);
            Assertions.assertEquals(new PacketRouter.Result("ABCDEF", 38), router.run());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
