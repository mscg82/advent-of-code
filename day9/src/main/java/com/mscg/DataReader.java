package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public record DataReader(String data, boolean clean) {

    public DataReader(final String data) {
        this(data, false);
    }

    public SplittedDataReader cleanGarbage() {
        final var newData = new StringBuilder(data.length());
        final var garbage = new StringBuilder(data.length());
        boolean inGarbage = false;
        for (int i = 0, l = data.length(); i < l; i++) {
            switch (data.charAt(i)) {
                case '!' -> {
                    i++; // skip next char
                    continue;
                }
                case '<' -> {
                    if (!inGarbage) {
                        inGarbage = true;
                        continue;
                    }
                }
                case '>' -> {
                    inGarbage = false;
                    continue;
                }
            }

            if (!inGarbage) {
                newData.append(data.charAt(i));
            } else {
                garbage.append(data.charAt(i));
            }
        }
        return new SplittedDataReader(new DataReader(newData.toString(), true), garbage.toString());
    }

    public List<Group> getGroups() {
        if (!clean) {
            return cleanGarbage().dataReader().getGroups();
        }

        final List<Group> groups = new ArrayList<>();
        final Deque<Group> activeGroups = new LinkedList<>();
        for (int i = 0, l = data.length(); i < l; i++) {
            switch (data.charAt(i)) {
                case '{' -> {
                    final Group group;
                    if (activeGroups.isEmpty()) {
                        group = new Group(1);
                    } else {
                        group = new Group(activeGroups.getFirst().depth() + 1);
                    }
                    activeGroups.addFirst(group);
                    groups.add(group);
                }
                case '}' -> activeGroups.removeFirst();
            }
        }
        return List.copyOf(groups);
    }

    public static long getScore(final List<Group> groups) {
        return groups.stream().mapToInt(Group::depth).sum();
    }

    public static DataReader parseInput(final BufferedReader in) throws IOException {
        return new DataReader(in.readLine(), false);
    }

    public static record SplittedDataReader(DataReader dataReader, String garbage) {

    }

    public static record Group(int depth) {

    }

}
