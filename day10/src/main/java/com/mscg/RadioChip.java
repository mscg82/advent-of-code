package com.mscg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record RadioChip(List<Instruction> instructions)
{

	public static RadioChip parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Instruction> instructions = in.lines() //
					.map(Instruction::from) //
					.toList();
			return new RadioChip(instructions);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public long computeSignalStrength()
	{
		final var cpu = new Cpu(instructions);
		final long[] signalStrength = new long[1];
		cpu.execute(currentCpu -> {
			if (currentCpu.cycle == 20 || //
					currentCpu.cycle == 60 || //
					currentCpu.cycle == 100 || //
					currentCpu.cycle == 140 || //
					currentCpu.cycle == 180 || //
					currentCpu.cycle == 220) {
				signalStrength[0] += currentCpu.cycle * currentCpu.x;
			}
			return currentCpu.cycle <= 220;
		});
		return signalStrength[0];
	}

	public String renderImage()
	{
		final var cpu = new Cpu(instructions);

		final int rows = 6;
		final int cols = 40;
		final StringBuilder buffer = new StringBuilder(" ".repeat(cols).repeat(rows));

		cpu.executeFully(currentCpu -> {
			final int index = (int) currentCpu.cycle - 1;
			final int indexInRow = index % cols;
			if (Math.abs(indexInRow - currentCpu.x) <= 1) {
				buffer.setCharAt(index, '█');
			}
		});

		final StringBuilder display = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			display.append(buffer.subSequence(i * cols, (i + 1) * cols)).append('\n');
		}
		return display.toString();
	}

	public sealed interface Instruction permits Instruction.AddX, Instruction.Noop
	{

		static Instruction from(final String line)
		{
			final String[] parts = line.split(" ");
			return switch (parts[0]) {
				case "addx" -> new AddX(Long.parseLong(parts[1]));
				case "noop" -> Noop.NOOP;
				default -> throw new IllegalArgumentException("Illegal instruction " + line);
			};
		}

		record AddX(long value) implements Instruction {}

		enum Noop implements Instruction
		{
			NOOP
		}

	}

	@Getter
	@RequiredArgsConstructor
	private static class Cpu
	{
		private int ip = 0;

		private long x = 1;

		private long cycle = 0;

		private final List<Instruction> instructions;

		public void executeFully(final Consumer<Cpu> analyzer)
		{
			execute(current -> {
				analyzer.accept(current);
				return true;
			});
		}

		public void execute(final Predicate<Cpu> analyzer)
		{
			while (ip < instructions.size()) {
				final var current = instructions.get(ip);
				switch (current) {
					//noinspection ConstantConditions
					case Instruction.AddX(long value) -> {
						increaseCycle(analyzer);
						increaseCycle(analyzer);
						x += value;
					}
					case Instruction.Noop __ -> {
						increaseCycle(analyzer);
					}
				}
				ip++;
			}
		}

		private void increaseCycle(final Predicate<Cpu> analyzer)
		{
			cycle++;
			if (!analyzer.test(this)) {
				ip = instructions.size();
			}
		}
	}

}
