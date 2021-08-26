package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public record Recipes(String target) {

    public String findRecipe() {
        final int target = Integer.parseInt(this.target);
        final byte[] values = new byte[(target + 10) * 2];
        values[0] = 3;
        values[1] = 7;
        int size = 2;

        int elf1 = 0;
        int elf2 = 1;
        while (size < target + 10) {
            final byte nextValue = (byte) (values[elf1] + values[elf2]);
            if (nextValue < 10) {
                values[size++] = nextValue;
            } else {
                values[size++] = (byte) (nextValue / 10);
                values[size++] = (byte) (nextValue % 10);
            }
            elf1 = (elf1 + values[elf1] + 1) % size;
            elf2 = (elf2 + values[elf2] + 1) % size;
        }

        final var res = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            res.append(values[target + i]);
        }

        return res.toString();
    }

    public int findLeftRecipes() {
        final byte[] values = new byte[50_000_000];
        values[0] = 3;
        values[1] = 7;
        int size = 2;

        int elf1 = 0;
        int elf2 = 1;
        boolean found = false;
        final byte[] target = getTarget();
        while (size < (values.length - 1) && !found) {
            final byte nextValue = (byte) (values[elf1] + values[elf2]);
            if (nextValue < 10) {
                values[size++] = nextValue;
            } else {
                values[size++] = (byte) (nextValue / 10);
                found = size >= target.length && Arrays.equals(target, 0, target.length, values, size - target.length, size);
                if (found) {
                    break;
                }
                values[size++] = (byte) (nextValue % 10);
            }
            elf1 = (elf1 + values[elf1] + 1) % size;
            elf2 = (elf2 + values[elf2] + 1) % size;

            found = size >= target.length && Arrays.equals(target, 0, target.length, values, size - target.length, size);
        }

        if (!found) {
            throw new IllegalStateException("Can find target recipe");
        }
        return size - target.length;
    }

    private byte[] getTarget() {
        final var res = new byte[target.length()];
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (target.charAt(i) - '0');
        }
        return res;
    }

    public static Recipes parseInput(final BufferedReader in) throws IOException {
        return new Recipes(in.readLine());
    }

}
