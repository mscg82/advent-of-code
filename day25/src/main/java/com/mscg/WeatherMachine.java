package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WeatherMachine {

    private final int targetRow;
    private final int targetCol;

    public long getSecutiryValue() {
        return getValueForCell(targetRow, targetCol);
    }

    public static int getSequenceForCell(int i, int j) {
        if (i <= 0) {
            throw new IllegalArgumentException("i must be > 0");
        }
        if (j <= 0) {
            throw new IllegalArgumentException("j must be > 0");
        }
        return (i * i - i + 2) / 2 + i * (j - 1) + (j * j - j) / 2;
    }

    public static long getValueForCell(int i, int j) {
        int sequence = getSequenceForCell(i, j);
        return LongStream.iterate(20151125, v -> (v * 252533) % 33554393) //
                .skip(sequence - 1) //
                .limit(1) //
                .findFirst() //
                .orElseThrow();
    }

    public static WeatherMachine parseInput(BufferedReader in) throws IOException {
        String line = in.readLine();
        var pattern = Pattern.compile("row (\\d+), column (\\d+)");
        var matcher = pattern.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Unable to parse input");
        }
        return new WeatherMachine(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }
}
