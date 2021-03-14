package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.mscg.Battle.Game;
import com.mscg.SpellShop.Spell;

public class AdventDay22 {

    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws IOException {
        try (BufferedReader in = readInput()) {
            var battle = Battle.parseInput(readInput());
            Game gameWithMinimalMana = battle.findGameWithMinimalMana(8, false);

            System.out.println("Part 1 - Spells %s Answer %d".formatted(
                    gameWithMinimalMana.spells().stream().map(Spell::type).collect(Collectors.toUnmodifiableList()),
                    gameWithMinimalMana.totalMana()));
        }
    }

    private static void part2() throws Exception {
        try (BufferedReader in = readInput()) {
            var battle = Battle.parseInput(readInput());
            Game gameWithMinimalMana = battle.findGameWithMinimalMana(8, true);

            System.out.println("Part 2 - Spells %s Answer %d".formatted(
                    gameWithMinimalMana.spells().stream().map(Spell::type).collect(Collectors.toUnmodifiableList()),
                    gameWithMinimalMana.totalMana()));
        }
    }

    private static BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(AdventDay22.class.getResourceAsStream("/input.txt"), StandardCharsets.UTF_8));
    }

}
