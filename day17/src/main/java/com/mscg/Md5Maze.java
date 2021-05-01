package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Deque;
import java.util.LinkedList;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.apache.commons.codec.binary.Hex;

public record Md5Maze(String password, int width, int height) {

    public Md5Maze(final String password) {
        this(password, 4, 4);
    }

    public String findPath() {
        final MessageDigest md5Digest = getMd5Digest();

        record Step(String path, Position pos) {
        }

        final var start = new Position(0, 0);
        final var end = new Position(width - 1, height - 1);

        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step("", start));

        while (!queue.isEmpty()) {
            final var currentStep = queue.pop();
            final Position position = currentStep.pos();
            final String path = currentStep.path();

            if (position.equals(end)) {
                return path;
            }

            final String md5 = computeKey(md5Digest, path);

            // UP
            if (position.y() != 0 && isOpen(md5.charAt(0))) {
                queue.add(new Step(path + "U", position.withY(position.y() - 1)));
            }

            // DOWN
            if (position.y() != height - 1 && isOpen(md5.charAt(1))) {
                queue.add(new Step(path + "D", position.withY(position.y() + 1)));
            }

            // LEFT
            if (position.x() != 0 && isOpen(md5.charAt(2))) {
                queue.add(new Step(path + "L", position.withX(position.x() - 1)));
            }

            // RIGHT
            if (position.x() != width - 1 && isOpen(md5.charAt(3))) {
                queue.add(new Step(path + "R", position.withX(position.x() + 1)));
            }
        }

        throw new IllegalArgumentException("Unable to reach " + end + " from " + start);
    }

    public String findLongestPath() {
        final MessageDigest md5Digest = getMd5Digest();

        record Step(String path, Position pos) {
        }

        final var start = new Position(0, 0);
        final var end = new Position(width - 1, height - 1);

        String longestPath = "";
        final Deque<Step> queue = new LinkedList<>();
        queue.add(new Step("", start));

        while (!queue.isEmpty()) {
            final var currentStep = queue.pop();
            final Position position = currentStep.pos();
            final String path = currentStep.path();

            if (position.equals(end)) {
                if (longestPath.length() < path.length()) {
                    longestPath = path;
                }
                continue;
            }

            final String md5 = computeKey(md5Digest, path);

            // UP
            if (position.y() != 0 && isOpen(md5.charAt(0))) {
                queue.add(new Step(path + "U", position.withY(position.y() - 1)));
            }

            // DOWN
            if (position.y() != height - 1 && isOpen(md5.charAt(1))) {
                queue.add(new Step(path + "D", position.withY(position.y() + 1)));
            }

            // LEFT
            if (position.x() != 0 && isOpen(md5.charAt(2))) {
                queue.add(new Step(path + "L", position.withX(position.x() - 1)));
            }

            // RIGHT
            if (position.x() != width - 1 && isOpen(md5.charAt(3))) {
                queue.add(new Step(path + "R", position.withX(position.x() + 1)));
            }
        }

        return longestPath;
    }

    private String computeKey(final MessageDigest md5Digest, final String path) {
        md5Digest.update(password.getBytes());
        md5Digest.update(path.getBytes());
        final String md5 = Hex.encodeHexString(md5Digest.digest());
        md5Digest.reset();
        return md5;
    }

    private MessageDigest getMd5Digest() {
        final MessageDigest md5Digest;
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return md5Digest;
    }

    private boolean isOpen(final char value) {
        return switch (value) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a' -> false;
            case 'b', 'c', 'd', 'e', 'f' -> true;
            default -> throw new IllegalArgumentException("Illegal character value " + value);
        };
    }

    public static Md5Maze parseInput(final BufferedReader in) throws IOException {
        return new Md5Maze(in.readLine(), 4, 4);
    }

    @RecordBuilder
    public static record Position(int x, int y) implements Md5MazePositionBuilder.With {

    }

}
