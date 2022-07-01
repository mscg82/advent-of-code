package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jooq.lambda.Seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record DeseaseFight(List<Group> immuneSystem, List<Group> infection)
{

	public static DeseaseFight parseInput(final BufferedReader in) throws IOException
	{
		enum Status
		{
			NONE, IMMUNE, INFECTION
		}

		final List<String> immuneLines = new ArrayList<>();
		final List<String> infectionLines = new ArrayList<>();
		try {
			final var finalStatus = in.lines() //
					.filter(not(String::isEmpty)) //
					.reduce(Status.NONE, (status, line) -> {
						if ("Immune System:".equals(line)) {
							return Status.IMMUNE;
						} else if ("Infection:".equals(line)) {
							return Status.INFECTION;
						}

						final var listToEdit = switch (status) {
							case NONE -> throw new IllegalStateException();
							case IMMUNE -> immuneLines;
							case INFECTION -> infectionLines;
						};
						listToEdit.add(line);
						return status;
					}, (s1, s2) -> s1);

			if (finalStatus == Status.NONE) {
				throw new IllegalStateException("Nothing have been parsed");
			}
		} catch (final UncheckedIOException e) {
			throw e.getCause();
		}

		final List<Group> immuneSystem = Seq.seq(immuneLines.stream()) //
				.zipWithIndex() //
				.map(idx -> Group.from(idx.v2().intValue() + 1, GroupType.IMMUNE_SYSTEM, idx.v1())) //
				.toList();

		final List<Group> infection = Seq.seq(infectionLines.stream()) //
				.zipWithIndex() //
				.map(idx -> Group.from(idx.v2().intValue() + 1, GroupType.INFECTION, idx.v1())) //
				.toList();

		return new DeseaseFight(immuneSystem, infection);
	}

	public long computeWinningArmySize()
	{
		List<Group> currentImmuneSystem = immuneSystem;
		List<Group> currentInfection = infection;

		final var byEffectivePowerDescAndInitiativeDesc = Comparator.comparingLong(Group::getEffectivePower).reversed()
				.thenComparing(Comparator.comparingInt(Group::initiative).reversed());

		while (!currentImmuneSystem.isEmpty() && !currentInfection.isEmpty()) {
			final Map<GroupId, GroupId> immuneSystemTargeted = new HashMap<>();
			final Map<GroupId, GroupId> infectionTargeted = new HashMap<>();

			final List<Group> allGroups = Stream.concat(currentImmuneSystem.stream(), currentInfection.stream()) //
					.sorted(byEffectivePowerDescAndInitiativeDesc) //
					.toList();

			selectTargets(currentImmuneSystem, currentInfection, immuneSystemTargeted, infectionTargeted, allGroups);

			final Map<GroupId, GroupId> immuneSystemTarget = infectionTargeted.entrySet().stream() //
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
			final Map<GroupId, GroupId> infectionTarget = immuneSystemTargeted.entrySet().stream() //
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

			final Collection<Group> survivedGroups = executeAttacks(currentImmuneSystem, currentInfection, immuneSystemTarget,
					infectionTarget);

			final Map<Boolean, List<Group>> partitionedGroups = survivedGroups.stream() //
					.collect(Collectors.partitioningBy(g -> g.id().type() == GroupType.IMMUNE_SYSTEM));

			currentImmuneSystem = partitionedGroups.get(Boolean.TRUE);
			currentInfection = partitionedGroups.get(Boolean.FALSE);
		}

		return Stream.concat(currentImmuneSystem.stream(), currentInfection.stream()) //
				.mapToInt(Group::units) //
				.sum();
	}

	private static void selectTargets(final List<Group> currentImmuneSystem, final List<Group> currentInfection,
			final Map<GroupId, GroupId> immuneSystemTargeted, final Map<GroupId, GroupId> infectionTargeted,
			final List<Group> allGroups)
	{
		for (final var group : allGroups) {
			final List<Group> targets = switch (group.id().type()) {
				case IMMUNE_SYSTEM -> currentInfection;
				case INFECTION -> currentImmuneSystem;
			};

			final Map<GroupId, GroupId> targetedMap = switch (group.id().type()) {
				case IMMUNE_SYSTEM -> infectionTargeted;
				case INFECTION -> immuneSystemTargeted;
			};

			final var byReceivedAttackPowerAndEffectivePower = Comparator //
					.<Group>comparingLong(g -> g.computeReceivedDamageFrom(group)) //
					.thenComparingLong(Group::getEffectivePower) //
					.thenComparingInt(Group::initiative);

			final Optional<Group> choosenTarget = targets.stream() //
					.filter(g -> !targetedMap.containsKey(g.id())) //
					.max(byReceivedAttackPowerAndEffectivePower) //
					.filter(g -> g.computeReceivedDamageFrom(group) != 0);
			choosenTarget.ifPresent(target -> targetedMap.put(target.id(), group.id()));
		}
	}

	@SuppressWarnings("java:S135")
	private static Collection<Group> executeAttacks(final List<Group> currentImmuneSystem, final List<Group> currentInfection,
			final Map<GroupId, GroupId> immuneSystemTarget, final Map<GroupId, GroupId> infectionTarget)
	{
		final Map<GroupId, Group> idToGroup = Stream.concat(currentImmuneSystem.stream(), currentInfection.stream()) //
				.collect(Collectors.toMap(Group::id, g -> g));

		final List<GroupId> groupIdsByInitiative = Stream.concat(currentImmuneSystem.stream(), currentInfection.stream()) //
				.sorted(Comparator.comparingInt(Group::initiative).reversed()) //
				.map(Group::id) //
				.toList();
		for (final GroupId groupId : groupIdsByInitiative) {
			final var group = idToGroup.get(groupId);
			if (group == null) {
				continue;
			}

			final Map<GroupId, GroupId> targetsMap = switch (group.id().type()) {
				case IMMUNE_SYSTEM -> immuneSystemTarget;
				case INFECTION -> infectionTarget;
			};
			final var targetGroup = Optional.ofNullable(targetsMap.get(groupId)) //
					.map(idToGroup::get) //
					.orElse(null);
			if (targetGroup == null) {
				continue;
			}

			idToGroup.computeIfPresent(targetGroup.id(), (id, g) -> g.receiveAttack(group).orElse(null));
		}

		return idToGroup.values();
	}

	public record Attack(int value, String type) {}

	public record GroupId(int id, GroupType type) {}

	@RecordBuilder
	public record Group(GroupId id, int units, int hitPoints, Set<String> immunities, Set<String> weaknesses, Attack attack,
						int initiative) implements DeseaseFightGroupBuilder.With
	{

		public static Group from(final int id, final GroupType type, final String line)
		{
			final var pattern = Pattern.compile(
					"(\\d+) units each with (\\d+) hit points( \\((.+?)\\))? with an attack that does (\\d+) (.+?) damage at initiative (\\d+)");
			final var matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Unsupported group line " + line);
			}

			final int units = Integer.parseInt(matcher.group(1));
			final int hitPoints = Integer.parseInt(matcher.group(2));
			final String modifiersLine = matcher.group(4);
			final Set<String> immunities;
			final Set<String> weaknesses;
			if (modifiersLine != null) {
				final Set<String> tmpImmunities = new LinkedHashSet<>();
				final Set<String> tmpWeaknesses = new LinkedHashSet<>();
				Stream.of(modifiersLine.split(";")) //
						.map(String::trim) //
						.forEach(part -> {
							if (part.startsWith("immune to")) {
								Stream.of(part.substring("immune to".length()).split(",")) //
										.map(String::trim) //
										.forEach(tmpImmunities::add);
							} else if (part.startsWith("weak to")) {
								Stream.of(part.substring("weak to".length()).split(",")) //
										.map(String::trim) //
										.forEach(tmpWeaknesses::add);
							} else {
								throw new IllegalArgumentException("Unsupported group line " + line);
							}
						});
				immunities = Collections.unmodifiableSet(tmpImmunities);
				weaknesses = Collections.unmodifiableSet(tmpWeaknesses);
			} else {
				immunities = Set.of();
				weaknesses = Set.of();
			}
			final int attackValue = Integer.parseInt(matcher.group(5));
			final String attackType = matcher.group(6);
			final int initiative = Integer.parseInt(matcher.group(7));

			return new Group(new GroupId(id, type), units, hitPoints, immunities, weaknesses, new Attack(attackValue, attackType),
					initiative);
		}

		public long getEffectivePower()
		{
			return (long) units * attack.value();
		}

		public long computeReceivedDamageFrom(final Group group)
		{
			final Attack receivedAttack = group.attack();

			if (immunities.contains(receivedAttack.type())) {
				return 0;
			}

			return group.getEffectivePower() * (weaknesses.contains(receivedAttack.type()) ? 2 : 1);
		}

		public Optional<Group> receiveAttack(final Group group)
		{

			final long attackPower = computeReceivedDamageFrom(group);
			if (attackPower == 0L) {
				throw new IllegalStateException("Attacker choosed an immune target");
			}

			final int killedUnits = (int) (attackPower / hitPoints);
			if (killedUnits >= units) {
				return Optional.empty();
			}

			return Optional.of(this.withUnits(units - killedUnits));
		}

	}

	public enum GroupType
	{
		IMMUNE_SYSTEM, INFECTION
	}

}
