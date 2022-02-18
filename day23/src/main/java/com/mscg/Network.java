package com.mscg;

import lombok.SneakyThrows;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public record Network(List<Node> nodes)
{

	public static Network parseInput(final BufferedReader in, final int numNodes) throws IOException
	{
		final var computer = IntcodeV7.parseInput(in);
		final List<Node> nodes = IntStream.range(0, numNodes) //
				.mapToObj(index -> new Node(computer, index)) //
				.toList();
		return new Network(nodes);
	}

	@SneakyThrows
	public long run()
	{
		final AtomicLong result = new AtomicLong(0);

		final List<IntcodeV7.QueueInputGenerator> inputGenerators = nodes.stream() //
				.map(node -> IntcodeV7.QueueInputGenerator.queue(node.index())) //
				.toList();

		final ExecutorService executorService = Executors.newFixedThreadPool(nodes.size());

		final OutputConsumer outputConsumer = (source, target, x, y) -> {
			if (target == 255) {
				result.compareAndSet(0, y);
				executorService.shutdownNow();
				return;
			}

			inputGenerators.get(target).add(x, y);
		};

		final List<NodeRunner> nodeRunners = Seq.seq(nodes.stream()) //
				.zipWithIndex() //
				.map(nodeIdx -> new NodeRunner(nodeIdx.v1(), inputGenerators.get(nodeIdx.v2().intValue()), outputConsumer)) //
				.toList();

		try {
			final List<Future<Void>> futures = executorService.invokeAll(nodeRunners);
			for (final Future<Void> future : futures) {
				future.get(); // wait for all nodes to finish
			}
			executorService.shutdown();
			if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Failed to stop executor service");
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}

		return result.get();
	}

	@FunctionalInterface
	private interface OutputConsumer
	{
		void accept(int source, int target, long x, long y);
	}

	private record Node(IntcodeV7 computer, int index) {}

	private record NodeRunner(Network.Node node, IntcodeV7.QueueInputGenerator input, OutputConsumer outputConsumer)
			implements Callable<Void>
	{

		@Override
		public Void call() throws Exception
		{
			IntcodeV7 computer = node.computer();

			while (true) {
				computer = computer.execute(input, 3);
				if (computer.halted()) {
					break;
				}
				final long[] outputs = computer.outputs();
				outputConsumer.accept(node.index(), (int) outputs[0], outputs[1], outputs[2]);
			}

			return null;
		}
	}
}
