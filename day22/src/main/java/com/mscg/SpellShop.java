package com.mscg;

import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

public class SpellShop {

    @Getter
    private static final List<Spell> spells = List.of( //
            Spell.builder("Magic Missile", 53).damage(4).build(), //
            Spell.builder("Drain", 73).damage(2).heal(2).build(), //
            Spell.builder("Shield", 113).armor(7).timer(6).build(), //
            Spell.builder("Poison", 173).damage(3).timer(6).build(), //
            Spell.builder("Recharge", 229).recharge(101).timer(5).build() //
    );

    @Builder
    public static record Spell(String name, int cost, int damage, int armor, int heal, int recharge, int timer) {

        public Optional<Spell> tick() {
            return Optional
                    .ofNullable(timer <= 1 ? null : new Spell(name, cost, damage, armor, heal, recharge, timer - 1));
        }

        public static SpellBuilder builder(@NonNull String name, int cost) {
            if (cost <= 0) {
                throw new IllegalArgumentException("Cost must be > 0");
            }
            return new SpellBuilder().name(name).cost(cost);
        }

    }

}
