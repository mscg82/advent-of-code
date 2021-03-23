package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdventDay4 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var roomList = RoomList.parseInput(in);

            System.out.println("Part 1 - Answer %d".formatted(roomList.validSectorsSum()));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            var roomList = RoomList.parseInput(in);
            var northPoleRoom = roomList.findValidRooms().stream() //
                    .filter(room -> room.decodeName().contains("north")) //
                    .findFirst() //
                    .orElseThrow();

            System.out.println("Part 2 - Answer %d".formatted(northPoleRoom.sector()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay4.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
