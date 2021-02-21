package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.mscg.Table.Couple;
import com.mscg.Table.Arrangement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay13Test {

    @Test
    public void testParse() throws Exception {
        var table = Table.parseInput(readInput());
        Assertions.assertEquals(Map.ofEntries( //
                Map.entry(new Couple("Alice", "Bob"), 54), //
                Map.entry(new Couple("Alice", "Carol"), -79), //
                Map.entry(new Couple("Alice", "David"), -2), //
                Map.entry(new Couple("Bob", "Alice"), 83), //
                Map.entry(new Couple("Bob", "Carol"), -7), //
                Map.entry(new Couple("Bob", "David"), -63), //
                Map.entry(new Couple("Carol", "Alice"), -62), //
                Map.entry(new Couple("Carol", "Bob"), 60), //
                Map.entry(new Couple("Carol", "David"), 55), //
                Map.entry(new Couple("David", "Alice"), 46), //
                Map.entry(new Couple("David", "Bob"), -7), //
                Map.entry(new Couple("David", "Carol"), 41) //
        ), table.getCoupleToHappiness());
    }

    @Test
    public void testGetAllNames() throws Exception {
        var table = Table.parseInput(readInput());
        Assertions.assertEquals(List.of("Alice", "Bob", "Carol", "David"), table.getAllNames());
    }

    @Test
    public void testGenerateVariats2() {
        var variants = Table.generateVariants(List.of("a", "b"));
        Assertions.assertEquals(List.of( //
                List.of("a", "b"), List.of("b", "a")), variants);
    }

    @Test
    public void testGenerateVariats3() {
        var variants = Table.generateVariants(List.of("a", "b", "c"));
        Assertions.assertEquals(List.of( //
                List.of("a", "b", "c"), //
                List.of("b", "a", "c"), //
                List.of("c", "a", "b"), //
                List.of("a", "c", "b"), //
                List.of("b", "c", "a"), //
                List.of("c", "b", "a") //
        ), variants);
    }

    @Test
    public void testBestArrangement() throws Exception {
        var table = Table.parseInput(readInput());
        Assertions.assertEquals(new Arrangement(List.of("Alice", "Bob", "Carol", "David"), 330), table.findBestArrangement());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
