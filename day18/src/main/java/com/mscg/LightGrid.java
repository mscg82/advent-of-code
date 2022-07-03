package com.mscg;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LightGrid
{

	private final List<List<Light>> lights;

	private final boolean cornersStuck;

	public static LightGrid parseInput(final BufferedReader in, final boolean cornerStuck) throws IOException
	{
		final List<? extends List<Light>> lights = in.lines() //
				.map(line -> line.chars() //
						.mapToObj(c -> Light.of((char) c)) //
						.collect(Collectors.toCollection(ArrayList::new))) //
				.toList();
		return new LightGrid(lights, cornerStuck);
	}

	public LightGrid(final List<List<Light>> lights)
	{
		this(lights, false);
	}

	public LightGrid(final List<? extends List<Light>> lights, final boolean cornersStuck)
	{
		if (cornersStuck) {
			List<Light> row = lights.get(0);
			row.set(0, Light.ON);
			row.set(row.size() - 1, Light.ON);

			row = lights.get(lights.size() - 1);
			row.set(0, Light.ON);
			row.set(row.size() - 1, Light.ON);
		}
		this.cornersStuck = cornersStuck;
		this.lights = lights.stream() //
				.map(List::copyOf) //
				.toList();
	}

	public LightGrid next()
	{
		final var newLights = cloneLights();
		for (int i = 0, l = lights.size(); i < l; i++) {
			final List<Light> row = lights.get(i);
			for (int j = 0, l2 = row.size(); j < l2; j++) {
				final List<Light> neighbours = new ArrayList<>(8);
				for (int k1 = Math.max(0, i - 1); k1 <= Math.min(l - 1, i + 1); k1++) {
					for (int k2 = Math.max(0, j - 1); k2 <= Math.min(l2 - 1, j + 1); k2++) {
						if (k1 != i || k2 != j) {
							neighbours.add(lights.get(k1).get(k2));
						}
					}
				}
				final int onNeighbours = (int) neighbours.stream() //
						.filter(light -> light == Light.ON) //
						.count();
				newLights.get(i).set(j, switch (lights.get(i).get(j)) {
					case ON -> (onNeighbours == 2 || onNeighbours == 3) ? Light.ON : Light.OFF;
					case OFF -> (onNeighbours == 3) ? Light.ON : Light.OFF;
				});
			}
		}

		return new LightGrid(newLights, cornersStuck);
	}

	@Override
	public String toString()
	{
		return lights.stream() //
				.map(row -> row.stream().map(Object::toString).collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	private List<? extends List<Light>> cloneLights()
	{
		return lights.stream() //
				.map(ArrayList::new) //
				.toList();
	}

	public enum Light
	{
		ON, OFF;

		public static Light of(final char c)
		{
			return switch (c) {
				case '#' -> Light.ON;
				case '.' -> Light.OFF;
				default -> throw new IllegalArgumentException("Invalid light state char " + c);
			};
		}

		@Override
		public String toString()
		{
			return switch (this) {
				case ON -> "#";
				case OFF -> ".";
			};
		}
	}

}
