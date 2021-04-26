package com.mscg;

import static com.mscg.ChipFactoryRoomComponentBuilder.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.mscg.ChipFactoryRoom.ComponentType;
import com.mscg.ChipFactoryRoom.Floor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay11Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            final var room = ChipFactoryRoom.parseInput(in);
            Assertions.assertEquals(Map.of( //
                    Floor.FIRST, List.of(Component("hydrogen", ComponentType.CHIP), Component("lithium", ComponentType.CHIP)), //
                    Floor.SECOND, List.of(Component("hydrogen", ComponentType.GENERATOR)), //
                    Floor.THIRD, List.of(Component("lithium", ComponentType.GENERATOR)), //
                    Floor.FOURTH, List.of() //
            ), room.floors());
            Assertions.assertEquals(Floor.FIRST, room.elevatorPosition());
        }
    }

    @Test
    public void testNextStates() throws Exception {
        try (BufferedReader in = readInput()) {
            final var room = ChipFactoryRoom.parseInput(in);

            final List<ChipFactoryRoom> next1 = room.generateNextStates();
            Assertions.assertEquals(1, next1.size());
            final var nextRoom = next1.get(0);
            Assertions.assertEquals(Map.of( //
                    Floor.FIRST, List.of(Component("lithium", ComponentType.CHIP)), //
                    Floor.SECOND, List.of(Component("hydrogen", ComponentType.GENERATOR), Component("hydrogen", ComponentType.CHIP)), //
                    Floor.THIRD, List.of(Component("lithium", ComponentType.GENERATOR)), //
                    Floor.FOURTH, List.of() //
            ), nextRoom.floors());
            Assertions.assertEquals(Floor.SECOND, nextRoom.elevatorPosition());

            final List<ChipFactoryRoom> next2 = nextRoom.generateNextStates();
            Assertions.assertEquals(3, next2.size());
            final var nextRoom2_1 = next2.get(0);
            Assertions.assertEquals(Map.of( //
                    Floor.FIRST, List.of(Component("hydrogen", ComponentType.CHIP), Component("lithium", ComponentType.CHIP)), //
                    Floor.SECOND, List.of(Component("hydrogen", ComponentType.GENERATOR)), //
                    Floor.THIRD, List.of(Component("lithium", ComponentType.GENERATOR)), //
                    Floor.FOURTH, List.of() //
            ), nextRoom2_1.floors());
            Assertions.assertEquals(Floor.FIRST, nextRoom2_1.elevatorPosition());

            final var nextRoom2_2 = next2.get(1);
            Assertions.assertEquals(Map.of( //
                    Floor.FIRST, List.of(Component("lithium", ComponentType.CHIP)), //
                    Floor.SECOND, List.of(Component("hydrogen", ComponentType.CHIP)), //
                    Floor.THIRD, List.of(Component("hydrogen", ComponentType.GENERATOR), Component("lithium", ComponentType.GENERATOR)), //
                    Floor.FOURTH, List.of() //
            ), nextRoom2_2.floors());
            Assertions.assertEquals(Floor.THIRD, nextRoom2_2.elevatorPosition());

            final var nextRoom2_3 = next2.get(2);
            Assertions.assertEquals(Map.of( //
                    Floor.FIRST, List.of(Component("lithium", ComponentType.CHIP)), //
                    Floor.SECOND, List.of(), //
                    Floor.THIRD, List.of(Component("hydrogen", ComponentType.GENERATOR), Component("lithium", ComponentType.GENERATOR), Component("hydrogen", ComponentType.CHIP)), //
                    Floor.FOURTH, List.of() //
            ), nextRoom2_3.floors());
            Assertions.assertEquals(Floor.THIRD, nextRoom2_3.elevatorPosition());
        }
    }

    @Test
    public void testBringEverythingToTop() throws Exception {
        try (BufferedReader in = readInput()) {
            final var room = ChipFactoryRoom.parseInput(in);

            final List<ChipFactoryRoom> steps = room.bringEverythingToTop();

            Assertions.assertEquals(12, steps.size());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
