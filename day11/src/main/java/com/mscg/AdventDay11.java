package com.mscg;

import static com.mscg.ChipFactoryRoomComponentBuilder.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay11 {

    public static void main(final String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            final var room = ChipFactoryRoom.parseInput(in);
            final List<ChipFactoryRoom> steps = room.bringEverythingToTop();
            System.out.println("Part 1 - Answer %d".formatted(steps.size() - 1));
        }
    }

    private static void part2() throws IOException {
        try (BufferedReader in = readInput()) {
            final var room = ChipFactoryRoom.parseInput(in);
            final var fixedRoom = room.withAdditionalComponents(List.of( //
                    Component("elerium", ChipFactoryRoom.ComponentType.GENERATOR),
                    Component("elerium", ChipFactoryRoom.ComponentType.CHIP),
                    Component("dilithium", ChipFactoryRoom.ComponentType.GENERATOR),
                    Component("dilithium", ChipFactoryRoom.ComponentType.CHIP)
            ));
            final List<ChipFactoryRoom> steps = fixedRoom.bringEverythingToTop();
            System.out.println("Part 2 - Answer %d".formatted(steps.size() - 1));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay11.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
