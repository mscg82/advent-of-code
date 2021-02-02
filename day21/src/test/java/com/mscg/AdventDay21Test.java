package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay21Test {

    @Test
    public void testParse() throws Exception {
        var foodList = FoodList.parseInput(readInput());

        Assertions.assertEquals(Set.of("mxmxvkd", "kfcds", "sqjhc", "nhms"),
                foodList.getRecipes().get(0).ingredients());
        Assertions.assertEquals(Set.of("dairy", "fish"), foodList.getRecipes().get(0).allergenes());

        Assertions.assertEquals(Set.of("trh", "fvjkl", "sbzzf", "mxmxvkd"), foodList.getRecipes().get(1).ingredients());
        Assertions.assertEquals(Set.of("dairy"), foodList.getRecipes().get(1).allergenes());

        Assertions.assertEquals(Set.of("sqjhc", "fvjkl"), foodList.getRecipes().get(2).ingredients());
        Assertions.assertEquals(Set.of("soy"), foodList.getRecipes().get(2).allergenes());

        Assertions.assertEquals(Set.of("sqjhc", "mxmxvkd", "sbzzf"), foodList.getRecipes().get(3).ingredients());
        Assertions.assertEquals(Set.of("fish"), foodList.getRecipes().get(3).allergenes());
    }

    @Test
    public void testFoodMapping() throws Exception {
        var foodList = FoodList.parseInput(readInput());

        Assertions.assertEquals(Set.of("kfcds", "nhms", "sbzzf", "trh"), foodList.findFoodWithoutAllergenes());
    }

    @Test
    public void testPart1Answer() throws Exception {
        var foodList = FoodList.parseInput(readInput());

        Assertions.assertEquals(5, foodList.computePart1Answer());
    }

    @Test
    public void testPart2Answer() throws Exception {
        var foodList = FoodList.parseInput(readInput());

        Assertions.assertEquals("mxmxvkd,sqjhc,fvjkl", foodList.computePart2Answer());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
