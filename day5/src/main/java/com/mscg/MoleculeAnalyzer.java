package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public record MoleculeAnalyzer(List<Character> components) {

    public String reduce() {
        final var components = new LinkedList<>(this.components);
        boolean isReduced;
        do {
            isReduced = false;
            final var it = components.listIterator();
            char prevChar = it.next();
            while (it.hasNext()) {
                final char curChar = it.next();
                if ((Character.isUpperCase(curChar) && Character.isLowerCase(prevChar)) || //
                        (Character.isLowerCase(curChar) && Character.isUpperCase(prevChar))) {
                    if (Character.toLowerCase(curChar) == Character.toLowerCase(prevChar)) {
                        // reduce
                        it.previous();
                        it.previous();
                        it.remove();
                        it.next();
                        it.remove();
                        isReduced = true;
                        break;
                    }
                }
                prevChar = curChar;
            }
        } while (isReduced);

        return components.stream() //
                .map(Object::toString) //
                .collect(Collectors.joining());
    }

    public static MoleculeAnalyzer parseInput(final BufferedReader in) throws IOException {
        final List<Character> components = in.readLine() //
                .chars() //
                .mapToObj(c -> (char) c) //
                .toList();
        return new MoleculeAnalyzer(components);
    }

}
