package com.mscg;

import java.util.List;
import java.util.stream.Collectors;

import com.mscg.SpellShop.Spell;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay22Test {

    @Test
    public void testGamesGeneration1() {
        List<Spell> allSpells = SpellShop.getSpells();
        List<List<Spell>> games = Battle.generateAllGames(1) //
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of( //
                List.of(allSpells.get(0)), //
                List.of(allSpells.get(1)), //
                List.of(allSpells.get(2)), //
                List.of(allSpells.get(3)), //
                List.of(allSpells.get(4)) //
        ), games);
    }

    @Test
    public void testGamesGeneration2() {
        List<Spell> allSpells = SpellShop.getSpells();
        List<List<Spell>> games = Battle.generateAllGames(2) //
                .collect(Collectors.toList());
        int i = 0;
        Assertions.assertEquals(List.of(allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(0), allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(0), allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(0), allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(0), allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(0), allSpells.get(4)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1), allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1), allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1), allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1), allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(1), allSpells.get(4)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2), allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2), allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2), allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2), allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(2), allSpells.get(4)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3), allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3), allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3), allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3), allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(3), allSpells.get(4)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4), allSpells.get(0)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4), allSpells.get(1)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4), allSpells.get(2)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4), allSpells.get(3)), games.get(i++));
        Assertions.assertEquals(List.of(allSpells.get(4), allSpells.get(4)), games.get(i++));
    }

}
