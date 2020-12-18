package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class AdventDay5Test {

    @Test
    public void testSanity() {
        Assertions.assertEquals(Optional.empty(), SeatId.fromString("FxFBBFFRLR"));
        Assertions.assertEquals(Optional.empty(), SeatId.fromString("FBFBBFFRxR"));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testParse() {
        {
            SeatId seatId = SeatId.fromString("FBFBBFFRLR").get();

            Assertions.assertEquals(44, seatId.row());
            Assertions.assertEquals(5, seatId.column());
            Assertions.assertEquals(357, seatId.computeId());
        }
        {
            SeatId seatId = SeatId.fromString("BFFFBBFRRR").get();

            Assertions.assertEquals(70, seatId.row());
            Assertions.assertEquals(7, seatId.column());
            Assertions.assertEquals(567, seatId.computeId());
        }
        {
            SeatId seatId = SeatId.fromString("FFFBBBFRRR").get();

            Assertions.assertEquals(14, seatId.row());
            Assertions.assertEquals(7, seatId.column());
            Assertions.assertEquals(119, seatId.computeId());
        }
        {
            SeatId seatId = SeatId.fromString("BBFFBBFRLL").get();

            Assertions.assertEquals(102, seatId.row());
            Assertions.assertEquals(4, seatId.column());
            Assertions.assertEquals(820, seatId.computeId());
        }
    }

}
