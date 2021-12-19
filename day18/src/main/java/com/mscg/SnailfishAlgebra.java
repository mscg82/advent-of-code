package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.lambda.Seq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public record SnailfishAlgebra(List<Number> numbers)
{

	public static SnailfishAlgebra parseInput(final BufferedReader in) throws IOException
	{
		try {
			final List<Number> numbers = in.lines() //
					.map(Number::parse) //
					.toList();
			return new SnailfishAlgebra(numbers);
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static void depthLeftFirstVisitAll(final Node root, final Consumer<NodeWithStatus> visitNode)
	{
		depthLeftFirstVisit(root, node -> {
			visitNode.accept(node);
			return null;
		});
	}

	private static <T> T depthLeftFirstVisit(final Node root, final Function<NodeWithStatus, T> visitNode)
	{
		final Deque<NodeWithStatus> stack = new LinkedList<>();
		stack.addFirst(NodeWithStatus.of(root));

		while (!stack.isEmpty()) {
			final var currentNode = stack.pop();
			final T result = visitNode.apply(currentNode);
			if (result != null) {
				return result;
			}

			switch (currentNode.node()) {
				case BiNode b -> {
					switch (currentNode.status()) {
						case VISIT_LEFT -> {
							stack.addFirst(new NodeWithStatus(b, NodeStatus.VISIT_RIGHT));
							stack.addFirst(NodeWithStatus.of(b.getLeft()));
						}
						case VISIT_RIGHT -> {
							stack.addFirst(new NodeWithStatus(b, NodeStatus.COMPLETE));
							stack.addFirst(NodeWithStatus.of(b.getRight()));
						}
						case COMPLETE -> {}
					}
				}
				case Value ignore -> {}
			}
		}

		return null;
	}

	public long computeSumMagnitude()
	{
		final Number total = Seq.seq(numbers.stream()) //
				.foldLeft(null, (s, n) -> s == null ? n : s.add(n));
		return total.magnitude();
	}

	public long computeMaxMagnitude()
	{
		return Seq.seq(numbers.stream()) //
				.crossSelfJoin() //
				.filter(t -> t.v1() != t.v2()) //
				.mapToLong(t -> t.v1().add(t.v2()).magnitude()) //
				.max() //
				.orElseThrow();
	}

	static Optional<Value> closestLeftNode(final Node node)
	{
		var curNode = node;
		var switchingParent = curNode.getParent();
		while (switchingParent instanceof BiNode b && b.getLeft() == curNode) {
			curNode = switchingParent;
			switchingParent = curNode.getParent();
		}
		if (!(switchingParent instanceof BiNode parentNode)) {
			return Optional.empty();
		}

		var leftNode = parentNode.getLeft();
		while (leftNode instanceof BiNode b) {
			leftNode = b.getRight(); // go to the rightmost children of the left subtree
		}

		return Optional.of((Value) leftNode);
	}

	static Optional<Value> closestRightNode(final Node node)
	{
		var curNode = node;
		var switchingParent = curNode.getParent();
		while (switchingParent instanceof BiNode b && b.getRight() == curNode) {
			curNode = switchingParent;
			switchingParent = curNode.getParent();
		}
		if (!(switchingParent instanceof BiNode parentNode)) {
			return Optional.empty();
		}

		var rightNode = parentNode.getRight();
		while (rightNode instanceof BiNode b) {
			rightNode = b.getLeft(); // go to the leftmost children of the right subtree
		}

		return Optional.of((Value) rightNode);
	}

	@AllArgsConstructor
	@Getter
	@Setter
	public static class Number
	{

		public static Number parse(final String value)
		{
			enum Direction
			{
				LEFT, RIGHT
			}

			Node root = null;
			BiNode current = null;
			Direction direction = Direction.LEFT;

			for (int i = 0, l = value.length(); i < l; i++) {
				final char c = value.charAt(i);
				switch (c) {
					case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
						final var v = new Value(current, c - '0');
						if (current != null) {
							switch (direction) {
								case LEFT -> current.setLeft(v);
								case RIGHT -> current.setRight(v);
							}
						}
						if (root == null) {
							root = v;
						}
					}
					case '[' -> {
						final var b = new BiNode(current);
						if (current != null) {
							switch (direction) {
								case LEFT -> current.setLeft(b);
								case RIGHT -> current.setRight(b);
							}
						}
						if (root == null) {
							root = b;
						}
						current = b;
						direction = Direction.LEFT;
					}
					case ',' -> direction = Direction.RIGHT;
					case ']' -> {
						if (current == null) {
							throw new IllegalStateException("No current node to close at index " + i);
						}
						if (current.getLeft() == null || current.getRight() == null) {
							throw new IllegalStateException("Closing an incomplete bi-node at index " + i);
						}
						current = switch (current.getParent()) {
							case null -> null;
							case BiNode b -> b;
							default -> throw new IllegalStateException("Closing a non bi-node at index " + i);
						};
					}
					default -> throw new IllegalArgumentException("Invalid character " + c + " in value at index " + i);
				}
			}

			if (root == null) {
				throw new IllegalArgumentException("Unable to parse number " + value);
			}
			return new Number(root);
		}

		private @NonNull Node root;

		public long magnitude()
		{
			return root.magnitude();
		}

		public Number add(final Number other)
		{
			final var left = Number.parse(this.toString());
			final var right = Number.parse(other.toString());

			depthLeftFirstVisitAll(left.getRoot(), //
					nodeWithStatus -> {
						if (nodeWithStatus.status() == NodeStatus.COMPLETE) {
							nodeWithStatus.node().setDepth(nodeWithStatus.node().getDepth() + 1);
						}
					});
			depthLeftFirstVisitAll(right.getRoot(), //
					nodeWithStatus -> {
						if (nodeWithStatus.status() == NodeStatus.COMPLETE) {
							nodeWithStatus.node().setDepth(nodeWithStatus.node().getDepth() + 1);
						}
					});

			final var newRoot = new BiNode(null);
			newRoot.setLeft(left.getRoot());
			left.getRoot().setParent(newRoot);
			newRoot.setRight(right.getRoot());
			right.getRoot().setParent(newRoot);

			final Number result = new Number(newRoot);
			result.reduce();

			return result;
		}

		public void reduce()
		{
			while (true) {
				// collect the first node from left to right that explodes
				final BiNode nodeToExplode = depthLeftFirstVisit(root, //
						nodeWithStatus -> {
							final Node node = nodeWithStatus.node();
							//noinspection SwitchStatementWithTooFewBranches
							return switch (node) {
								case BiNode b && b.getDepth() >= 5 -> b;
								default -> null;
							};
						});
				if (nodeToExplode != null) {
					explodeNode(nodeToExplode);
				} else {
					// no node explodes, collect the first node from left to right that splits
					final Value valueToSplit = depthLeftFirstVisit(root, //
							nodeWithStatus -> {
								final Node node = nodeWithStatus.node();
								//noinspection SwitchStatementWithTooFewBranches
								return switch (node) {
									case Value v && v.getVal() >= 10 -> v;
									default -> null;
								};
							});
					if (valueToSplit != null) {
						splitValue(valueToSplit);
					} else {
						// no operation to do, reduce terminates
						break;
					}
				}
			}
		}

		@Override
		public String toString()
		{
			final StringBuilder str = new StringBuilder();
			depthLeftFirstVisitAll(root, //
					nodeWithStatus -> {
						switch (nodeWithStatus.node()) {
							case Value v -> str.append(v.getVal());
							case BiNode ignore -> {
								switch (nodeWithStatus.status()) {
									case VISIT_LEFT -> str.append("[");
									case VISIT_RIGHT -> str.append(",");
									case COMPLETE -> str.append("]");
								}
							}
						}
					});
			return str.toString();
		}

		private void explodeNode(final BiNode node)
		{
			closestLeftNode(node) //
					.ifPresent(leftValue -> leftValue.setVal(leftValue.getVal() + ((Value) node.getLeft()).getVal()));
			closestRightNode(node) //
					.ifPresent(rightValue -> rightValue.setVal(rightValue.getVal() + ((Value) node.getRight()).getVal()));

			final Node parentNode = node.getParent();
			if (parentNode instanceof BiNode parent) {
				final Value zero = new Value(parent, 0);
				if (parent.getLeft() == node) {
					parent.setLeft(zero);
				} else {
					parent.setRight(zero);
				}
			}
		}

		private void splitValue(final Value value)
		{
			final Node parentNode = value.getParent();
			if (parentNode instanceof BiNode parent) {
				final int leftSplit = value.getVal() / 2;
				final int rightSplit = value.getVal() - leftSplit;
				final var node = new BiNode(parent);
				node.setLeft(new Value(node, leftSplit));
				node.setRight(new Value(node, rightSplit));
				if (parent.getLeft() == value) {
					parent.setLeft(node);
				} else {
					parent.setRight(node);
				}
			}
		}
	}

	@Getter
	@Setter
	public abstract static sealed class Node permits BiNode,Value
	{
		protected Node parent;

		protected long depth;

		protected Node(final Node parent) {
			this.parent = parent;
			this.depth = parent == null ? 1 : parent.depth + 1;
		}

		public abstract long magnitude();
	}

	@Getter
	@Setter
	public static final class Value extends Node
	{
		private int val;

		Value(final Node parent, final int val) {
			super(parent);
			this.val = val;
		}

		@Override
		public long magnitude()
		{
			return val;
		}
	}

	@Getter
	@Setter
	public static final class BiNode extends Node
	{
		private Node left;

		private Node right;

		BiNode(final Node parent) {
			super(parent);
		}

		@Override
		public long magnitude()
		{
			return 3 * left.magnitude() + 2 * right.magnitude();
		}
	}

	public enum NodeStatus
	{
		VISIT_LEFT, VISIT_RIGHT, COMPLETE
	}

	public record NodeWithStatus(Node node, NodeStatus status)
	{

		public static NodeWithStatus of(final Node node)
		{
			return new NodeWithStatus(node, node instanceof BiNode ? NodeStatus.VISIT_LEFT : NodeStatus.COMPLETE);
		}

	}
}
