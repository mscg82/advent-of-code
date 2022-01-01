package com.mscg;

import com.mscg.IntcodeV6.InputGenerator;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ASCII(IntcodeV6 computer)
{

	public static final InputGenerator NO_INPUT = () -> {
		throw new UnsupportedOperationException("No input can be generated");
	};

	public static ASCII parseInput(final BufferedReader in) throws IOException
	{
		return new ASCII(IntcodeV6.parseInput(in));
	}

	public long calibrate()
	{
		final var map = readMap();

		return map.scaffolds().stream() //
				.filter(pos -> {
					final var presentNeighbors = new HashSet<>(pos.neighbours(map.rows(), map.cols()));
					presentNeighbors.retainAll(map.scaffolds());
					return presentNeighbors.size() == 4;
				}) //
				.mapToLong(pos -> (long) pos.x() * pos.y()) //
				.sum();
	}

	public long visit()
	{
		final var map = readMap();
		final String visitProgram = generateMainVisitProgram(map);
		final String[] instructions = visitProgram.split("(?<=\\d),");
		final var subprogramA = findLongestSubprogram(instructions);

		final var compiled = compileProgram(visitProgram, subprogramA);

		final String program = Stream.of(compiled.mainProgram(), //
						compiled.a().toString(), //
						compiled.b().toString(), //
						compiled.c().toString(), //
						"n") //
				.collect(Collectors.joining("\n", "", "\n"));

		final long[] inputs = program.chars() //
				.mapToLong(v -> v) //
				.toArray();

		final var execComputer = computer.withUpdatedData(data -> data[0] = 2);

		final var run = execComputer.execute(InputGenerator.forArray(inputs));
		final long[] outputs = run.outputs();
		return outputs[outputs.length - 1];
	}

	@SuppressWarnings({ "java:S106", "unused" })
	public void printMap()
	{
		final var map = readMap();
		final Map<Position, Direction> robots = map.robots().stream().collect(Collectors.toMap(Robot::position, Robot::direction));
		for (int y = 0; y <= map.rows(); y++) {
			for (int x = 0; x <= map.cols(); x++) {
				final var pos = new Position(x, y);
				if (map.scaffolds().contains(pos)) {
					System.out.print("#");
				} else if (robots.containsKey(pos)) {
					System.out.print(robots.get(pos));
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}

	private Subprogram findLongestSubprogram(final String[] instructions)
	{
		int offset = 0;
		while ("A".equals(instructions[offset]) || "B".equals(instructions[offset]) || "C".equals(instructions[offset])) {
			offset++;
		}
		var subProgram = new Subprogram(List.of());
		for (int i = offset; i < instructions.length; i++) {
			final String instruction = instructions[i];
			if ("A".equals(instruction) || "B".equals(instruction) || "C".equals(instruction)) {
				break;
			}
			final List<String> extendedComponents = Stream.concat(subProgram.components().stream(), Stream.of(instruction))
					.toList();
			final var next = subProgram.withComponents(extendedComponents);
			if (next.length() > 20) {
				break;
			}
			subProgram = next;
		}
		return subProgram;
	}

	private CompiledProgram compileProgram(final String program, final Subprogram a)
	{
		final String programWithA = program.replace(a.toString(), "A");
		String[] instructions = programWithA.split("(?<=[ABC\\d]),");
		final var b = findLongestSubprogram(instructions);

		final String programWithAB = programWithA.replace(b.toString(), "B");
		instructions = programWithAB.split("(?<=[ABC\\d]),");
		final var c = findLongestSubprogram(instructions);

		final String programWithABC = programWithAB.replace(c.toString(), "C");
		if (programWithABC.contains("L") || programWithABC.contains("R")) {
			// try with a shorter subprogram A
			final Subprogram shorterA = a.withComponents(a.components().subList(0, a.components().size() - 1));
			if (shorterA.components().isEmpty()) {
				throw new IllegalStateException("Can't compile program " + program);
			}
			return compileProgram(program, shorterA);
		}

		return new CompiledProgram(programWithABC, a, b, c);
	}

	private String generateMainVisitProgram(final ASCIIMap map)
	{
		Robot robot = map.robots().iterator().next();

		final StringBuilder program = new StringBuilder();
		do {
			final var optRotate = robot.rotate(map.scaffolds());
			if (optRotate.isEmpty()) {
				break;
			}
			final var rotate = optRotate.get();
			if (!program.isEmpty()) {
				program.append(",");
			}
			program.append(rotate.turn() == Direction.LEFT ? "L," : "R,");
			final var moved = rotate.robot().moveForward(map.scaffolds());
			program.append(moved.moves());
			robot = moved.robot();
		} while (true);

		return program.toString();
	}

	private ASCIIMap readMap()
	{
		final var run = computer.execute(NO_INPUT);

		int curX = 0;
		int curY = 0;
		final Set<Position> scaffoldPositions = new HashSet<>();
		final Set<Robot> robots = new HashSet<>();
		for (final long output : run.outputs()) {
			final char c = (char) output;
			switch (c) {
				case '\n' -> {
					curX = 0;
					curY++;
				}
				case '#' -> scaffoldPositions.add(new Position(curX++, curY));
				case '.' -> curX++;
				default -> {
					final var dir = Direction.from(c);
					robots.add(new Robot(new Position(curX++, curY), dir));
				}
			}
		}

		final int cols = Stream.concat(scaffoldPositions.stream(), robots.stream().map(Robot::position)) //
				.mapToInt(Position::x) //
				.max() //
				.orElseThrow();
		final int rows = Stream.concat(scaffoldPositions.stream(), robots.stream().map(Robot::position)) //
				.mapToInt(Position::y) //
				.max() //
				.orElseThrow();

		return new ASCIIMap(Set.copyOf(scaffoldPositions), Set.copyOf(robots), rows, cols);
	}

	public enum Direction
	{
		UP, RIGHT, DOWN, LEFT;

		public static Direction from(final char c)
		{
			return switch (c) {
				case '^' -> UP;
				case '>' -> RIGHT;
				case 'v' -> DOWN;
				case '<' -> LEFT;
				default -> throw new IllegalArgumentException("Unsupported direction character " + c);
			};
		}

		@Override
		public String toString()
		{
			return switch (this) {
				case UP -> "^";
				case RIGHT -> ">";
				case DOWN -> "v";
				case LEFT -> "<";
			};
		}
	}

	@RecordBuilder
	public record Position(int x, int y) implements ASCIIPositionBuilder.With
	{

		public Set<Position> neighbours(final int rows, final int cols)
		{
			return Stream.of( //
							new Position(x, y - 1), //
							new Position(x + 1, y), //
							new Position(x, y + 1), //
							new Position(x - 1, y)) //
					.filter(pos -> pos.x() >= 0 && pos.x() <= cols && pos.y() >= 0 && pos.y() <= rows) //
					.collect(Collectors.toUnmodifiableSet());
		}

	}

	@RecordBuilder
	public record Robot(Position position, Direction direction) implements ASCIIRobotBuilder.With
	{
		private static Position forwardPosition(final Position position, final Direction direction)
		{
			return position.with(pos -> {
				switch (direction) {
					case UP -> pos.y(pos.y() - 1);
					case RIGHT -> pos.x(pos.x() + 1);
					case DOWN -> pos.y(pos.y() + 1);
					case LEFT -> pos.x(pos.x() - 1);
				}
			});
		}

		public MovedRobot moveForward(final Set<Position> scaffolds)
		{
			int moves = 0;
			var position = this.position;
			while (true) {
				final var next = forwardPosition(position, direction);
				if (!scaffolds.contains(next)) {
					break;
				}
				moves++;
				position = next;
			}
			return new MovedRobot(this.withPosition(position), moves);
		}

		public Optional<TurnedRobot> rotate(final Set<Position> scaffolds)
		{
			final var left = turnLeft();
			if (scaffolds.contains(forwardPosition(position, left))) {
				return Optional.of(new TurnedRobot(this.withDirection(left), Direction.LEFT));
			}

			final var right = turnRight();
			if (scaffolds.contains(forwardPosition(position, right))) {
				return Optional.of(new TurnedRobot(this.withDirection(right), Direction.RIGHT));
			}

			return Optional.empty();
		}

		private Direction turnLeft()
		{
			return switch (direction) {
				case UP -> Direction.LEFT;
				case RIGHT -> Direction.UP;
				case DOWN -> Direction.RIGHT;
				case LEFT -> Direction.DOWN;
			};
		}

		private Direction turnRight()
		{
			return switch (direction) {
				case UP -> Direction.RIGHT;
				case RIGHT -> Direction.DOWN;
				case DOWN -> Direction.LEFT;
				case LEFT -> Direction.UP;
			};
		}
	}

	@RecordBuilder
	public record MovedRobot(Robot robot, int moves) implements ASCIIMovedRobotBuilder.With {}

	public record TurnedRobot(Robot robot, Direction turn) {}

	public record ASCIIMap(Set<Position> scaffolds, Set<Robot> robots, int rows, int cols) {}

	@RecordBuilder
	public record Subprogram(List<String> components) implements ASCIISubprogramBuilder.With
	{
		public int length()
		{
			return components.stream().mapToInt(String::length).sum() + components.size() - 1;
		}

		@Override
		public String toString()
		{
			return String.join(",", components);
		}
	}

	public record CompiledProgram(String mainProgram, Subprogram a, Subprogram b, Subprogram c) {}
}
