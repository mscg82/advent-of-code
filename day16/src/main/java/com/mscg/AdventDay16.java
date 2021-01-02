package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdventDay16 {
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        try (BufferedReader in = readInput()) {
            var ticketDB = TicketDB.parseInput(in);
            int errorRate = ticketDB.getTicketErrorRate();
            System.out.println("Part 1: Answer: %d".formatted(errorRate));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var ticketDB = TicketDB.parseInput(in);
            final Map<String, Integer> mappedTicket = ticketDB.getMappedTicket();
            long prod = mappedTicket.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("departure"))
                    .mapToLong(Map.Entry::getValue)
                    .reduce(1, (acc, v) -> acc * v);
            System.out.println("Part 2: Answer: %d".formatted(prod));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay16.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }
}
