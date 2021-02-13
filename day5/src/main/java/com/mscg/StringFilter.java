package com.mscg;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;

public final class StringFilter {

    private StringFilter() {

    }

    public static boolean isNice(String value) {
        if (value.indexOf("ab") >= 0 || value.indexOf("cd") >= 0 || value.indexOf("pq") >= 0
                || value.indexOf("xy") >= 0) {
            return false;
        }

        long vowelsCount = value.chars() //
                .filter(c -> c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') //
                .count();
        if (vowelsCount < 3) {
            return false;
        }

        boolean hasDoubles = StreamUtils.windowed(value.chars().boxed(), 2) //
                .anyMatch(couple -> couple.get(0).equals(couple.get(1)));

        return hasDoubles;
    }

    public static boolean isNice2(String value) {
        boolean hasDoubles = StreamUtils.windowed(value.chars().boxed(), 3) //
                .anyMatch(triple -> triple.get(0).equals(triple.get(2)));
        if (!hasDoubles) {
            return false;
        }

        Map<String, List<Couple>> strToInfo = StreamUtils.zipWithIndex(StreamUtils.windowed(value.chars().boxed(), 2)) //
                .map(indexed -> new Couple(
                        new String(new char[] { toChar(indexed.getValue().get(0)), toChar(indexed.getValue().get(1)) }),
                        indexed.getIndex())) //
                .collect(Collectors.groupingBy(Couple::str));

        long doubleCouples = strToInfo.entrySet().stream() //
                .filter(entry -> entry.getValue().size() >= 2) //
                .count();

        if (doubleCouples == 0) {
            return false;
        }

        boolean noOverlappingCouples = strToInfo.entrySet().stream() //
                .filter(entry -> entry.getValue().size() >= 2) //
                .allMatch(entry -> {
                    List<Couple> couples = entry.getValue();
                    boolean nonOverlapping = false;
                    for (int i = 0, l = couples.size(); i < l - 1; i++) {
                        for (int j = i + 1; j < l; j++) {
                            if (Math.abs(couples.get(j).position() - couples.get(i).position()) > 1) {
                                nonOverlapping = true;
                            }
                        }
                    }
                    return nonOverlapping;
                });

        return noOverlappingCouples;
    }

    private static char toChar(Integer i) {
        return (char) i.intValue();
    }

    private static record Couple(String str, long position) {
    }

}
