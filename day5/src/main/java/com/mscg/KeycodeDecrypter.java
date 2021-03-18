package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeycodeDecrypter {
    
    private final String doorId;

    public String findPassword1() {
        return LongStream.iterate(0L, l -> l +1) //
                .mapToObj(l -> DigestUtils.md5Hex(doorId + l)) //
                .filter(s -> "00000".equals(s.substring(0, 5))) //
                .limit(8) //
                .map(s -> String.valueOf(s.charAt(5))) //
                .collect(Collectors.joining());
    }

    public String findPassword2() {
        Map<Integer, Character> password = new TreeMap<>();
        
        for (long l = 0; true; l++) {
            String hash = DigestUtils.md5Hex(doorId + l);
            if ("00000".equals(hash.substring(0, 5))) {
                int position = switch (hash.charAt(5)) {
                    case '0' -> 0;
                    case '1' -> 1;
                    case '2' -> 2;
                    case '3' -> 3;
                    case '4' -> 4;
                    case '5' -> 5;
                    case '6' -> 6;
                    case '7' -> 7;
                    default -> {
                        continue;
                    }
                };
                password.computeIfAbsent(position, __ -> hash.charAt(6));
            }

            if (password.size() == 8) {
                break;
            }
        }

        return password.values().stream() //
                .map(String::valueOf) //
                .collect(Collectors.joining());
    }

    public static KeycodeDecrypter parseInput(BufferedReader in) throws IOException {
        return new KeycodeDecrypter(in.readLine());
    }
}
