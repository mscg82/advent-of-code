package com.mscg;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay6Test {

    @Test
    public void testRedistribute() {
        final var memory = new MemoryBank(List.of(0, 2, 7, 0));
        Assertions.assertEquals(new MemoryBank.IndexSize(5, 4), memory.redistributeUntilLoop());
    }

}
