package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.OptionalInt;

public class GiftDelivery {

    private final long target;
    private final int maxHouse;
    private final int maxElf;
    private final long[] houses;

    public GiftDelivery(long target, int maxHouse, int maxElf, boolean limitGifts) {
        this.target = target;
        this.maxHouse = maxHouse;
        this.maxElf = maxElf;

        this.houses = new long[this.maxHouse];
        if (limitGifts) {
            for (int elf = 1; elf <= this.maxElf; elf++) {
                for (int i = 1; i <= 50; i++) {
                    int index = elf * i;
                    if (index > this.maxHouse) {
                        break;
                    }
                    houses[index - 1] += elf * 11;
                }
            }
        }
        else {
            for (int elf = 1; elf <= this.maxElf; elf++) {
                for (int house = elf; house < this.maxHouse; house += elf) {
                    houses[house - 1] += elf * 10;
                }
            }
        }
    }

    public long countGifts(int number) {
        return houses[number - 1];
    }

    public OptionalInt findHouseNumber() {
        for (int i = 0; i < maxHouse; i++) {
            if (houses[i] >= target) {
                return OptionalInt.of(i + 1);
            }
        }
        return OptionalInt.empty();
    }

    public static GiftDelivery parseInput(BufferedReader in, int maxHouse, int maxElf, boolean limitGifts) throws IOException {
        long target = Long.parseLong(in.readLine());
        return new GiftDelivery(target, maxHouse, maxElf, limitGifts);
    }

}
