package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public List<Spell> findGameWithMinimalMana(int turns, boolean hardMode) {
        int minMana = Integer.MAX_VALUE;
        List<Spell> result = null;
        for (var it = generateAllGames(turns).iterator(); it.hasNext();) {
            List<Spell> game = it.next();
            if (!isValidGame(game, minMana, this.boss, INITIAL_PLAYER_INFO.stats().mana())) {
                continue;
            }

            var gameResult = playGame(new ArrayList<>(game), hardMode);
            if (gameResult.result() == FightResult.PLAYER_WINS) {
                int totalMana = game.stream() //
                        .mapToInt(Spell::cost) //
                        .sum();
                if (totalMana < minMana) {
                    minMana = totalMana;
                    result = game;
                }
            }
        }

        if (result == null) {
            throw new IllegalStateException("Unable to solve the battle");
        }

        return result;
    }

    public GameResult playGame(List<Spell> game, boolean hardMode) {
        Contestants contestants = new Contestants(INITIAL_PLAYER_INFO, this.boss);

        List<Spell> activeSpells = new ArrayList<>(game.size());

        for (int i = 0; !game.isEmpty() || !activeSpells.isEmpty(); i++) {
            if (hardMode) {
                if (i % 2 == 0) {
                    // player turn
                    contestants = new Contestants(
                            new Fighter(contestants.player().hitPoints() - 1, contestants.player().stats()),
                            contestants.boss());
    
                    if (contestants.player().hitPoints() <= 0) {
                        return new GameResult(FightResult.BOSS_WINS, contestants);
                    }
                }
            }

            for (var it = activeSpells.listIterator(); it.hasNext();) {
                Spell spell = it.next().tick();
                it.set(spell);
                contestants = spell.apply(contestants);
                if (spell.timer() <= 0) {
                    it.remove();
                    contestants = spell.onFade(contestants);
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
                if (!game.isEmpty()) {
                    Spell spell = game.remove(0);

                    if (activeSpells.stream().anyMatch(s -> s.type() == spell.type())) {
                        return new GameResult(FightResult.INVALID, contestants);
                    }

                    activeSpells.add(spell);
                    contestants = spell.onCast(contestants);
                }
            } else {
                // boss turn
                int damage = contestants.boss().stats().damage() - contestants.player().stats().armor();
                Fighter newPlayer = new Fighter(contestants.player().hitPoints() - Math.max(1, damage),
                        contestants.player().stats());
                contestants = new Contestants(newPlayer, contestants.boss());
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

    private static boolean isValidGame(List<Spell> game, int minMana, Fighter boss, int initialPlayerMana) {
        int totalMana = game.stream() //
                .mapToInt(Spell::cost) //
                .sum();
        if (totalMana >= minMana) {
            return false;
        }

        return true;
    }

    public static Stream<List<Spell>> generateAllGames(int turns) {
        return IntStream.rangeClosed(1, turns) //
                .mapToObj(Battle::generateAllGamesWithLength) //
                .flatMap(s -> s);
    }

    private static Stream<List<Spell>> generateAllGamesWithLength(int turns) {
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
                });
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
    }

    public static record Fighter(int hitPoints, Stats stats) {
    }

    public static record Contestants(Fighter player, Fighter boss) {
    }

    public static record GameResult(FightResult result, Contestants contestants) {
    }

    public enum FightResult {
        PLAYER_WINS, BOSS_WINS, INVALID;
    }

}
