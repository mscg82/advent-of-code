package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

public record Reactions(Map<String, Reaction> resultNameToReaction)
{

	public static Reactions parseInput(final BufferedReader in) throws IOException
	{
		try {
			final Map<Component, Source> formulas = in.lines() //
					.map(line -> line.split(" => ")) //
					.collect(Collectors.toMap(parts -> Component.parse(parts[1]), parts -> Source.parse(parts[0])));

			final Map<String, Reaction> resultNameToReaction = formulas.entrySet().stream() //
					.collect(Collectors.toMap(entry -> entry.getKey().element(),
							entry -> new Reaction(entry.getValue(), entry.getKey())));

			return new Reactions(resultNameToReaction);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static long ceilDiv(final long a, final long b)
	{
		return (a + b - 1) / b;
	}

	public long computeMaxFuelQuantity(final long oreQuantity)
	{
		final long oreForOneFuel = computeOreQuantity(1);
		long fuelLow = oreQuantity / oreForOneFuel;
		long fuelHigh = fuelLow * 2;
		while (fuelHigh > fuelLow + 1) {
			final long targetFuel = fuelLow + (fuelHigh - fuelLow) / 2; // mean without overflow
			final long ore = computeOreQuantity(targetFuel);
			if (ore > oreQuantity) {
				fuelHigh = targetFuel;
			} else {
				fuelLow = targetFuel;
			}
		}

		return fuelLow;
	}

	public long computeOreQuantity(final long fuelQuantity)
	{
		long oreQuantity = 0;

		final var fuelReaction = resultNameToReaction.get("FUEL");

		final Map<String, Long> leftOvers = new LinkedHashMap<>();
		final Map<String, Long> queue = new LinkedHashMap<>();
		final Consumer<Component> addToQueue = component -> queue.merge(component.element(), component.quantity(), Long::sum);
		fuelReaction.source().multiply(fuelQuantity).components().forEach(addToQueue);

		while (!queue.isEmpty()) {
			final var it = queue.entrySet().iterator();
			final Map.Entry<String, Long> entry = it.next();
			final var component = new Component(entry.getKey(), entry.getValue());
			it.remove(); // pop from queue

			if ("ORE".equals(component.element())) {
				oreQuantity += component.quantity();
				continue;
			}

			final var reaction = resultNameToReaction.get(component.element());
			final Long leftOver = leftOvers.get(component.element());
			final long requiredQuantity;
			if (leftOver != null) {
				if (leftOver > component.quantity()) {
					// nothing to do, reduce the leftover and continue
					leftOvers.put(component.element(), leftOver - component.quantity());
					continue;
				}
				requiredQuantity = component.quantity() - leftOver;
				leftOvers.remove(component.element());
			} else {
				requiredQuantity = component.quantity();
			}
			final long multiplier = ceilDiv(requiredQuantity, reaction.result().quantity());
			final Source multipliedSource = reaction.source().multiply(multiplier);
			final long totalResultQuantity = multiplier * reaction.result().quantity();
			final long resultLeftOver = totalResultQuantity - requiredQuantity;
			if (resultLeftOver != 0L) {
				leftOvers.merge(component.element(), resultLeftOver, Long::sum);
			}
			multipliedSource.components().forEach(addToQueue);
		}

		return oreQuantity;
	}

	@RecordBuilder
	public record Component(String element, long quantity) implements ReactionsComponentBuilder.With
	{
		public static Component parse(final String value)
		{
			final var pattern = Pattern.compile("(\\d+) (.+)");
			final Matcher componentMatcher = pattern.matcher(value);
			if (!componentMatcher.matches()) {
				throw new IllegalArgumentException("Invalid component " + value);
			}
			return new Component(componentMatcher.group(2), Long.parseLong(componentMatcher.group(1)));
		}
	}

	public record Source(List<Component> components)
	{

		public static Source parse(final String value)
		{
			final List<Component> components = Arrays.stream(value.split(", ")) //
					.map(Component::parse) //
					.toList();
			return new Source(components);
		}

		public Source multiply(final long multiplier)
		{
			final List<Component> newComponents = this.components.stream() //
					.map(comp -> comp.withQuantity(comp.quantity() * multiplier)) //
					.toList();
			return new Source(newComponents);
		}

	}

	public record Reaction(Source source, Component result)
	{

	}
}
