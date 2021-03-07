package com.mscg;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mscg.ItemShop.Armor;
import com.mscg.ItemShop.Item;
import com.mscg.ItemShop.Ring;
import com.mscg.ItemShop.Weapon;
import com.mscg.ItemShop.WithArmor;
import com.mscg.ItemShop.WithDamage;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Equipment {
    private final Weapon weapon;
    private final Armor armor;
    private final Ring leftRing;
    private final Ring rigthRing;

    public Equipment(Weapon weapon) {
        this.weapon = weapon;
        this.armor = null;
        this.leftRing = null;
        this.rigthRing = null;
    }

    public Equipment withArmor(Armor armor) {
        return new Equipment(this.weapon, armor, this.leftRing, this.rigthRing);
    }

    public Equipment withLeftRing(Ring leftRing) {
        return new Equipment(this.weapon, this.armor, leftRing, this.rigthRing);
    }

    public Equipment withRightRing(Ring rigthRing) {
        return new Equipment(this.weapon, this.armor, this.leftRing, rigthRing);
    }

    public int cost() {
        return Stream.<Item>of(weapon, armor, leftRing, rigthRing) //
                .filter(Objects::nonNull) //
                .mapToInt(Item::cost) //
                .sum();
    }

    public Battle.Stats stats() {
        int damage = Stream.<WithDamage>of(this.weapon, this.leftRing, this.rigthRing) //
                .filter(Objects::nonNull) //
                .mapToInt(WithDamage::damage) //
                .sum();

        int armor = Stream.<WithArmor>of(this.armor, this.leftRing, this.rigthRing) //
                .filter(Objects::nonNull) //
                .mapToInt(WithArmor::armor) //
                .sum();

        return new Battle.Stats(damage, armor);
    }

    public static List<Equipment> generateAllEquipments() {
        return ItemShop.getWeapons().stream() //
                .map(Equipment::new) //
                .flatMap(eq -> Stream.concat(Stream.of(eq), ItemShop.getArmors().stream().map(eq::withArmor))) //
                .flatMap(eq -> Stream.concat(Stream.of(eq), ItemShop.getRings().stream().map(eq::withLeftRing))) //
                .flatMap(eq -> Stream.concat(Stream.of(eq), ItemShop.getRings().stream().map(eq::withRightRing))) //
                .collect(Collectors.toUnmodifiableList());
    }
}
