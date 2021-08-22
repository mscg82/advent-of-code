package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay10Test {

    @Test
    public void testFindMessage() throws Exception {
        try (var in = readInput()) {
            final var msgBuilder = MessageBuilder.parseInput(in);
            final List<MessageBuilder.MessageInfo> messages = msgBuilder.findMessage(4);
            Assertions.assertEquals(1, messages.size());
            Assertions.assertEquals(3, messages.get(0).time());
            Assertions.assertEquals("""
                    #...#..###
                    #...#...#.
                    #...#...#.
                    #####...#.
                    #...#...#.
                    #...#...#.
                    #...#...#.
                    #...#..###
                    """, messages.get(0).message());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
