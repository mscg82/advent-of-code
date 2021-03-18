package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static KeycodeDecrypter parseInput(BufferedReader in) throws IOException {
        return new KeycodeDecrypter(in.readLine());
    }
}
