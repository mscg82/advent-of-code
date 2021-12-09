package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public record Amplifier(IntcodeV3 computer) {

	public static Amplifier parseInput(final BufferedReader in) throws IOException
	{
		return new Amplifier(IntcodeV3.parseInput(in));
	}

	private static List<int[]> generatePermutations(final int[] elements)
	{
		final List<int[]> permutations = new ArrayList<>();
		generetePermutationsRecursive(elements.length, elements, permutations);
		return permutations;
	}

	private static void generetePermutationsRecursive(final int n, final int[] elements, final List<int[]> permutations)
	{
		if (n == 1) {
			permutations.add(elements.clone());
		} else {
			for (int i = 0; i < n - 1; i++) {
				generetePermutationsRecursive(n - 1, elements, permutations);
				if (n % 2 == 0) {
					swap(elements, i, n - 1);
				} else {
					swap(elements, 0, n - 1);
				}
			}
			generetePermutationsRecursive(n - 1, elements, permutations);
		}
	}

	private static void swap(final int[] input, final int a, final int b)
	{
		final int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;
	}

	public int computeMaxOutput()
	{
		final List<int[]> permutations = generatePermutations(new int[] { 0, 1, 2, 3, 4 });
		return permutations.stream() //
				.mapToInt(input -> {
					final var resultA = computer.execute(List.of(input[0], 0).iterator(), true);
					final var resultB = computer.execute(List.of(input[1], resultA.outputs()[0]).iterator(), true);
					final var resultC = computer.execute(List.of(input[2], resultB.outputs()[0]).iterator(), true);
					final var resultD = computer.execute(List.of(input[3], resultC.outputs()[0]).iterator(), true);
					final var resultE = computer.execute(List.of(input[4], resultD.outputs()[0]).iterator(), true);

					return resultE.outputs()[0];
				}) //
				.max() //
				.orElseThrow();
	}

	public int computeMaxOutputWithLoop()
	{
		final List<int[]> permutations = generatePermutations(new int[] { 5, 6, 7, 8, 9 });

		int maxOutput = Integer.MIN_VALUE;
		for (final int[] input : permutations) {
			final var amps = new IntcodeV3[] { //
					new IntcodeV3(computer.data()), //
					new IntcodeV3(computer.data()), //
					new IntcodeV3(computer.data()), //
					new IntcodeV3(computer.data()), //
					new IntcodeV3(computer.data()) //
			};

			final List<Deque<Integer>> inputs = List.of( //
					new LinkedList<>(List.of(input[0])), //
					new LinkedList<>(List.of(input[1])), //
					new LinkedList<>(List.of(input[2])), //
					new LinkedList<>(List.of(input[3])), //
					new LinkedList<>(List.of(input[4])));

			int lastSignal = 0;
			while (true) {
				inputs.get(0).add(lastSignal);
				amps[0] = amps[0].execute(inputs.get(0), true);
				if (amps[0].halted()) {
					break;
				}

				inputs.get(1).add(amps[0].outputs()[0]);
				amps[1] = amps[1].execute(inputs.get(1), true);
				if (amps[1].halted()) {
					break;
				}

				inputs.get(2).add(amps[1].outputs()[0]);
				amps[2] = amps[2].execute(inputs.get(2), true);
				if (amps[2].halted()) {
					break;
				}

				inputs.get(3).add(amps[2].outputs()[0]);
				amps[3] = amps[3].execute(inputs.get(3), true);
				if (amps[3].halted()) {
					break;
				}

				inputs.get(4).add(amps[3].outputs()[0]);
				amps[4] = amps[4].execute(inputs.get(4), true);
				if (amps[4].halted()) {
					break;
				}

				lastSignal = amps[4].outputs()[0];
			}

			if (lastSignal > maxOutput) {
				maxOutput = lastSignal;
			}
		}

		return maxOutput;
	}

}
