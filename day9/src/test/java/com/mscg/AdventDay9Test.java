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

}
