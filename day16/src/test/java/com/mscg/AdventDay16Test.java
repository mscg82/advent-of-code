package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay16Test {

    @SuppressWarnings("unchecked")
    private static <T> List<T> cast(List<?> source, Class<T> clazz) {
        return (List<T>) source;
    }

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var ticketDB = TicketDB.parseInput(in);
            Assertions.assertEquals(3, ticketDB.validityRules().size());
            Assertions.assertArrayEquals(new TicketDB.ValidityRule[]{
                    new TicketDB.ValidityRule("class", new TicketDB.InclusiveRange(1, 3), new TicketDB.InclusiveRange(5, 7)),
                    new TicketDB.ValidityRule("row", new TicketDB.InclusiveRange(6, 11), new TicketDB.InclusiveRange(33, 44)),
                    new TicketDB.ValidityRule("seat", new TicketDB.InclusiveRange(13, 40), new TicketDB.InclusiveRange(45, 50))
            }, cast(ticketDB.validityRules(), TicketDB.ValidityRule.class).toArray(new TicketDB.ValidityRule[0]));

            Assertions.assertArrayEquals(new int[]{ 7, 1, 14 }, ticketDB.yourTicket().values());

            Assertions.assertEquals(4, ticketDB.nearbyTickets().size());
            Assertions.assertArrayEquals(new int[]{ 7, 3, 47 }, ((TicketDB.Ticket) ticketDB.nearbyTickets().get(0)).values());
            Assertions.assertArrayEquals(new int[]{ 40, 4, 50 }, ((TicketDB.Ticket) ticketDB.nearbyTickets().get(1)).values());
            Assertions.assertArrayEquals(new int[]{ 55, 2, 20 }, ((TicketDB.Ticket) ticketDB.nearbyTickets().get(2)).values());
            Assertions.assertArrayEquals(new int[]{ 38, 6, 12 }, ((TicketDB.Ticket) ticketDB.nearbyTickets().get(3)).values());
        }
    }

    @Test
    public void testRange() {
        var range = new TicketDB.InclusiveRange(1, 5);
        Assertions.assertTrue(range.test(1));
        Assertions.assertTrue(range.test(3));
        Assertions.assertTrue(range.test(5));
        Assertions.assertFalse(range.test(0));
        Assertions.assertFalse(range.test(6));
    }

    @Test
    public void testRule() {
        var rule = new TicketDB.ValidityRule("rule", new TicketDB.InclusiveRange(1, 3), new TicketDB.InclusiveRange(5, 7));
        Assertions.assertFalse(rule.test(0));

        Assertions.assertTrue(rule.test(1));
        Assertions.assertTrue(rule.test(2));
        Assertions.assertTrue(rule.test(3));

        Assertions.assertFalse(rule.test(4));

        Assertions.assertTrue(rule.test(5));
        Assertions.assertTrue(rule.test(6));
        Assertions.assertTrue(rule.test(7));

        Assertions.assertFalse(rule.test(8));
    }

    @Test
    public void testValidTickets() throws Exception {
        try (BufferedReader in = readInput()) {
            var ticketDB = TicketDB.parseInput(in);
            final List<TicketDB.Ticket> validTickets = ticketDB.getValidTickets();
            Assertions.assertEquals(1, validTickets.size());
            Assertions.assertArrayEquals(new int[]{ 7, 3, 47 }, validTickets.get(0).values());
        }
    }

    @Test
    public void testErrorRate() throws Exception {
        try (BufferedReader in = readInput()) {
            var ticketDB = TicketDB.parseInput(in);
            Assertions.assertEquals(71, ticketDB.getTicketErrorRate());
        }
    }

    @Test
    public void testValidTickets2() throws Exception {
        try (BufferedReader in = readInput2()) {
            var ticketDB = TicketDB.parseInput(in);
            final List<TicketDB.Ticket> validTickets = ticketDB.getValidTickets();
            Assertions.assertEquals(3, validTickets.size());
            Assertions.assertArrayEquals(new int[]{ 3, 9, 18 }, validTickets.get(0).values());
            Assertions.assertArrayEquals(new int[]{ 15, 1, 5 }, validTickets.get(1).values());
            Assertions.assertArrayEquals(new int[]{ 5, 14, 9 }, validTickets.get(2).values());
        }
    }

    @Test
    public void testFindFields() throws Exception {
        try (BufferedReader in = readInput2()) {
            var ticketDB = TicketDB.parseInput(in);
            Assertions.assertEquals(List.of("row", "class", "seat"), ticketDB.findFields());
        }
    }

    @Test
    public void testMapperTicket() throws Exception {
        try (BufferedReader in = readInput2()) {
            var ticketDB = TicketDB.parseInput(in);
            final Map<String, Integer> mappedTicket = ticketDB.getMappedTicket();
            Assertions.assertEquals(Map.of(
                    "row", 11,
                    "class", 12,
                    "seat", 13
            ), mappedTicket);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

    private BufferedReader readInput2() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input2.txt"), StandardCharsets.UTF_8));
    }

}
