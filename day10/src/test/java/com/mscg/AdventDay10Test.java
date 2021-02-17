package com.mscg;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay10Test {

    @Test
    public void testTransform() {
        Assertions.assertEquals("11", LookAndSay.transform("1"));
        Assertions.assertEquals("21", LookAndSay.transform("11"));
        Assertions.assertEquals("1211", LookAndSay.transform("21"));
        Assertions.assertEquals("111221", LookAndSay.transform("1211"));
        Assertions.assertEquals("312211", LookAndSay.transform("111221"));

        List<String> strings = Stream.iterate("1", LookAndSay::transform) //
                .skip(1) //
                .limit(5) //
                .collect(Collectors.toUnmodifiableList());
        Assertions.assertEquals(List.of("11", "21", "1211", "111221", "312211"), strings);
    }

}
