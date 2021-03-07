package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Battle {

    private final Fighter boss;

    public Equipment findWinningEquipment() {
        return Equipment.generateAllEquipments().stream() //
                .filter(eq -> {
                    var playerStats = eq.stats();
                    var bossStats = boss.stats();

                    int playerDamage = Math.max(1, playerStats.damage() - bossStats.armor());
                    int bossDamage = Math.max(1, bossStats.damage() - playerStats.armor());

                    int playerTurns = (int) Math.ceil(((double) boss.hitPoints()) / playerDamage);
                    int bossTurns = (int) Math.ceil(100.0 / bossDamage);

                    return playerTurns <= bossTurns;
                }) //
                .sorted(Comparator.comparingInt(Equipment::cost)) //
                .findFirst() //
                .orElseThrow();
    }

    public static Battle parseInput(BufferedReader in) throws IOException {
        var boss = new Fighter(getNumber(in.readLine()), //
                new Stats(getNumber(in.readLine()), getNumber(in.readLine())));
        return new Battle(boss);
    }

    private static int getNumber(String line) {
        int index = line.indexOf(':');
        return Integer.parseInt(line.substring(index + 1).trim());
    }

    public static record Stats(int damage, int armor) {
    }

    public static record Fighter(int hitPoints, Stats stats) {
    }

}
