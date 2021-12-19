package com.mscg;

import static com.mscg.SnailfishAlgebra.Number;

import java.util.List;
import java.util.Optional;

import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mscg.SnailfishAlgebra.BiNode;
import com.mscg.SnailfishAlgebra.Value;

class AdventDay18Test
{

	@Test
	void testParseAndToString()
	{

		Number n;

		n = Number.parse("5");
		Assertions.assertEquals("5", n.toString());

		n = Number.parse("[1,2]");
		Assertions.assertEquals("[1,2]", n.toString());

		n = Number.parse("[[1,2],3]");
		Assertions.assertEquals("[[1,2],3]", n.toString());

		n = Number.parse("[9,[8,7]]");
		Assertions.assertEquals("[9,[8,7]]", n.toString());

		n = Number.parse("[[1,9],[8,5]]");
		Assertions.assertEquals("[[1,9],[8,5]]", n.toString());

		n = Number.parse("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]");
		Assertions.assertEquals("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]", n.toString());

		n = Number.parse("[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]");
		Assertions.assertEquals("[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]", n.toString());

		n = Number.parse("[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]");
		Assertions.assertEquals("[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]", n.toString());

	}

	@Test
	void testClosestNodes1()
	{
		final var n = Number.parse("[[6,[5,[4,[3,2]]]],1]");
		var tmp = ((BiNode) n.getRoot()).getLeft();
		tmp = ((BiNode) tmp).getRight();
		tmp = ((BiNode) tmp).getRight();
		tmp = ((BiNode) tmp).getRight();
		final var target = (BiNode) tmp;
		Assertions.assertEquals(5L, target.getDepth());
		Assertions.assertEquals(3, ((Value) target.getLeft()).getVal());
		Assertions.assertEquals(2, ((Value) target.getRight()).getVal());

		final Optional<Value> leftNode = SnailfishAlgebra.closestLeftNode(target);
		Assertions.assertTrue(leftNode.isPresent());
		Assertions.assertEquals(4, leftNode.get().getVal());

		final Optional<Value> rightNode = SnailfishAlgebra.closestRightNode(target);
		Assertions.assertTrue(rightNode.isPresent());
		Assertions.assertEquals(1, rightNode.get().getVal());
	}

	@Test
	void testClosestNodes2()
	{
		final var n = Number.parse("[[[[[9,8],1],2],3],4]");
		var tmp = ((BiNode) n.getRoot()).getLeft();
		tmp = ((BiNode) tmp).getLeft();
		tmp = ((BiNode) tmp).getLeft();
		tmp = ((BiNode) tmp).getLeft();
		final var target = (BiNode) tmp;
		Assertions.assertEquals(5L, target.getDepth());
		Assertions.assertEquals(9, ((Value) target.getLeft()).getVal());
		Assertions.assertEquals(8, ((Value) target.getRight()).getVal());

		final Optional<Value> optLeftNode = SnailfishAlgebra.closestLeftNode(target);
		Assertions.assertFalse(optLeftNode.isPresent());

		final Optional<Value> rightNode = SnailfishAlgebra.closestRightNode(target);
		Assertions.assertTrue(rightNode.isPresent());
		Assertions.assertEquals(1, rightNode.get().getVal());
	}

	@Test
	void testClosestNodes3()
	{
		final var n = Number.parse("[7,[6,[5,[4,[3,2]]]]]");
		var tmp = ((BiNode) n.getRoot()).getRight();
		tmp = ((BiNode) tmp).getRight();
		tmp = ((BiNode) tmp).getRight();
		tmp = ((BiNode) tmp).getRight();
		final var target = (BiNode) tmp;
		Assertions.assertEquals(5L, target.getDepth());
		Assertions.assertEquals(3, ((Value) target.getLeft()).getVal());
		Assertions.assertEquals(2, ((Value) target.getRight()).getVal());

		final Optional<Value> leftNode = SnailfishAlgebra.closestLeftNode(target);
		Assertions.assertTrue(leftNode.isPresent());
		Assertions.assertEquals(4, leftNode.get().getVal());

		final Optional<Value> optRightNode = SnailfishAlgebra.closestRightNode(target);
		Assertions.assertFalse(optRightNode.isPresent());
	}

	@Test
	void testReduce()
	{
		final var n = Number.parse("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]");
		n.reduce();
		Assertions.assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", n.toString());
	}

	@Test
	void testAdd1()
	{
		final var n1 = Number.parse("[[[[4,3],4],4],[7,[[8,4],9]]]");
		final var n2 = Number.parse("[1,1]");
		final var sum = n1.add(n2);

		Assertions.assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", sum.toString());
	}

	@Test
	void testAdd2()
	{
		final var input = """
				[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
				[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
				[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
				[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
				[7,[5,[[3,8],[1,4]]]]
				[[2,[2,2]],[8,[8,1]]]
				[2,9]
				[1,[[[9,3],9],[[9,0],[0,7]]]]
				[[[5,[7,4]],7],1]
				[[[[4,2],2],6],[8,7]]""";
		final List<Number> numbers = input.lines() //
				.map(Number::parse) //
				.toList();

		var sum = numbers.get(0).add(numbers.get(1));
		Assertions.assertEquals("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", sum.toString());

		sum = sum.add(numbers.get(2));
		Assertions.assertEquals("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]", sum.toString());

		sum = sum.add(numbers.get(3));
		Assertions.assertEquals("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]", sum.toString());

		sum = sum.add(numbers.get(4));
		Assertions.assertEquals("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]", sum.toString());

		sum = sum.add(numbers.get(5));
		Assertions.assertEquals("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]", sum.toString());

		sum = sum.add(numbers.get(6));
		Assertions.assertEquals("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]", sum.toString());

		sum = sum.add(numbers.get(7));
		Assertions.assertEquals("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]", sum.toString());

		sum = sum.add(numbers.get(8));
		Assertions.assertEquals("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]", sum.toString());

		sum = sum.add(numbers.get(9));
		Assertions.assertEquals("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", sum.toString());

		final Number total = Seq.seq(input.lines() //
				.map(Number::parse)) //
				.foldLeft(null, (s, n) -> s == null ? n : s.add(n));
		Assertions.assertEquals("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", total.toString());
	}

	@Test
	void testMagnitude()
	{
		Assertions.assertEquals(143, Number.parse("[[1,2],[[3,4],5]]").magnitude());
		Assertions.assertEquals(3488, Number.parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude());
	}

}
