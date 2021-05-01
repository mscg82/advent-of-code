package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay16Test {

    @Test
    public void testDragonCurve() {
        Assertions.assertEquals("100", DiskCleaner.dragonCurve("1"));
        Assertions.assertEquals("001", DiskCleaner.dragonCurve("0"));
        Assertions.assertEquals("11111000000", DiskCleaner.dragonCurve("11111"));
        Assertions.assertEquals("1111000010100101011110000", DiskCleaner.dragonCurve("111100001010"));
    }

    @Test
    public void testChecksum() {
        Assertions.assertEquals("100", DiskCleaner.checksum("110010110100"));
        Assertions.assertEquals("01100", DiskCleaner.checksum("10000011110010000111"));
    }

    @Test
    public void testCleanDisk() throws Exception {
        try (BufferedReader in = readInput()) {
            final var diskCleaner = DiskCleaner.parseInput(in);
            Assertions.assertEquals("01100", diskCleaner.cleanDisk(20));
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
