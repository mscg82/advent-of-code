package com.mscg;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class FoodList {

    private final List<Recipe> recipes;

    public Map<String, String> mapAllergenesToFood() {
        final Map<String, String> allergeneToFood = new HashMap<>();

        final Map<String, List<Set<String>>> allergeneToIngredientsSet = new HashMap<>();
        for (var recipe : recipes) {
            for (var allergene : recipe.allergenes()) {
                allergeneToIngredientsSet.computeIfAbsent(allergene, __ -> new ArrayList<>())
                        .add(recipe.ingredients());
            }
        }

        Map<String, Set<String>> allergeneToPossibileIngredients = allergeneToIngredientsSet.entrySet().stream() //
                .map(entry -> {
                    List<Set<String>> ingredientsList = entry.getValue();
                    Set<String> intersection = new HashSet<>(ingredientsList.get(0));
                    for (var ingredients : ingredientsList) {
                        intersection.retainAll(ingredients);
                    }
                    return Map.entry(entry.getKey(), intersection);
                }) //
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        while (!allergeneToPossibileIngredients.isEmpty()) {
            boolean modified = false;

            for (var it = allergeneToPossibileIngredients.entrySet().iterator(); it.hasNext();) {
                var entry = it.next();
                Set<String> possibleIngredients = entry.getValue();
                if (possibleIngredients.size() == 1) {
                    String ingredient = possibleIngredients.iterator().next();
                    it.remove();
                    allergeneToPossibileIngredients.values().forEach(ingredients -> ingredients.remove(ingredient));
                    allergeneToFood.put(entry.getKey(), ingredient);
                    modified = true;
                }
            }

            if (!modified) {
                throw new IllegalArgumentException("Failed to find mapping for allergenes " + allergeneToPossibileIngredients.keySet());
            }
        }

        return Map.copyOf(allergeneToFood);
    }

    public Set<String> findFoodWithoutAllergenes() {
        final Map<String, String> allergeneToFood = mapAllergenesToFood();

        Set<String> foodsWithAllergenes = new HashSet<>(allergeneToFood.values());

        return recipes.stream() //
                .flatMap(recipe -> recipe.ingredients().stream()) //
                .filter(ingredient -> !foodsWithAllergenes.contains(ingredient)) //
                .collect(Collectors.toSet());
    }

    public long computePart1Answer() {
        Set<String> foodWithoutAllergenes = findFoodWithoutAllergenes();
        return recipes.stream() //
                .flatMap(recipe -> recipe.ingredients().stream()) //
                .filter(foodWithoutAllergenes::contains) //
                .count();
    }

    public String computePart2Answer() {
        Map<String, String> allergeneToFood = new TreeMap<>(mapAllergenesToFood());
        return allergeneToFood.values().stream().collect(Collectors.joining(","));
    }

    public static FoodList parseInput(BufferedReader in) {
        List<Recipe> recipes = in.lines() //
                .map(Recipe::parseString) //
                .collect(Collectors.toUnmodifiableList());

        return new FoodList(recipes);
    }
}