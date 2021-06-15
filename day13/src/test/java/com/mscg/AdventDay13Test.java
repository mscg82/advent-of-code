package com.mscg;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay13Test {

    @Test
    public void testComputeSeverity() {
        final var firewall = new Firewall(Map.of( //
                0, 3, //
                1, 2, //
                4, 4, //
                6, 4 //
        ));
        Assertions.assertEquals(24, firewall.computeSeverity(0));
    }

    @Test
    public void testSkipGettingCaught() {
        final var firewall = new Firewall(Map.of( //
                0, 3, //
                1, 2, //
                4, 4, //
                6, 4 //
        ));
        Assertions.assertEquals(0, firewall.computeSeverity(10));
    }

    @Test
    public void testComputeDelay() {
        final var firewall = new Firewall(Map.of( //
                0, 3, //
                1, 2, //
                4, 4, //
                6, 4 //
        ));
        Assertions.assertEquals(10, firewall.computeDelay());
    }

}
