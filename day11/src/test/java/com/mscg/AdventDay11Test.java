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

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
