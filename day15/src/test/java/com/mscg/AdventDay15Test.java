package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mscg.RecipeBook.Ingredient;
import com.mscg.RecipeBook.Proportion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay15Test {

    @Test
    public void testParse() throws Exception {
        var recipeBook = RecipeBook.parseInput(readInput());

        Assertions.assertEquals(List.of( //
                new Ingredient("Butterscotch", -1, -2, 6, 3, 8), //
                new Ingredient("Cinnamon", 2, 3, -2, -1, 3) //
        ), recipeBook.getIngredients());
    }

    @Test
    public void testGenerateProportions() throws Exception {
        var recipeBook = RecipeBook.parseInput(readInput());
        List<List<Proportion>> proportions = recipeBook.generateProportions(100);

        Assertions.assertTrue(proportions.stream() //
                .flatMap(List::stream) //
                .noneMatch(p -> p.quantity() == 0), "No ingredient should have quantity 0");

        Assertions.assertEquals(101, proportions.size());

        Assertions.assertTrue(proportions.stream() //
                .mapToInt(p -> p.stream().mapToInt(Proportion::quantity).sum()) //
                .noneMatch(s -> s != 100), "No recipe should have a total size less than 100");
    }

    @Test
    public void testBestRecipe() throws Exception {
        var recipeBook = RecipeBook.parseInput(readInput());
        var recipe = recipeBook.getBestRecipe();

        Map<String, Integer> ingredientToQuantity = recipe.proportions().stream() //
                .collect(Collectors.toMap(p -> p.ingredient().name(), p -> p.quantity()));
        Assertions.assertEquals(Map.of( //
                "Butterscotch", 44, //
                "Cinnamon", 56 //
        ), ingredientToQuantity);

        Assertions.assertEquals(62842880L, recipe.score());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
