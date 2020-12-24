package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay11Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            SeatBoard seatBoard = SeatBoard.parseInput(in);
            Assertions.assertEquals("""
                            L.LL.LL.LL
                            LLLLLLL.LL
                            L.L.L..L..
                            LLLL.LL.LL
                            L.LL.LL.LL
                            L.LLLLL.LL
                            ..L.L.....
                            LLLLLLLLLL
                            L.LLLLLL.L
                            L.LLLLL.LL""",
                    seatBoard.toString());
        }
    }

    @Test
    public void testNext1() throws Exception {
        final SeatBoard seatBoard;
        try (BufferedReader in = readInput()) {
            seatBoard = SeatBoard.parseInput(in);
        }

        SeatBoard next1 = seatBoard.next1();
        Assertions.assertEquals("""
                        #.##.##.##
                        #######.##
                        #.#.#..#..
                        ####.##.##
                        #.##.##.##
                        #.#####.##
                        ..#.#.....
                        ##########
                        #.######.#
                        #.#####.##""",
                next1.toString());

        SeatBoard next2 = next1.next1();
        Assertions.assertEquals("""
                        #.LL.L#.##
                        #LLLLLL.L#
                        L.L.L..L..
                        #LLL.LL.L#
                        #.LL.LL.LL
                        #.LLLL#.##
                        ..L.L.....
                        #LLLLLLLL#
                        #.LLLLLL.L
                        #.#LLLL.##""",
                next2.toString());
    }

    @Test
    public void testEvolve1() throws Exception {
        final SeatBoard seatBoard;
        try (BufferedReader in = readInput()) {
            seatBoard = SeatBoard.parseInput(in);
        }

        SeatBoard evolved = seatBoard.evolveUntilHalt1();
        Assertions.assertEquals(37, evolved.countOccupied());
    }

    @Test
    public void testNext2() throws Exception {
        final SeatBoard seatBoard;
        try (BufferedReader in = readInput()) {
            seatBoard = SeatBoard.parseInput(in);
        }

        SeatBoard next1 = seatBoard.next2();
        Assertions.assertEquals("""
                        #.##.##.##
                        #######.##
                        #.#.#..#..
                        ####.##.##
                        #.##.##.##
                        #.#####.##
                        ..#.#.....
                        ##########
                        #.######.#
                        #.#####.##""",
                next1.toString());

        SeatBoard next2 = next1.next2();
        Assertions.assertEquals("""
                        #.LL.LL.L#
                        #LLLLLL.LL
                        L.L.L..L..
                        LLLL.LL.LL
                        L.LL.LL.LL
                        L.LLLLL.LL
                        ..L.L.....
                        LLLLLLLLL#
                        #.LLLLLL.L
                        #.LLLLL.L#""",
                next2.toString());

        SeatBoard next3 = next2.next2();
        Assertions.assertEquals("""
                        #.L#.##.L#
                        #L#####.LL
                        L.#.#..#..
                        ##L#.##.##
                        #.##.#L.##
                        #.#####.#L
                        ..#.#.....
                        LLL####LL#
                        #.L#####.L
                        #.L####.L#""",
                next3.toString());
    }


    @Test
    public void testEvolve2() throws Exception {
        final SeatBoard seatBoard;
        try (BufferedReader in = readInput()) {
            seatBoard = SeatBoard.parseInput(in);
        }

        SeatBoard evolved = seatBoard.evolveUntilHalt2();
        Assertions.assertEquals(26, evolved.countOccupied());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
