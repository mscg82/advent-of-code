package com.mscg;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringParser {

    public static String cleanString(final String source) {
        var builder = new StringBuilder();

        for (int i = 1, l = source.length(); i < l - 1; i++) {
            char c = source.charAt(i);
            switch (c) {
                case '\\' -> {
                    char c2 = source.charAt(++i);
                    char charToAdd = switch (c2) {
                        case '\\', '"' -> c2;
                        case 'x' -> {
                            char c3 = source.charAt(++i);
                            char c4 = source.charAt(++i);
                            String hexCode = new String(new char[] { c3, c4 });
                            yield (char) Integer.parseInt(hexCode, 16);
                        }
                        default -> throw new IllegalArgumentException("Invalid string");
                    };
                    builder.append(charToAdd);
                }

                default -> builder.append(c);
            }
        }

        return builder.toString();
    }

    public static String expandString(final String source) {
        return "\"" + source //
                .replace("\\", "\\\\") //
                .replace("\"", "\\\"") + "\"";
    }

}
