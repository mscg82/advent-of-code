package com.mscg;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LookAndSay {
    
    public static String transform(String source) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0, l = source.length(); i < l; i++) {
            if (i == l - 1) {
                ret.append(1).append(source.charAt(i));
                continue;
            }

            for (int j = i + 1; j <= l; j++) {
                if (j == l || source.charAt(i) != source.charAt(j)) {
                    ret.append(j - i).append(source.charAt(i));
                    i = j;
                }
            }
        }

        return ret.toString();
    }

}
