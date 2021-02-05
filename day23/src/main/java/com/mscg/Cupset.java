package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class Cupset {

    private final int[] cups;
    private final int minCup;
    private final int maxCup;

    public Cupset(final int[] cups) {
        this.cups = cups;

        int min = cups[0];
        int max = cups[1];
        for (int c : cups) {
            if (c < min) {
                min = c;
            }
            if (c > max) {
                max = c;
            }
        }

        this.minCup = min;
        this.maxCup = max;
    }

    public void run(int steps) {
        int currentCupIdx = 0;
        for (int i = 0; i < steps; i++) {
            final int destinationCupIdx = getDestinationCupIndex(currentCupIdx);
            int destinationCup = cups[destinationCupIdx];

            final int next1 = pickCup((currentCupIdx + 1) % cups.length);
            final int next2 = pickCup((currentCupIdx + 2) % cups.length);
            final int next3 = pickCup((currentCupIdx + 3) % cups.length);

            shiftLeft(currentCupIdx, destinationCupIdx);
            final int newDestinationCupIdx = findDestinationIndex(destinationCup);

            cups[(newDestinationCupIdx + 1) % cups.length] = next1;
            cups[(newDestinationCupIdx + 2) % cups.length] = next2;
            cups[(newDestinationCupIdx + 3) % cups.length] = next3;

            currentCupIdx = (currentCupIdx + 1) % cups.length;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = Arrays.stream(cups) //
                .collect(StringBuilder::new, (s, c) -> s.append(c), (s1, s2) -> s1.append(s2));
        return str.toString();
    }

    public String toStringFrom(int firstCup) {
        int firstIndex = findDestinationIndex(firstCup);
        var str = new StringBuilder(cups.length - 1);
        for (int i = (firstIndex + 1) % cups.length; i != firstIndex; i = (i + 1) % cups.length) {
            str.append(cups[i]);
        }
        return str.toString();
    }

    private int pickCup(int index) {
        int cup = cups[index];
        cups[index] = Integer.MIN_VALUE;
        return cup;
    }

    private void shiftLeft(int currentIndex, int destinationIndex) {
        int firstIndex = (currentIndex + 4) % cups.length;
        int lastIndex = destinationIndex;
        int writeIndex = (currentIndex + 1) % cups.length;

        int copySize = lastIndex >= firstIndex ? (lastIndex - firstIndex + 1)
                : (lastIndex - firstIndex + 1) + cups.length;
        int workIndex = firstIndex;
        for (int i = 0; i < copySize; i++) {
            cups[writeIndex] = pickCup(workIndex);
            workIndex = (workIndex + 1) % cups.length;
            writeIndex = (writeIndex + 1) % cups.length;
        }
    }

    private int getDestinationCupIndex(int currentCupIdx) {
        final int currentCup = cups[currentCupIdx];
        final int next1 = cups[(currentCupIdx + 1) % cups.length];
        final int next2 = cups[(currentCupIdx + 2) % cups.length];
        final int next3 = cups[(currentCupIdx + 3) % cups.length];

        int destinationCup = currentCup - 1;
        if (destinationCup < minCup) {
            destinationCup = maxCup;
        }
        while (destinationCup == next1 || destinationCup == next2 || destinationCup == next3) {
            if (destinationCup == currentCup) {
                throw new IllegalStateException("Unable to find destination cup");
            }
            destinationCup = destinationCup - 1;
            if (destinationCup < minCup) {
                destinationCup = maxCup;
            }
        }

        return findDestinationIndex(destinationCup);
    }

    private int findDestinationIndex(int destinationCup) {
        for (int i = 0; i < cups.length; i++) {
            if (cups[i] == destinationCup) {
                return i;
            }
        }
        throw new IllegalStateException("This should not happen");
    }

    public static Cupset parseInput(BufferedReader in) throws IOException {
        String line = in.readLine();
        int length = line.length();
        final int[] cups = new int[length];
        for (int i = 0; i < length; i++) {
            cups[i] = Integer.parseInt(line.substring(i, i + 1));
        }
        return new Cupset(cups);
    }

}