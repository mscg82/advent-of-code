package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
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

    private final Fighter boss;

    public List<Spell> findGameWithMinimalMana(int turns) {
        int minMana = Integer.MAX_VALUE;
        List<Spell> result = null;
        for (var it = generateAllGames(turns).iterator(); it.hasNext();) {
            List<Spell> game = it.next();
            if (!isValidGame(game, minMana, this.boss)){
                continue;
            }
        }
        return result;
    }

    private static boolean isValidGame(List<Spell> game, int minMana, Fighter boss) {
        int totalMana = game.stream() //
                .mapToInt(Spell::cost) //
                .sum();
        if (totalMana >= minMana) {
            return false;
        }

        int totalDamage = game.stream() //
                .mapToInt(spell -> spell.damage() * spell.timer()) //
                .sum();
        if (totalDamage < boss.hitPoints()) {
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
                            .collect(Collectors.toList());
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

}
