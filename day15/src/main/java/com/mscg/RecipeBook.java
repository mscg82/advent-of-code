package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecipeBook {

    private final List<Ingredient> ingredients;

    public List<List<Proportion>> generateProportions(int totalSize) {
        List<List<Proportion>> results = new ArrayList<>();

        for (int i = totalSize; i >= 0; i--) {
            List<Proportion> recipe = i != 0 ? List.of(new Proportion(ingredients.get(0), i)) : List.of();
            List<List<Proportion>> proportions = fillProportions(recipe, ingredients.subList(1, ingredients.size()),
                    totalSize - i, totalSize);
            results.addAll(proportions);
        }
        return results;
    }

    private List<List<Proportion>> fillProportions(List<Proportion> baseRecipe, List<Ingredient> ingredients,
            int availableQuantity, int totalSize) {
        if (availableQuantity == 0) {
            return List.of(baseRecipe);
        }

        if (ingredients.size() == 1) {
            return List.of(add(baseRecipe, new Proportion(ingredients.get(0), availableQuantity)));
        }

        List<List<Proportion>> results = new ArrayList<>();
        for (int i = availableQuantity; i >= 0; i--) {
            var newRecipe = i != 0 ? add(baseRecipe, new Proportion(ingredients.get(0), i)) : baseRecipe;
            List<List<Proportion>> proportions = fillProportions(newRecipe, ingredients.subList(1, ingredients.size()),
                    availableQuantity - i, totalSize);
            results.addAll(proportions);
        }

        return results;
    }

    private static <T> List<T> add(List<T> list, T newElement) {
        var newList = new ArrayList<T>(list.size() + 1);
        newList.addAll(list);
        newList.add(newElement);
        return List.copyOf(newList);
    }

    public Recipe getBestRecipe(long wantedCalories) {

        List<List<Proportion>> allProportions = generateProportions(100);
        Recipe bestRecipe = allProportions.parallelStream() //
                .map(proportions -> {
                    int totalCapacity = 0;
                    int totalDurability = 0;
                    int totalFlavor = 0;
                    int totalTexture = 0;
                    long totalCalories = 0L;
                    for (Proportion proportion : proportions) {
                        totalCapacity += proportion.quantity() * proportion.ingredient().capacity();
                        totalDurability += proportion.quantity() * proportion.ingredient().durability();
                        totalFlavor += proportion.quantity() * proportion.ingredient().flavor();
                        totalTexture += proportion.quantity() * proportion.ingredient().texture();
                        totalCalories += proportion.quantity() * proportion.ingredient().calories();
                    }
                    long score = totalCapacity < 0 || totalDurability < 0 || totalFlavor < 0 || totalTexture < 0 ? 0
                            : totalCapacity * totalDurability * totalFlavor * totalTexture;
                    return new Recipe(proportions, score, totalCalories);
                }) //
                .filter(recipe -> wantedCalories == 0 || recipe.calories() == wantedCalories) //
                .max(Comparator.comparingLong(Recipe::score)) //
                .orElseThrow();

        return bestRecipe;
    }

    public static RecipeBook parseInput(BufferedReader in) throws IOException {
        List<Ingredient> ingredients = in.lines() //
                .map(Ingredient::fromString) //
                .collect(Collectors.toUnmodifiableList());
        return new RecipeBook(ingredients);
    }

    public static record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {

        public static Ingredient fromString(String line) {
            var pattern = Pattern.compile(
                    "^(.+?): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)$");
            var matcher = pattern.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Unable to parse line " + line);
            }
            return new Ingredient(matcher.group(1), Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
        }

    }

    public static record Proportion(Ingredient ingredient, int quantity) {
    }

    public static record Recipe(List<Proportion> proportions, long score, long calories) {
    }

}
