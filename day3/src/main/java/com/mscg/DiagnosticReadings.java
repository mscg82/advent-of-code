package com.mscg;

import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public record DiagnosticReadings(List<BitSet> values, int inputLength) {

    public static DiagnosticReadings parseInput(final BufferedReader in) throws IOException {
        try {
            final var values = in.lines() //
                    .mapToLong(s -> Long.parseLong(s, 2)) //
                    .mapToObj(v -> BitSet.valueOf(new long[]{ v })) //
                    .toList();

            final int inputLength = values.stream() //
                    .mapToInt(BitSet::length) //
                    .max() //
                    .orElseThrow();

            return new DiagnosticReadings(values, inputLength);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public long findPowerConsumption() {
        final List<BitSet> positionToBits = computePositionToBits(values);

        final BitSet gammaRateBitset = new BitSet(inputLength);
        for (int pos = 0, l = positionToBits.size(); pos < l; pos++) {
            final var bitset = positionToBits.get(pos);
            final int numOfOnes = bitset.cardinality();
            final int numOfZeros = values.size() - numOfOnes;
            if (numOfOnes > numOfZeros) {
                gammaRateBitset.set(pos);
            }
        }

        final BitSet epsilonRateBitset = BitSet.valueOf(gammaRateBitset.toLongArray());
        epsilonRateBitset.flip(0, inputLength);

        final long gammaRate = gammaRateBitset.toLongArray()[0];
        final long epsilonRate = epsilonRateBitset.toLongArray()[0];

        return gammaRate * epsilonRate;
    }

    public long findLifeSupportRating() {
        final BitSet oxygenRatingBitset = applyBitFilter((numOfOnes, numOfZeros) -> numOfOnes >= numOfZeros) //
                .orElseThrow(() -> new IllegalStateException("Can't find value for oxygen rating"));

        final BitSet co2ScrubberRatingBitset = applyBitFilter((numOfOnes, numOfZeros) -> numOfZeros > numOfOnes) //
                .orElseThrow(() -> new IllegalStateException("Can't find value for co2 scrubber rating"));

        final long oxygenRating = oxygenRatingBitset.toLongArray()[0];
        final long co2ScrubberRating = co2ScrubberRatingBitset.toLongArray()[0];

        return oxygenRating * co2ScrubberRating;
    }

    private List<BitSet> computePositionToBits(final Collection<BitSet> values) {
        final int numValues = values.size();
        final List<BitSet> positionToBits = IntStream.range(0, inputLength) //
                .mapToObj(__ -> new BitSet(numValues)) //
                .toList();

        Seq.seq(values.stream()) //
                .zipWithIndex() //
                .forEach(indexed -> {
                    final int idx = indexed.v2().intValue();
                    final var bitset = indexed.v1();
                    bitset.stream().forEach(i -> positionToBits.get(i).set(idx));
                });

        return positionToBits;
    }

    private Optional<BitSet> applyBitFilter(final BitToKeepGenerator bitToKeepGenerator) {
        var valuesToCheck = values;
        for (int i = inputLength - 1; i >= 0; i--) {
            final List<BitSet> positionToBits = computePositionToBits(valuesToCheck);
            final int numOfOnes = positionToBits.get(i).cardinality();
            final int numOfZeros = valuesToCheck.size() - numOfOnes;
            final boolean bitToKeep = bitToKeepGenerator.generateBitToKeep(numOfOnes, numOfZeros);
            final int idx = i;
            valuesToCheck = valuesToCheck.stream() //
                    .filter(bitset -> bitset.get(idx) == bitToKeep) //
                    .toList();
            if (valuesToCheck.isEmpty()) {
                return Optional.empty();
            }
            if (valuesToCheck.size() == 1) {
                return Optional.of(valuesToCheck.get(0));
            }
        }
        return Optional.empty();
    }

    @FunctionalInterface
    private interface BitToKeepGenerator {

        boolean generateBitToKeep(int numOfOnes, int numOfZeros);

    }

}
