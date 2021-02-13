package com.mscg;

import com.codepoetics.protonpack.StreamUtils;

public final class StringFilter {
    
    private StringFilter() {

    }

    public static boolean isNice(String value) {
        if (value.indexOf("ab") >= 0 || value.indexOf("cd") >= 0 || value.indexOf("pq") >= 0 || value.indexOf("xy") >= 0) {
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

}
