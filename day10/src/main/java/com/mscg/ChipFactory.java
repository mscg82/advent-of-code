package com.mscg;

import static com.mscg.ChipFactoryBotBuilder.Bot;
import static com.mscg.ChipFactoryMoveRuleBuilder.MoveRule;
import static com.mscg.ChipFactorySetRuleBuilder.SetRule;
import static com.mscg.ChipFactoryTargetBuilder.Target;

import java.io.BufferedReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChipFactory {

    private final List<? extends Rule> rules;
    private final Map<Integer, Bot> bots = new HashMap<>();
    private final Map<Integer, Integer> outputs = new HashMap<>();

    public int execute(Predicate<Bot> botFilter) {
        List<SetRule> setRules = new ArrayList<>();
        Map<Integer, MoveRule> botToMoveRule = new HashMap<>();
        for (Rule rule : rules) {
            if (rule instanceof SetRule setRule) {
                setRules.add(setRule);
            } else if (rule instanceof MoveRule moveRule) {
                botToMoveRule.put(moveRule.botNumber(), moveRule);
            } else {
                throw new IllegalArgumentException("Unsupported rule " + rule.getClass());
            }
        }

        setRules.forEach(rule -> rule.execute(bots, outputs));

        boolean updated = true;
        while (updated) {
            updated = false;
            Optional<Map.Entry<Integer, Bot>> fullBotIndex = bots.entrySet().stream() //
                    .filter(entry -> entry.getValue().isFull()) //
                    .findFirst();
            if (fullBotIndex.isPresent()) {
                Map.Entry<Integer, Bot> entry = fullBotIndex.get();
                MoveRule moveRule = botToMoveRule.get(entry.getKey());
                if (moveRule != null) {
                    updated = true;
                    if (!botFilter.test(entry.getValue())) {
                        return entry.getKey();
                    }
                    moveRule.execute(bots, outputs);
                }
            }
        }

        return -1;
    }

    public static ChipFactory parseInput(BufferedReader in) throws UncheckedIOException {
        var setRulePattern = Pattern.compile("value (\\d+) goes to bot (\\d+)");
        var moveRulePattern = Pattern.compile("bot (\\d+) gives low to (bot|output) (\\d+) and high to (bot|output) (\\d+)");

        List<? extends Rule> rules = in.lines() //
                .map(line -> {
                    var setRuleMatcher = setRulePattern.matcher(line);
                    if (setRuleMatcher.matches()) {
                        return SetRule(Integer.parseInt(setRuleMatcher.group(2)), Integer.parseInt(setRuleMatcher.group(1)));
                    } else {
                        var moveRuleMatcher = moveRulePattern.matcher(line);
                        if (moveRuleMatcher.matches()) {
                            var lowTarget = Target(TargetType.fromString(moveRuleMatcher.group(2)), Integer.parseInt(moveRuleMatcher.group(3)));
                            var highTarget = Target(TargetType.fromString(moveRuleMatcher.group(4)), Integer.parseInt(moveRuleMatcher.group(5)));
                            return MoveRule(Integer.parseInt(moveRuleMatcher.group(1)), lowTarget, highTarget);
                        }
                    }
                    throw new IllegalArgumentException("Unsupported rule \"" + line + "\"");
                }) //
                .toList();

        return new ChipFactory(rules);
    }

    @RecordBuilder
    public static record Bot(Integer value1, Integer value2) implements ChipFactoryBotBuilder.With {

        public boolean isFull() {
            return value1 != null && value2 != null;
        }

        public int low() {
            if (!isFull()) {
                throw new IllegalStateException("Low can be computed only on full bots");
            }

            if (value1.compareTo(value2) <= 0) {
                return value1;
            } else {
                return value2;
            }
        }

        public int high() {
            if (!isFull()) {
                throw new IllegalStateException("High can be computed only on full bots");
            }

            if (value1.compareTo(value2) > 0) {
                return value1;
            } else {
                return value2;
            }
        }

        public static Bot newEmpty() {
            return Bot(null, null);
        }

        public Bot setValue(int value) {
            if (value1 == null) {
                return this.withValue1(value);
            } else if (value2 == null) {
                return this.withValue2(value);
            } else {
                throw new IllegalStateException("Bot has both values already set");
            }
        }

    }

    public interface Rule {
        void execute(Map<Integer, Bot> bots, Map<Integer, Integer> outputs);
    }

    @RecordBuilder
    public static record SetRule(int botNumber, int value) implements Rule {

        @Override
        public void execute(Map<Integer, Bot> bots, Map<Integer, Integer> outputs) {
            bots.compute(botNumber, (__, bot) -> (bot == null ? Bot.newEmpty() : bot).setValue(value));
        }

    }

    @RecordBuilder
    public static record MoveRule(int botNumber, @NonNull Target lowTarget, @NonNull Target highTarget) implements Rule {

        @Override
        public void execute(Map<Integer, Bot> bots, Map<Integer, Integer> outputs) {
            var bot = bots.computeIfAbsent(botNumber, __ -> Bot.newEmpty());

            switch (lowTarget.type()) {
                case BOT -> bots.compute(lowTarget.index(), //
                        (__, targetBot) -> (targetBot == null ? Bot.newEmpty() : targetBot).setValue(bot.low()));
                case OUTPUT -> outputs.put(lowTarget.index(), bot.low());
            }

            switch (highTarget.type()) {
                case BOT -> bots.compute(highTarget.index(), //
                        (__, targetBot) -> (targetBot == null ? Bot.newEmpty() : targetBot).setValue(bot.high()));
                case OUTPUT -> outputs.put(highTarget.index(), bot.high());
            }

            bots.put(botNumber, Bot.newEmpty());
        }

    }

    public enum TargetType {
        BOT, OUTPUT;

        public static TargetType fromString(String value) {
            return switch (value.toLowerCase()) {
                case "bot" -> TargetType.BOT;
                case "output" -> TargetType.OUTPUT;
                default -> throw new IllegalArgumentException("Unsupported target type " + value);
            };
        }
    }

    @RecordBuilder
    public static record Target(@NonNull TargetType type, int index) {

    }

}
