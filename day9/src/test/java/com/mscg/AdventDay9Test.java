package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay9Test {

    @Test
    public void testUnzip() {
        Assertions.assertEquals("ADVENT", Unzipper.unzip("ADVENT").toString());
        Assertions.assertEquals("ABBBBBC", Unzipper.unzip("A(1x5)BC").toString());
        Assertions.assertEquals("XYZXYZXYZ", Unzipper.unzip("(3x3)XYZ").toString());
        Assertions.assertEquals("ABCBCDEFEFG", Unzipper.unzip("A(2x2)BCD(2x2)EFG").toString());
        Assertions.assertEquals("(1x3)A", Unzipper.unzip("(6x1)(1x3)A").toString());
        Assertions.assertEquals("X(3x3)ABC(3x3)ABCY", Unzipper.unzip("X(8x2)(3x3)ABCY").toString());
    }

    @Test
    public void testUnzipRecursive() {
        Assertions.assertEquals("XYZXYZXYZ", Unzipper.unzipRecursive("(3x3)XYZ").toString());
        Assertions.assertEquals("XABCABCABCABCABCABCY", Unzipper.unzipRecursive("X(8x2)(3x3)ABCY").toString());
        Assertions.assertEquals(241920, Unzipper.unzipRecursive("(27x12)(20x12)(13x14)(7x10)(1x12)A").length());
        Assertions.assertEquals(445, Unzipper.unzipRecursive("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN").length());
    }

    @Test
    public void testCountUnzipRecursive() {
        Assertions.assertEquals(9L, Unzipper.countUnzipRecursive("(3x3)XYZ"));
        Assertions.assertEquals(20L, Unzipper.countUnzipRecursive("X(8x2)(3x3)ABCY"));
        Assertions.assertEquals(241920L, Unzipper.countUnzipRecursive("(27x12)(20x12)(13x14)(7x10)(1x12)A"));
        Assertions.assertEquals(445L, Unzipper.countUnzipRecursive("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN"));
    }

}
