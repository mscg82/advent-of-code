package com.mscg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordManager {

    public static String next(String current) {
        long val = Long.parseLong(toBase26(current), 26);
        val++;
        String result = fromBase26(Long.toString(val, 26));
        while (result.length() < current.length()) {
            result = "a" + result;
        }
        return result;
    }

    public static String nextValid(String current) {
        return nextValid(current, 0L);
    }

    public static String nextValid(String current, long skip) {
        return Stream.iterate(current, PasswordManager::next) //
                .filter(PasswordManager::isValid) //
                .skip(skip) //
                .findFirst() //
                .orElseThrow();
    }

    public static boolean isValid(String password) {
        if (password.contains("i") || password.contains("o") || password.contains("l")) {
            return false;
        }

        boolean tripletFound = false;
        for (int i = 0, l = password.length(); i < l - 2 && !tripletFound; i++) {
            if (password.charAt(i + 1) - password.charAt(i) == 1
                    && password.charAt(i + 2) - password.charAt(i + 1) == 1) {
                tripletFound = true;
            }
        }
        if (!tripletFound) {
            return false;
        }
        
        Map<String, List<Integer>> coupleToPositions = new HashMap<>();
        for (int i = 0, l = password.length(); i < l - 1; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            if (c1 == c2) {
                coupleToPositions.computeIfAbsent(new String(new char[] {c1, c2}), __ -> new ArrayList<>()).add(i);
            }
        }
        long distinctCouples = coupleToPositions.values().stream() //
                .filter(l -> l.size() == 1) //
                .count();

        return distinctCouples >= 2;
    }

    private static String toBase26(String value) {
        return value.chars() //
                .map(c -> switch (c) {
                    case 'a' -> '0';
                    case 'b' -> '1';
                    case 'c' -> '2';
                    case 'd' -> '3';
                    case 'e' -> '4';
                    case 'f' -> '5';
                    case 'g' -> '6';
                    case 'h' -> '7';
                    case 'i' -> '8';
                    case 'j' -> '9';
                    case 'k' -> 'a';
                    case 'l' -> 'b';
                    case 'm' -> 'c';
                    case 'n' -> 'd';
                    case 'o' -> 'e';
                    case 'p' -> 'f';
                    case 'q' -> 'g';
                    case 'r' -> 'h';
                    case 's' -> 'i';
                    case 't' -> 'j';
                    case 'u' -> 'k';
                    case 'v' -> 'l';
                    case 'w' -> 'm';
                    case 'x' -> 'n';
                    case 'y' -> 'o';
                    case 'z' -> 'p';
                    default -> throw new IllegalArgumentException("Invalid char " + ((char) c) + " found in input string");
                }) //
                .collect(() -> new StringBuilder(value.length()), (s, i) -> s.append((char) i), StringBuilder::append) //
                .toString();
    }

    private static String fromBase26(String value) {
        return value.chars() //
                .map(c -> switch (c) {
                    case '0' -> 'a';
                    case '1' -> 'b';
                    case '2' -> 'c';
                    case '3' -> 'd';
                    case '4' -> 'e';
                    case '5' -> 'f';
                    case '6' -> 'g';
                    case '7' -> 'h';
                    case '8' -> 'i';
                    case '9' -> 'j';
                    case 'a' -> 'k';
                    case 'b' -> 'l';
                    case 'c' -> 'm';
                    case 'd' -> 'n';
                    case 'e' -> 'o';
                    case 'f' -> 'p';
                    case 'g' -> 'q';
                    case 'h' -> 'r';
                    case 'i' -> 's';
                    case 'j' -> 't';
                    case 'k' -> 'u';
                    case 'l' -> 'v';
                    case 'm' -> 'w';
                    case 'n' -> 'x';
                    case 'o' -> 'y';
                    case 'p' -> 'z';
                    default -> throw new IllegalArgumentException("Invalid char " + ((char) c) + " found in input string");
                }) //
                .collect(() -> new StringBuilder(value.length()), (s, i) -> s.append((char) i), StringBuilder::append) //
                .toString();
    }
}
