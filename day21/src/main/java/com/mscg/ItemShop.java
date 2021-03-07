package com.mscg;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemShop {

    @Getter
    private static final List<Weapon> weapons = List.of( //
            new Weapon("Dagger", 8, 4), //
            new Weapon("Shortsword", 10, 5), //
            new Weapon("Warhammer", 25, 6), //
            new Weapon("Longsword", 40, 7), //
            new Weapon("Greataxe", 74, 8));

    @Getter
    private static final List<Armor> armors = List.of( //
            new Armor("Leather", 13, 1), //
            new Armor("Chainmail", 31, 2), //
            new Armor("Splintmail", 53, 3), //
            new Armor("Bandedmail", 75, 4), //
            new Armor("Platemail", 102, 5) //
    );

    @Getter
    private static final List<Ring> rings = List.of( //
            new Ring("Damage +1", 25, 1, 0), //
            new Ring("Damage +2", 50, 2, 0), //
            new Ring("Damage +3", 100, 3, 0), //
            new Ring("Defense +1", 20, 0, 1), //
            new Ring("Defense +2", 40, 0, 2), //
            new Ring("Defense +3", 80, 0, 3) //
    );

    public sealed interface Item {
        String name();

        int cost();
    }

    public sealed interface WithDamage {
        int damage();
    }

    public sealed interface WithArmor {
        int armor();
    }

    public static record Weapon(String name, int cost, int damage) implements Item, WithDamage {
    }

    public static record Armor(String name, int cost, int armor) implements Item, WithArmor {
    }

    public static record Ring(String name, int cost, int damage, int armor) implements Item, WithDamage, WithArmor {
    }

}
