package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.mscg.SpellShop.Spell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Battle {

    private static final Fighter INITIAL_PLAYER_INFO = new Fighter(50, new Stats(0, 0, 500));

    private final Fighter boss;

    public Game findGameWithMinimalMana(int turns, boolean hardMode) {
        long minMana = Long.MAX_VALUE;
        Game result = null;
        for (var it = generateAllGames(turns).iterator(); it.hasNext();) {
            Game game = it.next();
            if (isGameInvalid(game, minMana, this.boss)) {
                continue;
            }

            var gameResult = playGame(game, hardMode);
            if (gameResult.result() == FightResult.PLAYER_WINS) {
                long totalMana = game.totalMana();
                minMana = totalMana;
                result = game;
            }
        }

        if (result == null) {
            throw new IllegalStateException("Unable to solve the battle");
        }

        return result;
    }

    public GameResult playGame(Game game, boolean hardMode) {
        return playGame(new ArrayList<>(game.spells()), hardMode);
    }

    public GameResult playGame(List<Spell> game, boolean hardMode) {
        Contestants contestants = new Contestants(INITIAL_PLAYER_INFO, this.boss);

        List<Spell> activeSpells = new ArrayList<>(game.size());

        for (int i = 0; !game.isEmpty() || !activeSpells.isEmpty(); i++) {
            if (hardMode) {
                if (i % 2 == 0) {
                    // player turn
                    contestants = contestants.onPlayer(p -> p.damage(1));

                    if (contestants.player().hitPoints() <= 0) {
                        return new GameResult(FightResult.BOSS_WINS, contestants);
                    }
                }
            }

            for (var it = activeSpells.listIterator(); it.hasNext();) {
                Spell spell = it.next().tick();
                it.set(spell);

                contestants = switch (spell.type()) {
                case POISON -> contestants.onBoss(b -> b.damage(spell.damage()));

                case RECHARGE -> contestants.onPlayer(p -> p.recharge(spell.recharge()));

                case MAGIC_MISSILE, DRAIN, SHIELD -> contestants;
                };

                if (spell.timer() <= 0) {
                    it.remove();
                    contestants = switch (spell.type()) {
                    case SHIELD -> contestants.onPlayer(p -> p.shield(-spell.armor()));

                    case MAGIC_MISSILE, DRAIN, POISON, RECHARGE -> contestants;
                    };
                }
            }

            if (contestants.player().hitPoints() <= 0 || contestants.player().stats().mana() <= 0) {
                return new GameResult(FightResult.BOSS_WINS, contestants);
            }
            if (contestants.boss().hitPoints() <= 0) {
                return new GameResult(FightResult.PLAYER_WINS, contestants);
            }

            if (i % 2 == 0) {
                // player turn
                if (game.isEmpty()) {
                    return new GameResult(FightResult.INVALID, contestants);
                }

                Spell spell = game.remove(0);

                if (contestants.player().stats().mana() < spell.cost()) {
                    return new GameResult(FightResult.BOSS_WINS, contestants);
                }

                if (activeSpells.stream().anyMatch(s -> s.type() == spell.type())) {
                    return new GameResult(FightResult.INVALID, contestants);
                }

                contestants = switch (spell.type()) {
                case MAGIC_MISSILE -> contestants.onBoss(b -> b.damage(spell.damage()));

                case DRAIN -> contestants.onBoth(p -> p.heal(spell.heal()), b -> b.damage(spell.damage()));

                case SHIELD -> {
                    activeSpells.add(spell);
                    yield contestants.onPlayer(p -> p.shield(spell.armor()));
                }

                case POISON, RECHARGE -> {
                    activeSpells.add(spell);
                    yield contestants;
                }
                };

                contestants = contestants.onPlayer(p -> p.discharge(spell.cost()));
            } else {
                // boss turn
                int damage = contestants.boss().stats().damage() - contestants.player().stats().armor();
                contestants = contestants.onPlayer(p -> p.damage(Math.max(1, damage)));
            }

            if (contestants.player().hitPoints() <= 0 || contestants.player().stats().mana() <= 0) {
                return new GameResult(FightResult.BOSS_WINS, contestants);
            }
            if (contestants.boss().hitPoints() <= 0) {
                return new GameResult(FightResult.PLAYER_WINS, contestants);
            }
        }

        return new GameResult(FightResult.INVALID, contestants);
    }

    private static boolean isGameInvalid(Game game, long minMana, Fighter boss) {
        if (game.totalMana() >= minMana) {
            return true;
        }

        int totalDamage = game.spells().stream() //
                .mapToInt(spell -> spell.damage() * Math.max(1, spell.timer())) //
                .sum();
        if (totalDamage < boss.hitPoints()) {
            return true;
        }

        return false;
    }

    public static Stream<Game> generateAllGames(int turns) {
        return IntStream.rangeClosed(1, turns) //
                .mapToObj(Battle::generateAllGamesWithLength) //
                .flatMap(s -> s);
    }

    private static Stream<Game> generateAllGamesWithLength(int turns) {
        int spellsNumber = SpellShop.getSpells().size();
        return LongStream.range(0L, pow(spellsNumber, turns)) //
                .mapToObj(l -> Long.toString(l, spellsNumber)) //
                .map(s -> {
                    while (s.length() < turns) {
                        s = "0" + s;
                    }
                    return s;
                }) //
                .map(s -> {
                    int length = s.length();
                    return IntStream.range(0, length) //
                            .map(i -> s.charAt(i) - '0') //
                            .mapToObj(SpellShop.getSpells()::get) //
                            .collect(Collectors.toUnmodifiableList());
                }) //
                .map(Game::new);
    }

    private static long pow(long base, int pow) {
        long res = 1L;
        for (int i = 0; i < pow; i++) {
            res *= base;
        }
        return res;
    }

    public static Battle parseInput(BufferedReader in) throws IOException {
        var boss = new Fighter(getNumber(in.readLine()), //
                new Stats(getNumber(in.readLine()), 0, 0));
        return new Battle(boss);
    }

    private static int getNumber(String line) {
        int index = line.indexOf(':');
        return Integer.parseInt(line.substring(index + 1).trim());
    }

    public static record Stats(int damage, int armor, int mana) {
        public Stats shield(int delta) {
            return new Stats(damage, armor + delta, mana);
        }

        public Stats recharge(int delta) {
            return new Stats(damage, armor, mana + delta);
        }
    }

    public static record Fighter(int hitPoints, Stats stats) {
        public Fighter damage(int damage) {
            return new Fighter(hitPoints - damage, stats);
        }

        public Fighter heal(int hitPoints) {
            return new Fighter(this.hitPoints + hitPoints, stats);
        }

        public Fighter shield(int armor) {
            return new Fighter(hitPoints, stats.shield(armor));
        }

        public Fighter recharge(int mana) {
            return new Fighter(hitPoints, stats.recharge(mana));
        }

        public Fighter discharge(int mana) {
            return new Fighter(hitPoints, stats.recharge(-mana));
        }
    }

    public static record Game(List<Spell> spells) {
        public long totalMana() {
            return spells.stream() //
                    .mapToLong(Spell::cost) //
                    .sum();
        }
    }

    public static record Contestants(Fighter player, Fighter boss) {

        public Contestants onPlayer(UnaryOperator<Fighter> op) {
            return new Contestants(op.apply(player), boss);
        }

        public Contestants onBoss(UnaryOperator<Fighter> op) {
            return new Contestants(player, op.apply(boss));
        }

        public Contestants onBoth(UnaryOperator<Fighter> playerOp, UnaryOperator<Fighter> bossOp) {
            return new Contestants(playerOp.apply(player), bossOp.apply(boss));
        }

    }

    public static record GameResult(FightResult result, Contestants contestants) {
    }

    public enum FightResult {
        PLAYER_WINS, BOSS_WINS, INVALID;
    }

}
