package com.mscg;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay5Test {

    @Test
    public void testStepsToLoop() {
        final var jumper = new Jumper(List.of(0, 3, 0, 1, -3));
        final int steps = jumper.stepsToEscape();
        Assertions.assertEquals(5, steps);
    }

    @Test
    public void testStepsToLoops() {
        final var jumper = new Jumper(List.of(0, 3, 0, 1, -3));
        final int steps = jumper.stepsToEscape2();
        Assertions.assertEquals(10, steps);
    }

}
