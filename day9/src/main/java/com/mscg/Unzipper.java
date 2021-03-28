package com.mscg;

import java.io.BufferedReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Unzipper {

    public static long countNonEmptyChars(CharSequence input) {
        return input.chars() //
                .filter(c -> !Character.isWhitespace((char) c)) //
                .count();
    }

    public static CharSequence unzip(BufferedReader in) {
        return unzip(in.lines().collect(Collectors.joining()));
    }

    public static CharSequence unzip(CharSequence input) {
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0, l = input.length(); i < l; i++) {
            char c = input.charAt(i);
            if (c != '(') {
                result.append(c);
            } else {
                StringBuilder markerLength = new StringBuilder();
                StringBuilder markerRepetition = new StringBuilder();
                StringBuilder marker = markerLength;
                for (int j = i + 1; j < l; j++) {
                    char mc = input.charAt(j);
                    if (mc == ')') {
                        i = j + 1;
                        break;
                    }
                    if (mc == 'x') {
                        marker = markerRepetition;
                        continue;
                    }
                    marker.append(mc);
                }
                int length = Integer.parseInt(markerLength.toString());
                int repetition = Integer.parseInt(markerRepetition.toString());
                CharSequence token = input.subSequence(i, i + length);
                IntStream.range(0, repetition).forEach(__ -> result.append(token));
                i += (length - 1);
            }
        }
        return result;
    }

}
