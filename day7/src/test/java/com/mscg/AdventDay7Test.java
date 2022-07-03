package com.mscg;

import com.mscg.LogicBoard.And;
import com.mscg.LogicBoard.Constant;
import com.mscg.LogicBoard.Instruction;
import com.mscg.LogicBoard.LShift;
import com.mscg.LogicBoard.Line;
import com.mscg.LogicBoard.Not;
import com.mscg.LogicBoard.Or;
import com.mscg.LogicBoard.RShift;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AdventDay7Test
{

	@Test
	public void testParse() throws Exception
	{
		final var board = LogicBoard.parseInput(readInput());

		Assertions.assertEquals(List.of( //
				new Instruction(new Constant(123), "x"), //
				new Instruction(new Constant(456), "y"), //
				new Instruction(new And(new Line("x"), new Line("y")), "d"), //
				new Instruction(new Or(new Line("x"), new Line("y")), "e"), //
				new Instruction(new LShift("x", 2), "f"), //
				new Instruction(new RShift("y", 2), "g"), //
				new Instruction(new Not("x"), "h"), //
				new Instruction(new Not("y"), "i"), //
				new Instruction(new And(new Constant(2), new Line("x")), "l"), //
				new Instruction(new Or(new Constant(3), new Line("y")), "m"), //
				new Instruction(new And(new Line("x"), new Constant(4)), "n"), //
				new Instruction(new Or(new Line("y"), new Constant(5)), "o"), //
				new Instruction(new And(new Line("d"), new Line("g")), "p"), //
				new Instruction(new Line("p"), "q") //
		), board.getInstructions());
	}

	@Test
	public void testExecute() throws Exception
	{
		final var board = LogicBoard.parseInput(readInput());
		final Map<String, Constant> portToValues = board.execute();

		Assertions.assertEquals(72, portToValues.get("d").value());
		Assertions.assertEquals(507, portToValues.get("e").value());
		Assertions.assertEquals(492, portToValues.get("f").value());
		Assertions.assertEquals(114, portToValues.get("g").value());
		Assertions.assertEquals(65412, portToValues.get("h").value());
		Assertions.assertEquals(65079, portToValues.get("i").value());
		Assertions.assertEquals(123, portToValues.get("x").value());
		Assertions.assertEquals(456, portToValues.get("y").value());
		Assertions.assertEquals(64, portToValues.get("p").value());
		Assertions.assertEquals(64, portToValues.get("q").value());
	}

	private BufferedReader readInput()
	{
		return new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
	}

}
