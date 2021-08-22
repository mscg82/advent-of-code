package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay10 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var msgBuilder = MessageBuilder.parseInput(in);
            final List<MessageBuilder.MessageInfo> messages = msgBuilder.findMessage(20_000);
            final var message = messages.get(0);
            System.out.println("Part 1 - Answer %n%s".formatted(message.message()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var msgBuilder = MessageBuilder.parseInput(in);
            final List<MessageBuilder.MessageInfo> messages = msgBuilder.findMessage(20_000);
            final var message = messages.get(0);
            System.out.println("Part 2 - Answer %d".formatted(message.time()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay10.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
