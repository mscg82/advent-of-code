package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class Screen {

    private final Pixel[][] screen;
    private final List<Instruction> instructions;

    public Screen(int rows, int cols, List<Instruction> instructions) {
        this.screen = new Pixel[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                screen[i][j] = Pixel.OFF;
            }
        }
        this.instructions = instructions;
    }

    public void run() {
        instructions.forEach(instruction -> instruction.executeOnScreen(screen));
    }

    public long countActivePixels() {
        return Arrays.stream(screen) //
                .flatMap(row -> Arrays.stream(row)) //
                .filter(pixel -> pixel == Pixel.ON) //
                .count();
    }

    @Override
    public String toString() {
        return Arrays.stream(screen) //
                .map(row -> Arrays.stream(row) //
                        .map(Pixel::toString) //
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }

    public static Screen parseInput(int rows, int cols, BufferedReader in) throws IOException {
        var rectPattern = Pattern.compile("rect (\\d+)x(\\d+)");
        var rotateRowPattern = Pattern.compile("rotate row y=(\\d+) by (\\d+)");
        var rotateColPattern = Pattern.compile("rotate column x=(\\d+) by (\\d+)");

        List<Instruction> instructions = in.lines() //
                .map(line -> {
                    Matcher matcher;
                    if ((matcher = rectPattern.matcher(line)).matches()) {
                        return new Rect(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    } else if ((matcher = rotateRowPattern.matcher(line)).matches()) {
                        return new RotateRow(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    } else if ((matcher = rotateColPattern.matcher(line)).matches()) {
                        return new RotateCol(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    } else {
                        throw new IllegalArgumentException("Unsupported instruction " + line);
                    }
                }) //
                .collect(Collectors.toUnmodifiableList());
        return new Screen(rows, cols, instructions);
    }

    public sealed interface Instruction {
        void executeOnScreen(Pixel[][] screen);
    }

    public static record Rect(int width, int height) implements Instruction {
        @Override
        public void executeOnScreen(Pixel[][] screen) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    screen[i][j] = Pixel.ON;
                }
            }
        }
    }

    public static record RotateRow(int row, int amount) implements Instruction {
        @Override
        public void executeOnScreen(Pixel[][] screen) {
            Pixel[] row = screen[this.row];
            int columns = row.length;
            Pixel[] lastPart = new Pixel[amount];
            System.arraycopy(row, columns - amount, lastPart, 0, amount);
            System.arraycopy(row, 0, row, amount, columns - amount);
            System.arraycopy(lastPart, 0, row, 0, amount);
        }
    }

    public static record RotateCol(int col, int amount) implements Instruction {
        @Override
        public void executeOnScreen(Pixel[][] screen) {
            int rows = screen.length;
            Pixel[] col = new Pixel[rows];
            for (int i = 0; i < rows; i++) {
                col[i] = screen[i][this.col];
            }
            Pixel[] lastPart = new Pixel[amount];
            System.arraycopy(col, rows - amount, lastPart, 0, amount);
            System.arraycopy(col, 0, col, amount, rows - amount);
            System.arraycopy(lastPart, 0, col, 0, amount);
            for (int i = 0; i < rows; i++) {
                screen[i][this.col] = col[i];
            }
        }
    }

    public enum Pixel {
        ON, OFF;

        @Override
        public String toString() {
            return switch (this) {
            case ON -> "#";
            case OFF -> ".";
            };
        }
    }

}
