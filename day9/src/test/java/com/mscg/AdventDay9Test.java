package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mscg.LocationMap.Connection;
import com.mscg.LocationMap.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class AdventDay9Test {

    @Test
    public void testParse() throws Exception {
        var map = LocationMap.parseInput(readInput());

        Assertions.assertEquals(List.of( //
                new Connection("Belfast", 518), //
                new Connection("Dublin", 464)), //
                map.getNodeToConnections().get("London"));

        Assertions.assertEquals(List.of( //
                new Connection("Belfast", 141), //
                new Connection("London", 464)), //
                map.getNodeToConnections().get("Dublin"));

        Assertions.assertEquals(List.of( //
                new Connection("Dublin", 141), //
                new Connection("London", 518)), //
                map.getNodeToConnections().get("Belfast"));
    }

    @Test
    public void testShorterPath() throws Exception {
        var map = LocationMap.parseInput(readInput());
        Path shorterPath = map.findShortestPath().orElseThrow();

        try {
            Assertions.assertEquals(new Path(605, List.of("London", "Dublin", "Belfast")), shorterPath);
        } catch (AssertionFailedError e) {
            Assertions.assertEquals(new Path(605, List.of("Belfast", "Dublin", "London")), shorterPath);
        }
    }

    @Test
    public void testLongerPath() throws Exception {
        var map = LocationMap.parseInput(readInput());
        Path shorterPath = map.findLongestPath().orElseThrow();

        try {
            Assertions.assertEquals(new Path(982, List.of("Dublin", "London", "Belfast")), shorterPath);
        } catch (AssertionFailedError e) {
            Assertions.assertEquals(new Path(982, List.of("Belfast", "London", "Dublin")), shorterPath);
        }
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }
}
