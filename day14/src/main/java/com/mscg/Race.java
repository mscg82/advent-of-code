package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Race {

    private final List<Horse> horses;

    public List<Position> run(int seconds) {
        return horses.stream() //
                .map(horse -> {
                    int timeBlock = horse.runTime() + horse.restTime();
                    int blocks = seconds / timeBlock;
                    int distance = horse.speed() * horse.runTime() * blocks;
                    int reminder = seconds % timeBlock;
                    distance += horse.speed() * Math.min(horse.runTime(), reminder);
                    return new Position(horse.name(), distance);
                }) //
                .collect(Collectors.toUnmodifiableList());
    }

    public static Race parseInput(BufferedReader in) throws IOException {
        var pattern = Pattern
                .compile("(.+?) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.");
        List<Horse> horses = in.lines() //
                .map(pattern::matcher) //
                .filter(Matcher::matches) //
                .map(matcher -> new Horse(matcher.group(1), Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)))) //
                .collect(Collectors.toUnmodifiableList());
        return new Race(horses);
    }

    public static record Horse(String name, int speed, int runTime, int restTime) {
    }

    public static record Position(String horseName, int km) {
    }

}
