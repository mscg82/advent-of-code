package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.mscg.CombatMapPositionBuilder.Position;

@RecordBuilder
public record CombatMap(List<MapNode> mapNodes, List<Player> elfs, List<Player> goblins, int maxX, int maxY, int step,
						int fullRounds) implements CombatMapBuilder.With
{

	public static CombatMap parseInput(final BufferedReader in) throws IOException
	{
		final List<String> lines;
		try {
			lines = in.lines().toList();
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final var mapNodes = new ArrayList<MapNode>();
		final var elfPositions = new ArrayList<Position>();
		final var goblinPositions = new ArrayList<Position>();

		for (int y = 1, l = lines.size(); y < l - 1; y++) {
			final var line = lines.get(y);
			for (int x = 1, l2 = line.length(); x < l2 - 1; x++) {
				final var c = Cell.from(line.charAt(x));
				if (c.isWalkable()) {
					final var nodePosition = new Position(x, y);
					final var neighbours = nodePosition.neighbours() //
							.filter(pos -> Cell.from(lines.get(pos.y()).charAt(pos.x())).isWalkable()) //
							.toList();
					mapNodes.add(new MapNode(nodePosition, neighbours));

					if (c == Cell.ELF) {
						elfPositions.add(nodePosition);
					}
					if (c == Cell.GOBLIN) {
						goblinPositions.add(nodePosition);
					}
				}
			}
		}

		return new CombatMap(List.copyOf(mapNodes), //
				elfPositions.stream() //
						.map(pos -> new Player(pos, 3, 200)) //
						.toList(), //
				goblinPositions.stream() //
						.map(pos -> new Player(pos, 3, 200)) //
						.toList(), //
				lines.get(0).length(), //
				lines.size(), //
				0, 0);
	}

	public long computeScore()
	{
		final CombatMap lastMap = evolveUntilEnd().findLast().orElseThrow();

		return computeOutcomeScore(lastMap);
	}

	public long helpElfsAndComputeScore()
	{
		final int numberOfElfs = elfs.size();

		final CombatMap victoriousMap = IntStream.range(4, 100) //
				.mapToObj(attack -> {
					final List<Player> buffedElfs = elfs.stream() //
							.map(player -> player.withAttackPower(attack)) //
							.toList();
					return this.withElfs(buffedElfs);
				}) //
				.flatMap(combatMap -> {
					final CombatMap lastMap = combatMap.evolveUntilEnd().findLast().orElseThrow();
					return lastMap.elfs.size() == numberOfElfs ? Stream.of(lastMap) : Stream.empty();
				}) //
				.findFirst() //
				.orElseThrow();

		return computeOutcomeScore(victoriousMap);
	}

	public CombatMap evolve()
	{
		final var actionTurn = Stream.concat( //
						elfs.stream().map(plr -> new TypedPosition(Cell.ELF, plr)), //
						goblins.stream().map(plr -> new TypedPosition(Cell.GOBLIN, plr))) //
				.sorted(Comparator.comparing(t -> t.player().position())) //
				.toList();

		final Map<Position, Player> newElfPositions = elfs.stream() //
				.collect(Collectors.toMap(Player::position, p -> p));
		final Map<Position, Player> newGoblinPositions = goblins.stream() //
				.collect(Collectors.toMap(Player::position, p -> p));
		final Set<Position> mapPositions = mapNodes.stream() //
				.map(MapNode::position) //
				.collect(Collectors.toUnmodifiableSet());

		boolean fullRound = true;
		final Set<Player> killedPlayers = new HashSet<>();
		for (final var action : actionTurn) {
			if (killedPlayers.contains(action.player())) {
				continue;
			}

			if (newElfPositions.isEmpty() || newGoblinPositions.isEmpty()) {
				fullRound = false;
				break;
			}

			// try to move to reach the nearest enemy
			final Optional<TypedPosition> movedAction = executeMove(newElfPositions, newGoblinPositions, mapPositions, action);

			// try to attack the weakest near enemy
			final Optional<Player> killedTarget = executeAttack(newElfPositions, newGoblinPositions, movedAction.orElse(action));
			killedTarget.ifPresent(killedPlayers::add);
		}

		return new CombatMap(mapNodes, List.copyOf(newElfPositions.values()), List.copyOf(newGoblinPositions.values()), maxX, maxY,
				step + 1, fullRounds + (fullRound ? 1 : 0));
	}

	@Override
	public String toString()
	{
		final var walkablePositions = mapNodes.stream() //
				.map(MapNode::position) //
				.collect(Collectors.toUnmodifiableSet());
		final var elfPositions = this.elfs.stream() //
				.map(Player::position) //
				.collect(Collectors.toUnmodifiableSet());
		final var goblinPositions = this.goblins.stream() //
				.map(Player::position) //
				.collect(Collectors.toUnmodifiableSet());

		return IntStream.range(0, maxY) //
				.mapToObj(y -> IntStream.range(0, maxX) //
						.mapToObj(x -> CombatMapPositionBuilder.Position(x, y)) //
						.map(pos -> {
							if (!walkablePositions.contains(pos)) {
								return "#";
							} else if (elfPositions.contains(pos)) {
								return "E";
							} else if (goblinPositions.contains(pos)) {
								return "G";
							} else {
								return ".";
							}
						}) //
						.collect(Collectors.joining())) //
				.collect(Collectors.joining("\n"));
	}

	Seq<CombatMap> evolveUntilEnd()
	{
		return Seq.iterate(this, CombatMap::evolve) //
				.limitUntilClosed(combatMap -> combatMap.elfs().isEmpty() || combatMap.goblins().isEmpty());
	}

	private long computeOutcomeScore(final CombatMap lastMap)
	{
		final long remainingHitPoints = Stream.concat(lastMap.elfs.stream(), lastMap.goblins.stream()) //
				.mapToLong(Player::hitPoints) //
				.sum();
		return lastMap.fullRounds() * remainingHitPoints;
	}

	private Optional<TypedPosition> executeMove(final Map<Position, Player> newElfPositions,
			final Map<Position, Player> newGoblinPositions, final Set<Position> mapPositions, final TypedPosition action)
	{
		final Optional<Position> movementTarget = action.moveToNearestEnemy(mapPositions, newElfPositions.keySet(),
				newGoblinPositions.keySet());
		if (movementTarget.isPresent()) {
			final Position newPosition = movementTarget.get();
			final Map<Position, Player> mapToUpdate = switch (action.cell()) {
				case ELF -> newElfPositions;
				case GOBLIN -> newGoblinPositions;
				default -> throw new IllegalStateException("Can't update cell of type " + action.cell());
			};
			final var oldPlayer = mapToUpdate.remove(action.player().position());
			final var newPlayer = oldPlayer.withPosition(newPosition);
			mapToUpdate.put(newPlayer.position(), newPlayer);
			return Optional.ofNullable(action.withPlayer(newPlayer));
		}
		return Optional.empty();
	}

	private Optional<Player> executeAttack(final Map<Position, Player> newElfPositions,
			final Map<Position, Player> newGoblinPositions, final TypedPosition action)
	{
		final Map<Position, Player> enemiesPositions = switch (action.cell()) {
			case ELF -> newGoblinPositions;
			case GOBLIN -> newElfPositions;
			default -> throw new IllegalStateException("Can't get enemies of cell of type " + action.cell());
		};

		final List<Position> attackablePositions = action.attackablePositions(newElfPositions.keySet(),
				newGoblinPositions.keySet());
		final Optional<Player> attackTarget = attackablePositions.stream() //
				.flatMap(pos -> {
					final Player player = enemiesPositions.get(pos);
					return player == null ? Stream.empty() : Stream.of(player);
				}) //
				.min(Comparator.comparingInt(Player::hitPoints).thenComparing(Player::position));

		return attackTarget.map(target -> {
			final var hitTarget = target.withHitPoints(target.hitPoints() - action.player().attackPower());
			if (hitTarget.hitPoints() <= 0) {
				return enemiesPositions.remove(hitTarget.position());
			} else {
				enemiesPositions.replace(hitTarget.position(), hitTarget);
				return null;
			}
		});
	}

	@RecordBuilder
	public record Position(int x, int y) implements CombatMapPositionBuilder.With, Comparable<Position>
	{
		private static final Comparator<Position> COMPARATOR = Comparator //
				.comparingInt(Position::y) //
				.thenComparingInt(Position::x);

		public Stream<Position> neighbours()
		{
			return Stream.of( //
					this.withY(y - 1), //
					this.withX(x + 1), //
					this.withY(y + 1), //
					this.withX(x - 1) //
			);
		}

		@Override
		public int compareTo(@NonNull final Position other)
		{
			return COMPARATOR.compare(this, other);
		}

		@Override
		public String toString()
		{
			return "(" + x + ", " + y + ")";
		}
	}

	public record MapNode(Position position, List<Position> neighbours) {}

	@RecordBuilder
	public record Player(Position position, int attackPower, int hitPoints) implements CombatMapPlayerBuilder.With
	{
		@Override
		public String toString()
		{
			return "[" + position + "," + hitPoints + "]";
		}
	}

	@RecordBuilder
	record TypedPosition(Cell cell, Player player) implements CombatMapTypedPositionBuilder.With
	{

		public List<Position> attackablePositions(final Set<Position> elfPositions, final Set<Position> goblinPositions)
		{
			return player.position().neighbours() //
					.filter(pos -> switch (cell) {
						case ELF -> goblinPositions.contains(pos);
						case GOBLIN -> elfPositions.contains(pos);
						case WALL, EMPTY -> false;
					}) //
					.toList();
		}

		public Optional<Position> moveToNearestEnemy(final Set<Position> mapPositions, final Set<Position> elfPositions,
				final Set<Position> goblinPositions)
		{
			final Set<Position> enemyPositions = switch (cell) {
				case ELF -> goblinPositions;
				case GOBLIN -> elfPositions;
				case WALL, EMPTY -> Set.of();
			};
			final Set<Position> friendPositions = switch (cell) {
				case ELF -> elfPositions;
				case GOBLIN -> goblinPositions;
				case WALL, EMPTY -> Set.of();
			};
			if (enemyPositions.isEmpty()) {
				return Optional.empty();
			}

			record VisitNode(Position position, VisitNode parent) {}

			final Deque<VisitNode> queue = new ArrayDeque<>();
			queue.add(new VisitNode(player.position(), null));

			final Set<Position> visitedNodes = new HashSet<>();
			visitedNodes.add(player.position());

			Optional<Position> movePosition = Optional.empty();
			while (!queue.isEmpty()) {
				final var curNode = queue.pop();
				final List<Position> targets = curNode.position().neighbours() //
						.filter(mapPositions::contains) //
						.filter(pos -> !visitedNodes.contains(pos) && !friendPositions.contains(pos)) //
						.sorted() //
						.toList();
				final Optional<Position> reachableEnemy = targets.stream() //
						.filter(enemyPositions::contains) //
						.findFirst();
				if (reachableEnemy.isPresent()) {
					// unwind the path and return position for the move
					if (curNode.parent == null) {
						// we are already close to an enemy
						movePosition = Optional.empty();
					} else {
						movePosition = Stream.iterate(curNode, VisitNode::parent) //
								.filter(node -> node.parent().parent() == null) //
								.findFirst() //
								.map(VisitNode::position);
					}
					break;
				}
				targets.stream() //
						.map(pos -> new VisitNode(pos, curNode)) //
						.forEach(e -> {
							queue.add(e);
							visitedNodes.add(e.position());
						});
			}

			return movePosition;
		}

	}

	@RequiredArgsConstructor
	@Getter
	public enum Cell
	{
		WALL(false), EMPTY(true), ELF(true), GOBLIN(true);

		private final boolean walkable;

		public static Cell from(final char c)
		{
			return switch (c) {
				case '#' -> WALL;
				case '.' -> EMPTY;
				case 'E' -> ELF;
				case 'G' -> GOBLIN;
				default -> throw new IllegalArgumentException("Unsupported input char " + c);
			};
		}

	}
}
