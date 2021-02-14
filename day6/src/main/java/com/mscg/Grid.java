package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Data;

@Data
public class Grid {

    private final List<Instruction> instructions;

    private final Map<Point, Boolean> lights;

    private final Map<Point, Long> brightness;

    private Grid(final List<Instruction> instructions) {
        this.instructions = instructions;
        this.lights = new HashMap<>(1_000_000);
        this.brightness = new HashMap<>(1_000_000);
    }

    public void run() {
        instructions.forEach(instr -> instr.run(lights));
    }

    public void run2() {
        instructions.forEach(instr -> instr.run2(brightness));
    }

    public static Grid parseInput(BufferedReader in) throws IOException {
        List<Instruction> instructions = in.lines() //
                .map(Instruction::parseString) //
                .collect(Collectors.toUnmodifiableList());
        
        return new Grid(instructions);
    }

    public static record Instruction(InstructionType type, Range range) {
        private static Pattern INSTRUCTION_PATTERN = Pattern.compile("(turn on|turn off|toggle) (\\d{1,3}),(\\d{1,3}) through (\\d{1,3}),(\\d{1,3})");

        public void run(Map<Point, Boolean> lights) {
            IntStream.rangeClosed(range.start().x(), range.end().x()) //
                    .mapToObj(i -> IntStream.rangeClosed(range.start().y(), range.end().y()) //
                                .mapToObj(j -> new Point(i, j))) //
                    .flatMap(s -> s) //
                    .forEach(point -> {
                        switch (type) {
                            case TURN_ON -> lights.put(point, Boolean.TRUE);
                            case TURN_OFF -> lights.put(point, Boolean.FALSE);
                            case TOGGLE -> lights.compute(point, (__, oldStatus) -> oldStatus == null ? Boolean.TRUE : !oldStatus.booleanValue());
                        }    
                    });
        }

        public void run2(Map<Point, Long> brightness) {
            IntStream.rangeClosed(range.start().x(), range.end().x()) //
                    .mapToObj(i -> IntStream.rangeClosed(range.start().y(), range.end().y()) //
                                .mapToObj(j -> new Point(i, j))) //
                    .flatMap(s -> s) //
                    .forEach(point -> {
                        long increment = switch (type) {
                            case TURN_ON -> 1L;
                            case TURN_OFF -> -1L;
                            case TOGGLE -> 2L;
                        };
                        
                        brightness.compute(point, (__, oldVal) -> Math.max(0L, (oldVal == null ? 0L : oldVal) + increment));
                    });
        }

        public static Instruction parseString(String line) {
            var matcher = INSTRUCTION_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid instruction line " + line);
            }

            InstructionType type = switch (matcher.group(1)) {
                case "turn on" -> InstructionType.TURN_ON;
                case "turn off" -> InstructionType.TURN_OFF;
                case "toggle" -> InstructionType.TOGGLE;
                default -> throw new IllegalArgumentException("Invalid instruction type literal");
            };

            var start = new Point(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
            var end = new Point(Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)));

            return new Instruction(type, new Range(start, end));
        }
    }

    public enum InstructionType {
        TURN_ON, TURN_OFF, TOGGLE;
    }

    public static record Point(int x, int y) {

    }

    public static record Range(Point start, Point end) {

    }

}
