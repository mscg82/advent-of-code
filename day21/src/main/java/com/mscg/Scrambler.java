package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Scrambler(List<Operation> operations) {

    public String scramble(final String input) {
        final var i = new StringBuilder(input);
        for (final var op : operations) {
            op.execute(i);
        }
        return i.toString();
    }

    public static Scrambler parseInput(final BufferedReader in) throws IOException {
        try {
            final List<Operation> operations = in.lines() //
                    .map(Operation::parseLine) //
                    .toList();
            return new Scrambler(operations);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public interface Operation {

        void execute(StringBuilder input);

        static Operation parseLine(final String line) {
            final var patternSwapPos = Pattern.compile("swap position (\\d+) with position (\\d+)");
            final var patternSwapLet = Pattern.compile("swap letter ([a-z]) with letter ([a-z])");
            final var patternRotate = Pattern.compile("rotate (left|right) (\\d+) steps?");
            final var patternRotateLet = Pattern.compile("rotate based on position of letter ([a-z])");
            final var patternReverse = Pattern.compile("reverse positions (\\d+) through (\\d+)");
            final var patternMove = Pattern.compile("move position (\\d+) to position (\\d+)");

            Matcher matcher;

            matcher = patternSwapPos.matcher(line);
            if (matcher.matches()) {
                return new SwapPositions(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }

            matcher = patternSwapLet.matcher(line);
            if (matcher.matches()) {
                return new SwapLetters(matcher.group(1).charAt(0), matcher.group(2).charAt(0));
            }

            matcher = patternRotate.matcher(line);
            if (matcher.matches()) {
                return new Rotate("right".equals(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }

            matcher = patternRotateLet.matcher(line);
            if (matcher.matches()) {
                return new RotateLetter(matcher.group(1).charAt(0));
            }

            matcher = patternReverse.matcher(line);
            if (matcher.matches()) {
                return new Reverse(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }

            matcher = patternMove.matcher(line);
            if (matcher.matches()) {
                return new Move(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }

            throw new IllegalArgumentException("Unsupported operation " + line);
        }

    }

    static record SwapPositions(int x, int y) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            final char charY = input.charAt(y);
            input.setCharAt(y, input.charAt(x));
            input.setCharAt(x, charY);
        }
    }

    static record SwapLetters(char x, char y) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            final int idxX = input.indexOf(String.valueOf(x));
            final int idxY = input.indexOf(String.valueOf(y));
            input.setCharAt(idxX, y);
            input.setCharAt(idxY, x);
        }
    }

    static record Rotate(boolean right, int steps) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            if (right) {
                rotateRight(input, steps);
            } else {
                rotateLeft(input, steps);
            }
        }

        static void rotateRight(final StringBuilder input, int steps) {
            steps = steps % input.length();
            if (steps == 0) {
                return;
            }
            final var end = input.substring(input.length() - steps, input.length());
            input.delete(input.length() - steps, input.length());
            input.insert(0, end);
        }

        static void rotateLeft(final StringBuilder input, int steps) {
            steps = steps % input.length();
            if (steps == 0) {
                return;
            }
            final var start = input.substring(0, steps);
            input.delete(0, steps);
            input.append(start);
        }
    }

    static record RotateLetter(char x) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            final int index = input.indexOf(String.valueOf(x));
            final int steps = 1 + index + (index >= 4 ? 1 : 0);
            Rotate.rotateRight(input, steps);
        }
    }

    static record Reverse(int x, int y) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            final var subInput = new StringBuilder(input.subSequence(x, y + 1));
            input.delete(x, y + 1);
            input.insert(x, subInput.reverse());
        }
    }

    static record Move(int x, int y) implements Operation {
        @Override
        public void execute(final StringBuilder input) {
            final var charX = input.charAt(x);
            input.deleteCharAt(x);
            input.insert(y, charX);
        }
    }
}
