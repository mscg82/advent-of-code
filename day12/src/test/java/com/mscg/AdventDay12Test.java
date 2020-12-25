package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdventDay12Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var boat = Boat.parseInput(in);
            Assertions.assertArrayEquals(new Boat.Instruction[] {
                    new Boat.Instruction(Boat.Direction.FORWARD, 10),
                    new Boat.Instruction(Boat.Direction.NORTH, 3),
                    new Boat.Instruction(Boat.Direction.FORWARD, 7),
                    new Boat.Instruction(Boat.Direction.RIGTH, 90),
                    new Boat.Instruction(Boat.Direction.FORWARD, 11),
            }, boat.getInstructions().toArray(new Boat.Instruction[0]));
        }
    }

    @Test
    public void testRotate1() throws Exception {
        {
            var boat = new Boat(List.of(new Boat.Instruction(Boat.Direction.RIGTH, 90)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(-1, 0), boat.getOrientation());
        }
        {
            var boat = new Boat(List.of(
                    new Boat.Instruction(Boat.Direction.RIGTH, 90),
                    new Boat.Instruction(Boat.Direction.LEFT, 180)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(1, 0), boat.getOrientation());
        }
        {
            var boat = new Boat(List.of(new Boat.Instruction(Boat.Direction.LEFT, 90)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(1, 0), boat.getOrientation());
        }
        {
            var boat = new Boat(List.of(
                    new Boat.Instruction(Boat.Direction.LEFT, 90),
                    new Boat.Instruction(Boat.Direction.RIGTH, 180)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(-1, 0), boat.getOrientation());
        }
        {
            var boat = new Boat(List.of(new Boat.Instruction(Boat.Direction.LEFT, 270)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(-1, 0), boat.getOrientation());
        }
        {
            var boat = new Boat(List.of(new Boat.Instruction(Boat.Direction.RIGTH, 270)));
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(1, 0), boat.getOrientation());
        }
    }

    @Test
    public void testExecute1() throws Exception {
        try (BufferedReader in = readInput()) {
            var boat = Boat.parseInput(in);
            boat.execute1();
            Assertions.assertEquals(new Boat.Position(-8, 17), boat.getPosition());
            Assertions.assertEquals(new Boat.Position(-1, 0), boat.getOrientation());
        }
    }

    @Test
    public void testExecute2() throws Exception {
        try (BufferedReader in = readInput()) {
            var boat = Boat.parseInput(in);
            boat.execute2();
            Assertions.assertEquals(new Boat.Position(-72, 214), boat.getPosition());
            Assertions.assertEquals(new Boat.Position(-10, 4), boat.getWaypoint());
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
