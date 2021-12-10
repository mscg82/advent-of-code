package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.lambda.Seq;
import org.jooq.lambda.Window;

public record SifImageDecoder(int[][][] pixelsPerLayer, int layers, int rows, int cols)
{

	public static SifImageDecoder parseInput(final BufferedReader in, final int rows, final int cols) throws IOException
	{
		final int layerSize = rows * cols;
		final Seq<Window<Integer>> layersWindows = Seq.seq(in.readLine().chars().map(c -> c - '0')) //
				.window(0, layerSize - 1) //
				.filter(w -> w.count() == layerSize) //
				.filter(w -> w.rowNumber() % layerSize == 0);
		final int[][][] pixelsPerLayer = layersWindows //
				.map(layer -> {
					final Seq<Window<Integer>> imageWindows = layer.window() //
							.window(0, cols - 1) //
							.filter(w -> w.count() == cols) //
							.filter(w -> w.rowNumber() % cols == 0);
					return imageWindows //
							.map(image -> image.window().mapToInt(Integer::intValue).toArray()) //
							.toArray(int[][]::new);
				}) //
				.toArray(int[][][]::new);
		return new SifImageDecoder(pixelsPerLayer, pixelsPerLayer.length, rows, cols);
	}

	public long scoreLayerWithFewerZeros()
	{
		final int[][] minLayer = Arrays.stream(pixelsPerLayer) //
				.min(Comparator.comparingLong(layer -> Arrays.stream(layer) //
						.flatMapToInt(Arrays::stream) //
						.filter(pixel -> pixel == 0) //
						.count())) //
				.orElseThrow();

		final Map<Integer, Long> pixelToFrequency = Arrays.stream(minLayer) //
				.flatMapToInt(Arrays::stream) //
				.boxed() //
				.collect(Collectors.groupingBy(v -> v, Collectors.counting()));

		return pixelToFrequency.get(1) * pixelToFrequency.get(2);
	}

	public String decodeImage()
	{
		final int[][] image = new int[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				for (int l = 0; l < layers; l++) {
					final int pixel = pixelsPerLayer[l][i][j];
					if (pixel == 0 || pixel == 1) {
						image[i][j] = pixel;
						break;
					}
				}
			}
		}

		return Arrays.stream(image) //
				.map(row -> Arrays.stream(row) //
						.mapToObj(pixel -> switch (pixel) {
						case 0 -> " ";
						case 1 -> "#";
						default -> throw new IllegalArgumentException();
						}) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}
}
