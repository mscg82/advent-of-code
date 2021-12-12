package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.soabase.recordbuilder.core.RecordBuilder;

public record HullPainterRobot(IntcodeV5 computer)
{

	public static HullPainterRobot parseInput(final BufferedReader in) throws IOException
	{
		return new HullPainterRobot(IntcodeV5.parseInput(in));
	}

	public int runAndPaint()
	{
		final Map<Position, Long> hull = new HashMap<>();
		paint(hull);

		return hull.size();
	}

	public String paintedCode()
	{
		final Map<Position, Long> hull = new HashMap<>();
		hull.put(new Position(0, 0), 1L);
		paint(hull);

		final LongSummaryStatistics xStats = hull.keySet().stream() //
				.mapToLong(Position::x) //
				.summaryStatistics();
		final LongSummaryStatistics yStats = hull.keySet().stream() //
				.mapToLong(Position::y) //
				.summaryStatistics();

		return LongStream.rangeClosed(yStats.getMin(), yStats.getMax()) //
				.mapToObj(y -> LongStream.rangeClosed(xStats.getMin(), xStats.getMax()) //
						.mapToObj(x -> hull.getOrDefault(new Position(x, y), 0L) == 1L ? "#" : " ") //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	private long paint(final Map<Position, Long> hull)
	{
		record Status(IntcodeV5 computer, Robot robot)
		{

		}

		return Stream.iterate( //
				new Status(computer, new Robot(new Position(0, 0), Direction.UP)), //
				status -> !status.computer().halted(), //
				status -> {
					var computer = status.computer();
					var robot = status.robot();
					final var curColor = hull.getOrDefault(robot.position(), 0L);
					computer = computer.execute(List.of(curColor).iterator(), 2);
					if (!computer.halted()) {
						hull.put(robot.position(), computer.outputs()[0]);
						robot = robot.rotateAndMove(computer.outputs()[1] == 0L);
					}
					return new Status(computer, robot);
				}) //
				.count();
	}

	public enum Direction
	{
		UP, RIGTH, DOWN, LEFT;

		public Direction rotate(final boolean left)
		{
			return switch (this) {
				case UP -> left ? LEFT : RIGTH;
				case RIGTH -> left ? UP : DOWN;
				case DOWN -> left ? RIGTH : LEFT;
				case LEFT -> left ? DOWN : UP;
			};
		}
	}

	@RecordBuilder
	public record Position(long x, long y) implements HullPainterRobotPositionBuilder.With
	{
		public Position move(final Direction direction)
		{
			return this.with(p -> {
				switch (direction) {
					case UP -> p.y(p.y() - 1);
					case RIGTH -> p.x(p.x() + 1);
					case DOWN -> p.y(p.y() + 1);
					case LEFT -> p.x(p.x() - 1);
				}
			});
		}
	}

	@RecordBuilder
	public record Robot(Position position, Direction direction) implements HullPainterRobotRobotBuilder.With
	{

		public Robot rotateAndMove(final boolean left)
		{
			return this.with(r -> {
				final Direction newDirection = r.direction().rotate(left);
				r.direction(newDirection);
				r.position(r.position().move(newDirection));
			});
		}

	}
}
