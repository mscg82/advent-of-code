package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay23Test {

    @Test
    public void test1Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(1);
        Assertions.assertEquals("328915467", cupset.toString());
    }

    @Test
    public void test2Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(2);
        Assertions.assertEquals("325467891", cupset.toString());
    }

    @Test
    public void test3Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(3);
        Assertions.assertEquals("725891346", cupset.toString());
    }

    @Test
    public void test4Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(4);
        Assertions.assertEquals("325846791", cupset.toString());
    }

    @Test
    public void test5Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(5);
        Assertions.assertEquals("925841367", cupset.toString());
    }

    @Test
    public void test6Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(6);
        Assertions.assertEquals("725841936", cupset.toString());
    }

    @Test
    public void test7Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(7);
        Assertions.assertEquals("836741925", cupset.toString());
    }

    @Test
    public void test8Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(8);
        Assertions.assertEquals("741583926", cupset.toString());
    }

    @Test
    public void test9Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(9);
        Assertions.assertEquals("574183926", cupset.toString());
    }

    @Test
    public void test10Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(10);
        Assertions.assertEquals("583741926", cupset.toString());
        Assertions.assertEquals("92658374", cupset.toStringFrom(1));
    }

    @Test
    public void test100Step() throws Exception {
        Cupset cupset = Cupset.parseInput(readInput());
        cupset.run(100);
        Assertions.assertEquals("67384529", cupset.toStringFrom(1));
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
