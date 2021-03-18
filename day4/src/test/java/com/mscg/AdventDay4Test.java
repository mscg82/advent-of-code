package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.RoomList.Room;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay4Test {
    
    @Test
    public void testParse() throws Exception {
        var roomList = RoomList.parseInput(readInput());

        Assertions.assertEquals(List.of( //
            new Room("aaaaa-bbb-z-y-x", 123, "abxyz"), //
            new Room("a-b-c-d-e-f-g-h", 987, "abcde"), //
            new Room("not-a-real-room", 404, "oarel"), //
            new Room("totally-real-room", 200, "decoy") //
        ), roomList.getRooms());
    }

    @Test
    public void testValidRooms() throws Exception {
        var roomList = RoomList.parseInput(readInput());

        Assertions.assertEquals(List.of( //
            new Room("aaaaa-bbb-z-y-x", 123, "abxyz"), //
            new Room("a-b-c-d-e-f-g-h", 987, "abcde"), //
            new Room("not-a-real-room", 404, "oarel") //
        ), roomList.findValidRooms());
    }

    @Test
    public void testValidSectors() throws Exception {
        var roomList = RoomList.parseInput(readInput());

        Assertions.assertEquals(1514, roomList.validSectorsSum());
    }

    @Test
    public void testDecodeName() {
        var room = new RoomList.Room("qzmt-zixmtkozy-ivhz", 343, "");
        Assertions.assertEquals("very encrypted name", room.decodeName());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
