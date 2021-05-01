package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay17Test {

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testPath1() {
        final var maze = new Md5Maze("hijkl");
        Assertions.assertThrows(IllegalArgumentException.class, maze::findPath);
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testPath2() {
        final var maze = new Md5Maze("ihgpwlah");
        Assertions.assertEquals("DDRRRD", maze.findPath());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testPath3() {
        final var maze = new Md5Maze("kglvqrro");
        Assertions.assertEquals("DDUDRLRRUDRD", maze.findPath());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testPath4() {
        final var maze = new Md5Maze("ulqzkmiv");
        Assertions.assertEquals("DRURDRUDDLLDLUURRDULRLDUUDDDRR", maze.findPath());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testLongestPath() {
        final var maze = new Md5Maze("ihgpwlah");
        Assertions.assertEquals(370, maze.findLongestPath().length());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testLongestPath2() {
        final var maze = new Md5Maze("kglvqrro");
        Assertions.assertEquals(492, maze.findLongestPath().length());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testLongestPath3() {
        final var maze = new Md5Maze("ulqzkmiv");
        Assertions.assertEquals(830, maze.findLongestPath().length());
    }

}
