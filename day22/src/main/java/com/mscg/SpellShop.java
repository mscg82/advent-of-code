package com.mscg;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

public class SpellShop {

    @Getter
    private static final List<Spell> spells = List.of( //
            Spell.builder(SpellType.MAGIC_MISSILE, 53).damage(4).build(), //
            Spell.builder(SpellType.DRAIN, 73).damage(2).heal(2).build(), //
            Spell.builder(SpellType.SHIELD, 113).armor(7).timer(6).build(), //
            Spell.builder(SpellType.POISON, 173).damage(3).timer(6).build(), //
            Spell.builder(SpellType.RECHARGE, 229).recharge(101).timer(5).build() //
    );

    public enum SpellType {
        MAGIC_MISSILE, DRAIN, SHIELD, POISON, RECHARGE;
    }

    @Builder
    public static record Spell(SpellType type, int cost, int damage, int armor, int heal, int recharge, int timer) {

        public Spell tick() {
            return new Spell(type, cost, damage, armor, heal, recharge, timer - 1);
        }

        public static SpellBuilder builder(@NonNull SpellType type, int cost) {
            if (cost <= 0) {
                throw new IllegalArgumentException("Cost must be > 0");
            }
            return new SpellBuilder().type(type).cost(cost);
        }

    }

}
